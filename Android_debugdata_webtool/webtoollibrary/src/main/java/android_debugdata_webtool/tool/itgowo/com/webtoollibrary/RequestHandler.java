/*
 *
 *  *    Copyright (C) 2016 Amit Shekhar
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.RowDataRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.TableDataResponse;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.UpdateRowResponse;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.Constants;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseFileProvider;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseHelper;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.PrefHelper;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.Utils;

/**
 * Created by amitshekhar on 06/02/17.
 */

public class RequestHandler {

    private final Context mContext;
    private final AssetManager mAssets;
    private boolean isDbOpened;
    private SQLiteDatabase mDatabase;
    private HashMap<String, File> mDatabaseFiles = new HashMap<>();
    ;
    private HashMap<String, File> mCustomDatabaseFiles;
    private String mSelectedDatabase = null;

    public RequestHandler(Context context) {
        mContext = context;
        mAssets = context.getResources().getAssets();
        mDatabaseFiles = DatabaseFileProvider.getDatabaseFiles(mContext);
        if (mCustomDatabaseFiles != null) {
            mDatabaseFiles.putAll(mCustomDatabaseFiles);
        }
    }

    /**
     * 多线程处理
     *
     * @param mSocket
     */
    public void asynHandle(final Socket mSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    syncHandle(mSocket);
                } catch (IOException mE) {
                    DebugDataTool.onError("web server:received request error,分配并处理数据异常", mE);
                }
            }
        }).start();
    }

    public void syncHandle(Socket socket) throws IOException {
        InputStream mInputStream = null;
        PrintStream output = null;
        try {
            int count = 0;
            StringBuilder mStringBuilder = new StringBuilder();
            mInputStream = socket.getInputStream();
            byte[] mBytes = new byte[4096];
            while (true) {
                count = mInputStream.read(mBytes);
                if (count > 0) {
                    mStringBuilder.append(new String(mBytes, 0, count)).append("\r\n");
                }
                if (count < 4096) {
                    break;
                }
            }
            HttpRequest mHttpRequest = null;
            try {
                mHttpRequest = HttpRequest.parser(mStringBuilder.toString().trim());
            } catch (Exception mE) {
                DebugDataTool.onError("web server:RequestHandler error,http请求解析异常", mE);
            }
            if (mHttpRequest == null) {
                return;
            }
            // Output stream that we send the response to
            output = new PrintStream(socket.getOutputStream());

            boolean isFile = false;
            byte[] bytes = new byte[0];
            String mAction = null;
            if (mHttpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
                output.println("HTTP/1.0 200 OK");
                output.println("Access-Control-Allow-Origin: *");
                output.println("Access-Control-Allow-Methods: *");
                output.println("Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept");
                output.println("Access-Control-Max-Age: Origin, 3600");

            } else {
                if (TextUtils.isEmpty(mHttpRequest.getPath())) {
                    if (TextUtils.isEmpty(mHttpRequest.getBody())) {//index.html
                        isFile = true;
                        mHttpRequest.setPath("index.html");
                        bytes = Utils.loadContent("index.html", mAssets);
                    } else {
                        //post请求数据
                        Request mRequest = null;
                        try {
                            mRequest = DebugDataTool.JsonToObject(mHttpRequest.getBody(), Request.class);
                        } catch (Exception mE) {
                            DebugDataTool.onError("web server:Request error,http请求解析异常", mE);
                            onServerError(output, "web server:Request error,http请求解析异常 " + mE.getMessage());
                            output.flush();
                            return;
                        }
                        if (mRequest == null || TextUtils.isEmpty(mRequest.getAction())) {
                            DebugDataTool.onError("web server:Request data is null or action is null,http请求没有action，无法解析操作", new Throwable("action is null"));
                            onServerError(output, "web server:Request data is null or action is null,http请求没有action，无法解析操作");
                            output.flush();
                            return;
                        }
                        mAction = mRequest.getAction();
                        switch (mRequest.getAction()) {
                            case "getDbList":
                                bytes = DebugDataTool.ObjectToJson(getDBList()).getBytes();
                                break;
                            case "getSpList":
                                bytes = DebugDataTool.ObjectToJson(getSPList()).getBytes();
                                break;
                            case "getTableList":
                                bytes = DebugDataTool.ObjectToJson(getTableList(mRequest.getDatabase())).getBytes();
                                break;
                            case "getDataFromDbTable":
                                bytes = DebugDataTool.ObjectToJson(getAllDataFromTheTableResponse(mRequest.getTableName(), mRequest.getPageIndex(), mRequest.getPageSize())).getBytes();
                                break;
                            case "addTableData":
                                bytes = addTableDataAndGetResponse(mHttpRequest.getPath()).getBytes();
                                break;
                            case "updateTableData":
                                bytes = updateTableDataAndGetResponse(mHttpRequest.getPath()).getBytes();
                                break;
                            case "deleteTableData":
                                bytes = deleteTableDataAndGetResponse(mHttpRequest.getPath()).getBytes();
                                break;
                            case "query":
                                bytes = executeQueryAndGetResponse(mHttpRequest.getRequestURI()).getBytes();
                                break;
                            case "downloadDb":
                                isFile = true;
                                bytes = Utils.getDatabase(mSelectedDatabase, mDatabaseFiles);
                                break;
                            case "downloadSp":
                                isFile = true;
                                bytes = Utils.getSharedPreferences(mSelectedDatabase, PrefHelper.getSharedPreference(mContext));
                                break;
                        }
                    }
                } else {
                    //文件请求
                    isFile = true;
                    bytes = Utils.loadContent(mHttpRequest.getPath(), mAssets);
                }
                if (!isFile) {
                    DebugDataTool.onRequest(mStringBuilder.toString(), mHttpRequest);
                    DebugDataTool.onResponse(new String(bytes));
                    if (null == bytes) {
                        onServerError(output, "");
                        output.flush();
                        return;
                    }
                } else {
                    DebugDataTool.onRequest(mHttpRequest.getPath(), mHttpRequest);
                    if (null == bytes) {
                        output.println("HTTP/1.0 404 Not Found");
                        output.println("Content-Type: application/json");
                        output.println("access-control-allow-origin: *");
                        output.println();
                        output.println(new Response().setCode(Response.code_FileNotFound).setMsg("请求的资源不存在").toJson());
                        output.println();
                        output.flush();
                        return;
                    }
                }
                output.println("HTTP/1.0 200 OK");
                output.println("Content-Type: " + Utils.detectMimeType(mHttpRequest.getPath()));
                output.println("access-control-allow-origin: *");

                if (!TextUtils.isEmpty(mAction) && (mAction.equalsIgnoreCase("downloadDb") || mAction.equalsIgnoreCase("downloadSp"))) {
                    output.println("Content-Disposition: attachment; filename=" + mSelectedDatabase);
                } else {
                    output.println("Content-Length: " + bytes.length);
                }
            }
            output.println();
            output.write(bytes);
            output.flush();
            output.close();
            socket.close();
        } finally {
            try {
                if (null != output) {
                    output.close();
                }
                if (null != mInputStream) {
                    mInputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                DebugDataTool.onError("web server:close request error,http请求解析结束处理异常", e);
            }
        }

    }

    private void onServerError(PrintStream output, String msg) {
        output.println("HTTP/1.0 500 Internal Server Error");
        output.println("Content-Type: application/json");
        output.println("access-control-allow-origin: *");
        output.println();
        output.println(new Response().setCode(Response.code_Error).setMsg("服务器异常  " + msg).toJson());
        output.println();
    }

    public void setCustomDatabaseFiles(HashMap<String, File> customDatabaseFiles) {
        mCustomDatabaseFiles = customDatabaseFiles;
    }


    private void openDatabase(String database) {
        closeDatabase();
        File databaseFile = mDatabaseFiles.get(database);
        mDatabase = SQLiteDatabase.openOrCreateDatabase(databaseFile.getAbsolutePath(), null);
        isDbOpened = true;
    }

    private void closeDatabase() {
        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
        }
        mDatabase = null;
        isDbOpened = false;
    }

    /**
     * 获取数据库列表
     *
     * @return
     */
    private Response getDBList() {
        mDatabaseFiles = DatabaseFileProvider.getDatabaseFiles(mContext);
        if (mCustomDatabaseFiles != null) {
            mDatabaseFiles.putAll(mCustomDatabaseFiles);
        }
        Response response = new Response();
        if (mDatabaseFiles != null) {
            List<String> dblist = new ArrayList<>(mDatabaseFiles.keySet());
            Iterator<String> mIterator = dblist.iterator();
            while (mIterator.hasNext()) {
                if (mIterator.next().contains("-journal")) {
                    mIterator.remove();
                }
            }
            response.setDbList(dblist);
        }
        return response;
    }

    /**
     * 获取共享参数列表
     *
     * @return
     */
    private Response getSPList() {
        Response response = new Response();
        response.setSpList(PrefHelper.getSharedPreferenceTags(mContext));
        return response;
    }

    private Response getAllDataFromTheTableResponse(String tableName, Integer pageindex, Integer pagesize) {
        if (tableName == null || tableName.length() < 1) {
            return null;
        }
        if (pageindex == null || pageindex < 1) {
            pageindex = 1;
        }
        if (pagesize == null || pagesize < 1) {
            pagesize = 10;
        }
        Response response = null;
        if (isDbOpened) {
            String sql = "SELECT * FROM " + tableName + " limit " + (pageindex - 1) * pagesize + "," + pagesize;
            response = DatabaseHelper.getTableData(mDatabase, sql, tableName);
        } else {
//            response = PrefHelper.getAllPrefData(mContext, tableName);
        }

        return response;

    }

    private String executeQueryAndGetResponse(String route) {
        String query = null;
        String data = null;
        String first;
        try {
            if (route.contains("?query=")) {
                query = route.substring(route.indexOf("=") + 1, route.length());
            }
            try {
                query = URLDecoder.decode(query, "UTF-8");
            } catch (Exception e) {
                DebugDataTool.onError("web server:executeQueryAndGetResponse error,参数处理异常，不是utf-8编码", e);
            }

            if (query != null) {
                first = query.split(" ")[0].toLowerCase();
                if (first.equals("select") || first.equals("pragma")) {
                    Response response = DatabaseHelper.getTableData(mDatabase, query, null);
                    data = DebugDataTool.ObjectToJson(response);
                } else {
                    TableDataResponse response = DatabaseHelper.exec(mDatabase, query);
                    data = DebugDataTool.ObjectToJson(response);
                }
            }
        } catch (Exception e) {
            DebugDataTool.onError("web server:executeQueryAndGetResponse error,参数处理异常", e);
        }

        if (data == null) {
            Response response = new Response();
            response.setCode(Response.code_SQLNODATA);
            response.setMsg("找不到数据");
            data = DebugDataTool.ObjectToJson(response);
        }

        return data;
    }

    private Response getTableList(String database) {
        if (database == null || database.length() < 1) {
            return null;
        }
        Response response = new Response();
        if (Constants.APP_SHARED_PREFERENCES.equals(database)) {
            response.setSpList(PrefHelper.getSharedPreferenceTags(mContext));

            closeDatabase();
            mSelectedDatabase = Constants.APP_SHARED_PREFERENCES;
        } else {
            openDatabase(database);
            response = DatabaseHelper.getAllTableName(mDatabase);
            mSelectedDatabase = database;
        }
        return response;
    }


    private String addTableDataAndGetResponse(String route) {
        UpdateRowResponse response;
        try {
            Uri uri = Uri.parse(URLDecoder.decode(route, "UTF-8"));
            String tableName = uri.getQueryParameter("tableName");
            String updatedData = uri.getQueryParameter("addData");
            List<RowDataRequest> rowDataRequests = DebugDataTool.JsonToObject(updatedData, Request.class).getRowDataRequests();
            if (Constants.APP_SHARED_PREFERENCES.equals(mSelectedDatabase)) {
                response = PrefHelper.addOrUpdateRow(mContext, tableName, rowDataRequests);
            } else {
                response = DatabaseHelper.addRow(mDatabase, tableName, rowDataRequests);
            }
            return DebugDataTool.ObjectToJson(response);
        } catch (Exception e) {
            DebugDataTool.onError("web server:addTableDataAndGetResponse error,参数处理异常", e);
            response = new UpdateRowResponse();
            response.isSuccessful = false;
            return DebugDataTool.ObjectToJson(response);
        }
    }

    private String updateTableDataAndGetResponse(String route) {
        UpdateRowResponse response;
        try {
            Uri uri = Uri.parse(URLDecoder.decode(route, "UTF-8"));
            String tableName = uri.getQueryParameter("tableName");
            String updatedData = uri.getQueryParameter("updatedData");
            List<RowDataRequest> rowDataRequests = DebugDataTool.JsonToObject(updatedData, Request.class).getRowDataRequests();
            if (Constants.APP_SHARED_PREFERENCES.equals(mSelectedDatabase)) {
                response = PrefHelper.addOrUpdateRow(mContext, tableName, rowDataRequests);
            } else {
                response = DatabaseHelper.updateRow(mDatabase, tableName, rowDataRequests);
            }
            return DebugDataTool.ObjectToJson(response);
        } catch (Exception e) {
            DebugDataTool.onError("web server:updateTableDataAndGetResponse error,参数处理异常", e);
            response = new UpdateRowResponse();
            response.isSuccessful = false;
            return DebugDataTool.ObjectToJson(response);
        }
    }


    private String deleteTableDataAndGetResponse(String route) {
        UpdateRowResponse response;
        try {
            Uri uri = Uri.parse(URLDecoder.decode(route, "UTF-8"));
            String tableName = uri.getQueryParameter("tableName");
            String updatedData = uri.getQueryParameter("deleteData");
            List<RowDataRequest> rowDataRequests = DebugDataTool.JsonToObject(updatedData, Request.class).getRowDataRequests();
            if (Constants.APP_SHARED_PREFERENCES.equals(mSelectedDatabase)) {
                response = PrefHelper.deleteRow(mContext, tableName, rowDataRequests);
            } else {
                response = DatabaseHelper.deleteRow(mDatabase, tableName, rowDataRequests);
            }
            return DebugDataTool.ObjectToJson(response);
        } catch (Exception e) {
            DebugDataTool.onError("web server:deleteTableDataAndGetResponse error,参数处理异常", e);
            response = new UpdateRowResponse();
            response.isSuccessful = false;
            return DebugDataTool.ObjectToJson(response);
        }
    }

}

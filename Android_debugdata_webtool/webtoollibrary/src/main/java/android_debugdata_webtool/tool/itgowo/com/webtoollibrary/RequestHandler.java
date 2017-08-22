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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.httpParser.HttpRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.Request.RowDataRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.Response;
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
    public void asynchronousHandle(final Socket mSocket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    syncHandle(mSocket);
                } catch (IOException mE) {
                    DebugDataTool.onError("web server:boot error,分配并处理数据异常", mE);
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
                mStringBuilder.append(new String(mBytes, 0, count)).append("\r\n");
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
            if (mHttpRequest.getPath() != null) {
                if (mHttpRequest.getPath().equalsIgnoreCase("getDbList")) {
                    final String response = getDBListResponse();
                    bytes = response.getBytes();
                } else if (mHttpRequest.getPath().equalsIgnoreCase("getAllDataFromTheTable")) {
                    final String response = getAllDataFromTheTableResponse(mHttpRequest.getParameter().get("tableName"));
                    bytes = response.getBytes();
                } else if (mHttpRequest.getPath().equalsIgnoreCase("getTableList")) {
                    final String response = getTableListResponse(mHttpRequest.getParameter().get("database"));
                    bytes = response.getBytes();
                } else if (mHttpRequest.getPath().equalsIgnoreCase("addTableData")) {
                    final String response = addTableDataAndGetResponse(mHttpRequest.getPath());
                    bytes = response.getBytes();
                } else if (mHttpRequest.getPath().equalsIgnoreCase("updateTableData")) {
                    final String response = updateTableDataAndGetResponse(mHttpRequest.getPath());
                    bytes = response.getBytes();
                } else if (mHttpRequest.getPath().equalsIgnoreCase("deleteTableData")) {
                    final String response = deleteTableDataAndGetResponse(mHttpRequest.getPath());
                    bytes = response.getBytes();
                } else if (mHttpRequest.getPath().equalsIgnoreCase("query")) {
                    final String response = executeQueryAndGetResponse(mHttpRequest.getPath());
                    bytes = response.getBytes();
                } else if (mHttpRequest.getPath().equalsIgnoreCase("downloadDb")) {
                    isFile = true;
                    if (Constants.APP_SHARED_PREFERENCES.equals(mSelectedDatabase)) {
                        bytes = Utils.getSharedPreferences(mSelectedDatabase, PrefHelper.getSharedPreference(mContext));
                    } else {
                        bytes = Utils.getDatabase(mSelectedDatabase, mDatabaseFiles);
                    }
                } else if (mHttpRequest.getPath().isEmpty()) {
                    isFile = true;
                    bytes = Utils.loadContent("index.html", mAssets);
                } else {
                    isFile = true;
                    bytes = Utils.loadContent(mHttpRequest.getPath(), mAssets);
                }


            }
            if (!isFile) {
                DebugDataTool.onRequest(mStringBuilder.toString(), mHttpRequest);
                DebugDataTool.onResponse(new String(bytes));
            } else {
                DebugDataTool.onRequest(mHttpRequest.getPath(), mHttpRequest);
            }
            if (null == bytes) {
                writeServerError(output);
                return;
            }

            output.println("HTTP/1.0 200 OK");

                output.println("Content-Type: " + Utils.detectMimeType(mHttpRequest.getPath()));

            if (  mHttpRequest.getPath().equalsIgnoreCase("downloadDb")) {
                output.println("Content-Disposition: attachment; filename=" + mSelectedDatabase);
            } else {
                output.println("Content-Length: " + bytes.length);
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

    public void setCustomDatabaseFiles(HashMap<String, File> customDatabaseFiles) {
        mCustomDatabaseFiles = customDatabaseFiles;
    }

    private void writeServerError(PrintStream output) {
        output.println("HTTP/1.0 500 Internal Server Error");
        output.flush();
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

    private String getDBListResponse() {
        mDatabaseFiles = DatabaseFileProvider.getDatabaseFiles(mContext);
        if (mCustomDatabaseFiles != null) {
            mDatabaseFiles.putAll(mCustomDatabaseFiles);
        }
        Response response = new Response();
        if (mDatabaseFiles != null) {
            for (HashMap.Entry<String, File> entry : mDatabaseFiles.entrySet()) {
                response.rows.add(entry.getKey());
            }
        }
        response.rows.add(Constants.APP_SHARED_PREFERENCES);
        response.isSuccessful = true;
        return DebugDataTool.ObjectToJson(response);
    }

    private String getAllDataFromTheTableResponse(String tableName) {
        TableDataResponse response;
        if (isDbOpened) {
            String sql = "SELECT * FROM " + tableName;
            response = DatabaseHelper.getTableData(mDatabase, sql, tableName);
        } else {
            response = PrefHelper.getAllPrefData(mContext, tableName);
        }

        return DebugDataTool.ObjectToJson(response);

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
                    TableDataResponse response = DatabaseHelper.getTableData(mDatabase, query, null);
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
            response.isSuccessful = false;
            data = DebugDataTool.ObjectToJson(response);
        }

        return data;
    }

    private String getTableListResponse(String database) {
        Response response = new Response();
        if (Constants.APP_SHARED_PREFERENCES.equals(database)) {
            response.getRows().addAll(PrefHelper.getSharedPreferenceTags(mContext));
            response.setSuccessful(true);
            closeDatabase();
            mSelectedDatabase = Constants.APP_SHARED_PREFERENCES;
        } else {
            openDatabase(database);
            response = DatabaseHelper.getAllTableName(mDatabase);
            mSelectedDatabase = database;
        }
        return DebugDataTool.ObjectToJson(response);
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

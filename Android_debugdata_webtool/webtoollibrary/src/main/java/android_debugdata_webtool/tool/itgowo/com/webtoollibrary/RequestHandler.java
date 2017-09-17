

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

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request.RowDataRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.Constants;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseHelper;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.PrefHelper;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.Utils;

/**
 * Created by lujianchao on 2017/8/22.
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
        getDatabaseFiles(context);
    }

    public HashMap<String, File> getDatabaseFiles(Context context) {
        mDatabaseFiles = new HashMap<>();
        try {
            for (String databaseName : context.databaseList()) {
                mDatabaseFiles.put(databaseName, context.getDatabasePath(databaseName));
            }
            if (mCustomDatabaseFiles != null) {
                mDatabaseFiles.putAll(mCustomDatabaseFiles);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDatabaseFiles;
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
        if (mDatabaseFiles == null) {
            getDatabaseFiles(mContext);
        }
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

            byte[] bytes = new byte[0];
            String mAction = null;
            if (mHttpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
                onRequestOptions(output);

            } else if (mHttpRequest.getMethod().equalsIgnoreCase("POST")) {
                Response mResponse = onRequestPost(output, mHttpRequest);
                if (mResponse != null) {
                    bytes = DebugDataTool.ObjectToJson(mResponse).getBytes();
                }
                DebugDataTool.onRequest(mStringBuilder.toString(), mHttpRequest);
                DebugDataTool.onResponse(new String(bytes));
            } else if (mHttpRequest.getMethod().equalsIgnoreCase("GET")) {
                if (TextUtils.isEmpty(mHttpRequest.getPath())) {//index.html
                    mHttpRequest.setPath("index.html");
                }
                if (mHttpRequest.getPath().startsWith("downloadDb")) {
                    bytes = Utils.getDatabase(mSelectedDatabase, mDatabaseFiles);
                } else if (mHttpRequest.getPath().startsWith("downloadSp")) {
                    bytes = Utils.getSharedPreferences(mSelectedDatabase, PrefHelper.getSharedPreference(mContext));
                } else {
                    //文件请求
                    bytes = Utils.loadContent(mHttpRequest.getPath(), mAssets);
                    DebugDataTool.onRequest(mHttpRequest.getPath(), mHttpRequest);
                    if (null == bytes) {
                        output.println("HTTP/1.1 404 Not Found");
                        output.println("Content-Type: application/json");
                        output.println("access-control-allow-origin: *");
                        output.println();
                        output.println(new Response().setCode(Response.code_FileNotFound).setMsg("请求的资源不存在").toJson());
                        output.println();
                        output.flush();
                        return;
                    }
                }
            }
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: " + Utils.detectMimeType(mHttpRequest.getPath()));
            output.println("access-control-allow-origin: *");
            if (!TextUtils.isEmpty(mAction) && (mAction.equalsIgnoreCase("downloadDb") || mAction.equalsIgnoreCase("downloadSp"))) {
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

    private Response onRequestPost(PrintStream output, HttpRequest mHttpRequest) {
        //post请求数据
        Request mRequest = null;
        try {
            mRequest = DebugDataTool.JsonToObject(mHttpRequest.getBody(), Request.class);
        } catch (Exception mE) {
            DebugDataTool.onError("web server:Request error,http请求解析异常", mE);
            onServerError(output, "web server:Request error,http请求解析异常 " + mE.getMessage());
            output.flush();
            return null;
        }
        if (mRequest == null || TextUtils.isEmpty(mRequest.getAction())) {
            DebugDataTool.onError("web server:Request data is null or action is null,http请求没有action，无法解析操作", new Throwable("action is null"));
            onServerError(output, "web server:Request data is null or action is null,http请求没有action，无法解析操作");
            output.flush();
            return null;
        }

        switch (mRequest.getAction()) {
            case "getDbList":
                return getDBList();
            case "getSpList":
                return getSPList();
            case "getTableList":
                return getTableList(mRequest.getDatabase());
            case "getDataFromDbTable":
                return getAllDataFromDbTable(mRequest.getDatabase(), mRequest.getTableName(), mRequest.getPageIndex(), mRequest.getPageSize());
            case "getDataFromSpFile":
                return getAllDataFromSpFile(mRequest.getSpFileName());
            case "addTableData":
                return addTableDataAndGetResponse(mHttpRequest.getPath());
            case "updateTableData":
                return updateTableDataAndGetResponse(mHttpRequest.getPath());
            case "deleteTableData":
                return deleteTableDataAndGetResponse(mHttpRequest.getPath());
            case "query":
                return executeQueryAndGetResponse(mHttpRequest.getRequestURI());
        }
        return null;
    }

    /**
     * 响应跨域请求
     *
     * @param output
     */
    private void onRequestOptions(PrintStream output) {
        output.println("HTTP/1.0 200 OK");
        output.println("Access-Control-Allow-Methods: *");
        output.println("Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept");
        output.println("Access-Control-Max-Age: Origin, 3600");
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
        if (databaseFile == null || !databaseFile.exists()) {
            isDbOpened = true;
            return;
        }
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
        getDatabaseFiles(mContext);
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
        closeDatabase();
        mSelectedDatabase = Constants.APP_SHARED_PREFERENCES;
        return response;
    }

    /**
     * 从数据库表读取数据
     *
     * @param database
     * @param tableName
     * @param pageindex
     * @param pagesize
     * @return
     */
    private Response getAllDataFromDbTable(String database, String tableName, Integer pageindex, Integer pagesize) {
        if (tableName == null || tableName.length() < 1) {
            return null;
        }
        Response response = null;
        openDatabase(database);
        if (isDbOpened) {
            String sql;
            if (pageindex == null || pagesize == null) {
                sql = "SELECT * FROM " + tableName;
            } else {
                sql = "SELECT * FROM " + tableName + " limit " + (pageindex - 1) * pagesize + "," + pagesize;
            }
            response = DatabaseHelper.getTableData(mDatabase, sql, tableName);
        } else {
            response = new Response().setCode(Response.code_FileNotFound).setMsg("数据库文件不存在，请检查是否做了删除操作");
        }
        return response;
    }

    /**
     * 从共享参数文件获取数据
     *
     * @param filename
     * @return
     */
    private Response getAllDataFromSpFile(String filename) {
        if (filename == null || filename.length() < 1) {
            return null;
        }
        Response response = PrefHelper.getAllPrefData(mContext, filename);
        return response;
    }

    private Response executeQueryAndGetResponse(String route) {
        String query = null;
        Response response = null;
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
                    response = DatabaseHelper.getTableData(mDatabase, query, null);

                } else {
                    response = DatabaseHelper.exec(mDatabase, query);
                }
            }
        } catch (Exception e) {
            DebugDataTool.onError("web server:executeQueryAndGetResponse error,参数处理异常", e);
        }

        if (response == null) {
            response = new Response();
            response.setCode(Response.code_SQLNODATA);
            response.setMsg("找不到数据");

        }

        return response;
    }

    private Response getTableList(String database) {
        if (database == null || database.length() < 1) {
            return null;
        }
        Response response = new Response();
        openDatabase(database);
        response = DatabaseHelper.getAllTableName(mDatabase);
        mSelectedDatabase = database;
        return response;
    }


    private Response addTableDataAndGetResponse(String route) {
        Response response = new Response();
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
            return response;
        } catch (Exception e) {
            DebugDataTool.onError("web server:addTableDataAndGetResponse error,参数处理异常", e);
            response.setCode(Response.code_Error).setMsg("web server:addTableDataAndGetResponse error,参数处理异常  " + e.getMessage());
            return response;
        }
    }

    private Response updateTableDataAndGetResponse(String route) {
        Response response;
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
            return response;
        } catch (Exception e) {
            DebugDataTool.onError("web server:updateTableDataAndGetResponse error,参数处理异常", e);
            response = new Response();
            response.setCode(Response.code_Error).setMsg("web server:updateTableDataAndGetResponse error,参数处理异常  " + e.getMessage());
            return response;
        }
    }


    private Response deleteTableDataAndGetResponse(String route) {
        Response response;
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
            return response;
        } catch (Exception e) {
            DebugDataTool.onError("web server:deleteTableDataAndGetResponse error,参数处理异常", e);
            response = new Response();
            response.setCode(Response.code_Error).setMsg("web server:deleteTableDataAndGetResponse error,参数处理异常  " + e);
            return response;
        }
    }

}

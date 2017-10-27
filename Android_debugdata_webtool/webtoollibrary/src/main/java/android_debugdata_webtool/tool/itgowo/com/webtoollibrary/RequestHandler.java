

package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private HashMap<String, File> mCustomDatabaseFiles;
    private ExecutorService mExecutorService= Executors.newFixedThreadPool(5);
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
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    syncHandle(mSocket);
                } catch (IOException mE) {
                    DebugDataTool.onError("web server:received request error,分配并处理数据异常", mE);
                }
            }
        });
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
            if (mHttpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
                onRequestOptions(output);

            } else if (mHttpRequest.getMethod().equalsIgnoreCase("POST")) {
                Response mResponse = onRequestPost(output, mHttpRequest);
                DebugDataTool.onRequest(mStringBuilder.toString(), mHttpRequest);
                if (mResponse != null) {
                    bytes = DebugDataTool.ObjectToJson(mResponse).getBytes();
                }
                DebugDataTool.onResponse(new String(bytes));
                output.println("HTTP/1.1 200 OK");
                output.println("Content-Type: " + Utils.detectMimeType(mHttpRequest.getPath()));
                output.println("access-control-allow-origin: *");
                output.println("Content-Length: " + bytes.length);
                output.println();
                output.write(bytes);
                output.flush();
                output.close();
            } else if (mHttpRequest.getMethod().equalsIgnoreCase("GET")) {
                if (TextUtils.isEmpty(mHttpRequest.getPath())) {//index.html
                    mHttpRequest.setPath("index.html");
                }
                File mFile = null;
                //文件请求
                if (mHttpRequest.getPath().equals("downloadFile")) {
                    mFile = new File(mHttpRequest.getParameter().get("downloadFile"));
                    if (mFile.exists()) {
                        bytes = Utils.getFile(new File(mHttpRequest.getParameter().get("downloadFile")));
                    }
                } else {
                    bytes = Utils.loadContent(mHttpRequest.getPath(), mAssets);
                    mFile = new File(mHttpRequest.getPath());
                }
                DebugDataTool.onRequest(mHttpRequest.getPath(), mHttpRequest);
                if (null == bytes) {
                    output.println("HTTP/1.1 404 Not Found");
                    output.println("Content-Type: application/json");
                    output.println("access-control-allow-origin: *");
                    output.println();
                    output.println(new Response().setCode(Response.code_FileNotFound).setMsg("请求的资源不存在").toJson());
                    output.println();

                } else {
                    output.println("HTTP/1.1 200 OK");
                    output.println("Content-Type: " + Utils.detectMimeType(mFile.getName()));
                    output.println("access-control-allow-origin: *");
                    if (!mHttpRequest.getPath().equals("index.html")) {
                        output.println("Content-Disposition: attachment; filename = " + mFile.getName());
                    } else {
                        output.println("Content-Disposition: filename = " + mFile.getName());
                    }
                    output.println("Content-Length: " + bytes.length);
                    output.println();
                    output.write(bytes);
                    output.println();
                }

                output.flush();
                output.close();
            }

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
            case "addDataToDb":
                return addData(mRequest, true);
            case "addDataToSp":
                return addData(mRequest, false);
            case "updateDataToDb":
                return updateData(mRequest, true);
            case "updateDataToSp":
                return updateData(mRequest, false);
            case "deleteDataFromDb":
                return deleteData(mRequest, true);
            case "deleteDataFromSp":
                return deleteData(mRequest, false);
            case "query":
                return executeQuery(mRequest);
            case "getFileList":
                return getFileList(mRequest);
            case "deleteFile":
                return deleteFile(mRequest);
        }
        return null;
    }

    private Response deleteFile(Request mRequest) {
        if (mRequest.getData() == null || mRequest.getData().length() < 5) {
            return new Response().setCode(Response.code_Error).setMsg("没有指定文件");
        }
        File mFile = new File(mRequest.getData());
        if (mFile == null || !mFile.exists()) {
            return new Response().setCode(Response.code_FileNotFound).setMsg("文件不存在");
        }
        if (mFile.delete()) {
            return new Response();
        } else {
            return new Response().setCode(Response.code_Error).setMsg("文件删除失败");
        }
    }

    private synchronized Response getFileList(Request mRequest) {

        File root = null;
        if (mRequest.getData() == null || mRequest.getData().length() < 5) {
            root = new File(mContext.getApplicationInfo().dataDir);
        } else {
            root = new File(mRequest.getData());
        }
        if (root == null || !root.exists()) {
            return new Response().setCode(Response.code_FileNotFound).setMsg("请求的目录或文件不存在");
        }

        List<Response.FileList.FileData> mFileDatas = new ArrayList<>();
        List<Response.FileList.FileColumn> mFileColumns = new ArrayList<>();
        mFileColumns.add(new Response.FileList.FileColumn().setTitle("文件名").setRootPath(root.getPath()).setData("fileName"));
        mFileColumns.add(new Response.FileList.FileColumn().setTitle("文件大小").setRootPath(root.getPath()).setData("fileSize"));
        mFileColumns.add(new Response.FileList.FileColumn().setTitle("最后编辑时间").setRootPath(root.getPath()).setData("fileTime"));
//        mFileColumns.add(new Response.FileList.FileColumn().setTitle("文件夹").setRootPath(root.getPath()).setData("dir"));
        mFileColumns.add(new Response.FileList.FileColumn().setTitle("操作").setRootPath(root.getPath()).setData("delete"));
        File[] files = root.listFiles();
        if (files != null && files.length != 0) {
            for (File f : files) {
                Response.FileList.FileData mFileData = new Response.FileList.FileData();
                mFileData.setIsDir(f.isDirectory());
                mFileData.setFileName(f.getName());
                mFileData.setRootPath(root.getPath().equalsIgnoreCase(mContext.getApplicationInfo().dataDir) ? null : root.getParent());
                mFileData.setFileSize(f.isDirectory() ? "" : Utils.formatFileSize(mContext, f.length(), false));
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                mFileData.setFileTime(mSimpleDateFormat.format(f.lastModified()));
                mFileData.setPath(f.getPath());
                mFileData.setDelete(f.canExecute());
                mFileDatas.add(mFileData);
            }
        }
        Response mResponse = new Response().setFileList(new Response.FileList().setFileList(mFileDatas).setFileColumns(mFileColumns));
        return mResponse;
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
        output.println("access-control-allow-origin: *");
        output.println("Content-Length: " + 0);
        output.println();
        output.flush();
        output.close();
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


    private synchronized void openDatabase(String database) {
        if (mDatabase != null && mDatabase.isOpen() && new File(mDatabase.getPath()).getName().equals(database)) {
            isDbOpened = true;
        } else {
            closeDatabase();
            File databaseFile = mDatabaseFiles.get(database);
            if (databaseFile == null || !databaseFile.exists()) {
                isDbOpened = false;
                return;
            }
            mDatabase = SQLiteDatabase.openOrCreateDatabase(databaseFile.getAbsolutePath(), null);
            isDbOpened = true;
        }
    }

    private synchronized void closeDatabase() {
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
    private synchronized Response getDBList() {
        getDatabaseFiles(mContext);
        Response response = new Response();
        if (mDatabaseFiles != null) {
            List<Response.FileList.FileData> dblist = new ArrayList<>();
            for (Map.Entry<String, File> mStringFileEntry : mDatabaseFiles.entrySet()) {
                if (!mStringFileEntry.getKey().contains("-journal")) {
                    dblist.add(new Response.FileList.FileData().setFileName(mStringFileEntry.getKey()).setPath(mStringFileEntry.getValue().getPath()));
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
    private synchronized Response getSPList() {
        Response response = new Response();
        response.setSpList(PrefHelper.getSharedPreferenceTags(mContext));
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
    private synchronized Response getAllDataFromDbTable(String database, String tableName, Integer pageindex, Integer pagesize) {
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
    private synchronized Response getAllDataFromSpFile(String filename) {
        if (filename == null || filename.length() < 1) {
            return null;
        }
        Response response = PrefHelper.getAllPrefData(mContext, filename);
        return response;
    }

    private synchronized Response executeQuery(Request mRequest) {
        Response response = null;
        String first;
        try {
            if (mRequest.getData() != null) {
                openDatabase(mRequest.getDatabase());
                first = mRequest.getData().split(" ")[0].toLowerCase();
                if (first.equals("select") || first.equals("pragma")) {
                    response = DatabaseHelper.getTableData(mDatabase, mRequest.getData(), null);
                } else {
                    response = DatabaseHelper.exec(mDatabase, mRequest.getData());
                }
            }
        } catch (Exception e) {
            DebugDataTool.onError("web server:executeQueryAndGetResponse error,参数处理异常", e);
        }
        if (response == null) {
            response = new Response().setCode(Response.code_SQLNODATA).setMsg("找不到数据或者SQL语句错误");
        }
        return response;
    }

    private synchronized Response getTableList(String database) {
        if (database == null || database.length() < 1) {
            return null;
        }
        Response response = new Response();
        openDatabase(database);
        response = DatabaseHelper.getAllTableName(mDatabase);

        return response;
    }

    /**
     * 添加数据
     *
     * @param mRequest
     * @param isDatabase
     * @return
     */
    private synchronized Response addData(Request mRequest, boolean isDatabase) {
        Response response = null;
        try {
            if (isDatabase) {
                openDatabase(mRequest.getDatabase());
                response = DatabaseHelper.addRow(mDatabase, mRequest.getTableName(), mRequest.getRowDataRequests());
            } else {
                response = PrefHelper.addOrUpdateRow(mContext, mRequest.getSpFileName(), mRequest.getRowDataRequests());
            }
            return response;
        } catch (Exception e) {
            DebugDataTool.onError("web server:addData error,参数处理异常", e);
            if (response == null) {
                response = new Response();
            }
            response.setCode(Response.code_Error).setMsg("web server:addData error,参数处理异常  " + e.getMessage());
            return response;
        }
    }

    /**
     * 更新数据
     *
     * @param mRequest
     * @param isDatabase
     * @return
     */
    private synchronized Response updateData(Request mRequest, boolean isDatabase) {
        Response response = new Response();
        try {
            if (isDatabase) {
                response = DatabaseHelper.updateRow(mDatabase, mRequest.getTableName(), mRequest.getRowDataRequests());
            } else {
                response = PrefHelper.addOrUpdateRow(mContext, mRequest.getSpFileName(), mRequest.getRowDataRequests());
            }
            return response;
        } catch (Exception e) {
            DebugDataTool.onError("web server:updateData error,参数处理异常", e);
            if (response == null) {
                response = new Response();
            }
            response.setCode(Response.code_Error).setMsg("web server:updateData error,参数处理异常  " + e.getMessage());
            return response;
        }
    }

    /**
     * 删除数据
     *
     * @param mRequest
     * @return
     */
    private synchronized Response deleteData(Request mRequest, boolean isDatabase) {
        Response response = null;
        try {
            if (isDatabase) {
                openDatabase(mRequest.getDatabase());
                response = DatabaseHelper.deleteRow(mDatabase, mRequest.getTableName(), mRequest.getRowDataRequests());
            } else {
                response = PrefHelper.deleteRow(mContext, mRequest.getSpFileName(), mRequest.getRowDataRequests());
            }
            return response;
        } catch (Exception e) {
            DebugDataTool.onError("web server:deleteData error,参数处理异常", e);
            if (response == null) {
                response = new Response();
            }
            response.setCode(Response.code_Error).setMsg("web server:deleteData error,参数处理异常  " + e);
            return response;
        }
    }
}



package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.Action;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionAddDataToDb;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionAddDataToSp;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionDeleteDataFromDb;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionDeleteDataFromSp;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionDeleteFile;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionGetDataFromDbTable;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionGetDataFromSpFile;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionGetDbList;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionGetFileList;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionGetSpList;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionGetTableList;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionQuery;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionUpdateDataToDb;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action.ActionUpdateDataToSp;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.Utils;

/**
 * Created by lujianchao on 2017/8/22.
 */

public class RequestHandler {
    private final Context context;
    private final AssetManager assetManager;
    private HashMap<String, Action> dispatcher = new HashMap<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    public RequestHandler(Context context) {
        this.context = context;
        assetManager = context.getResources().getAssets();
        dispatcher.put(ActionGetDbList.ACTION, new ActionGetDbList());
        dispatcher.put(ActionGetSpList.ACTION, new ActionGetSpList());
        dispatcher.put(ActionGetTableList.ACTION, new ActionGetTableList());
        dispatcher.put(ActionGetDataFromDbTable.ACTION, new ActionGetDataFromDbTable());
        dispatcher.put(ActionGetDataFromSpFile.ACTION, new ActionGetDataFromSpFile());
        dispatcher.put(ActionAddDataToDb.ACTION, new ActionAddDataToDb());
        dispatcher.put(ActionAddDataToSp.ACTION, new ActionAddDataToSp());
        dispatcher.put(ActionUpdateDataToDb.ACTION, new ActionUpdateDataToDb());
        dispatcher.put(ActionUpdateDataToSp.ACTION, new ActionUpdateDataToSp());
        dispatcher.put(ActionDeleteDataFromDb.ACTION, new ActionDeleteDataFromDb());
        dispatcher.put(ActionDeleteDataFromSp.ACTION, new ActionDeleteDataFromSp());
        dispatcher.put(ActionQuery.ACTION, new ActionQuery());
        dispatcher.put(ActionGetFileList.ACTION, new ActionGetFileList());
        dispatcher.put(ActionDeleteFile.ACTION, new ActionDeleteFile());
    }


    /**
     * 多线程处理
     *
     * @param socket
     */
    public void asynHandle(final Socket socket) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    syncHandle(socket);
                } catch (IOException mE) {
                    DebugDataTool.onError("web server:received request error,分配并处理数据异常", mE);
                }
            }
        });
    }

    public void syncHandle(Socket socket) throws IOException {
        InputStream inputStream = null;
        PrintStream printStream = null;
        try {
            int count = 0;
            StringBuilder stringBuilder = new StringBuilder();
            inputStream = socket.getInputStream();
            byte[] bytes1 = new byte[4096];
            while (true) {
                count = inputStream.read(bytes1);
                if (count > 0) {
                    stringBuilder.append(new String(bytes1, 0, count)).append("\r\n");
                }
                if (count < 4096) {
                    break;
                }
            }
            HttpRequest httpRequest = null;
            try {
                httpRequest = HttpRequest.parser(stringBuilder.toString().trim());
            } catch (Exception mE) {
                DebugDataTool.onError("web server:RequestHandler error,http请求解析异常", mE);
            }
            if (httpRequest == null) {
                return;
            }
            // Output stream that we send the response to
            printStream = new PrintStream(socket.getOutputStream());
            if (httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
                onRequestOptions(printStream);
            } else if (httpRequest.getMethod().equalsIgnoreCase("POST")) {
                DebugDataTool.onRequest(stringBuilder.toString(), httpRequest);
                onRequestPost(httpRequest, printStream);
            } else if (httpRequest.getMethod().equalsIgnoreCase("GET")) {
                onRequestGet(httpRequest, printStream);
            }
            socket.close();
        } finally {
            try {
                if (null != printStream) {
                    printStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                DebugDataTool.onError("web server:close request error,http请求解析结束处理异常", e);
            }
        }

    }

    private void onRequestGet(HttpRequest httpRequest, PrintStream printStream) throws IOException {
        byte[] bytes = null;
        if (TextUtils.isEmpty(httpRequest.getPath())) {//index.html
            httpRequest.setPath("index.html");
        }
        File file = null;
        //文件请求
        if (httpRequest.getPath().equals("downloadFile")) {
            file = new File(httpRequest.getParameter().get("downloadFile"));
            if (file.exists()) {
                bytes = Utils.getFile(new File(httpRequest.getParameter().get("downloadFile")));
            }
        } else {
            bytes = Utils.loadContent(httpRequest.getPath(), assetManager);
            file = new File(httpRequest.getPath());
        }
        DebugDataTool.onRequest(httpRequest.getPath(), httpRequest);
        if (null == bytes) {
            printStream.println("HTTP/1.1 404 Not Found");
            printStream.println("Content-Type: application/json");
            printStream.println("access-control-allow-origin: *");
            printStream.println();
            printStream.println(new Response().setCode(Response.code_FileNotFound).setMsg("请求的资源不存在").toJson());
            printStream.println();

        } else {
            printStream.println("HTTP/1.1 200 OK");
            printStream.println("Content-Type: " + Utils.detectMimeType(file.getName()));
            printStream.println("access-control-allow-origin: *");
            if (!httpRequest.getPath().equals("index.html")) {
                printStream.println("Content-Disposition: attachment; filename = " + file.getName());
            } else {
                printStream.println("Content-Disposition: filename = " + file.getName());
            }
            printStream.println("Content-Length: " + bytes.length);
            printStream.println();
            printStream.write(bytes);
            printStream.println();
        }
        printStream.flush();
        printStream.close();
    }

    private void onRequestPost(HttpRequest httpRequest, PrintStream printStream) throws IOException {
        Response response = null;
        //post请求数据
        Request request = null;
        try {
            request = DebugDataTool.JsonToObject(httpRequest.getBody(), Request.class);
        } catch (Exception e) {
            String msg = "web server:Request error,http请求解析异常 ";
            DebugDataTool.onError(msg, e);
            response = new Response().setCode(Response.code_Error).setMsg(msg + e.getMessage());
        }
        if (response == null) {
            if (request == null || TextUtils.isEmpty(request.getAction())) {
                String msg = "web server:Request data is null or action is null,http请求没有action，无法解析操作";
                DebugDataTool.onError(msg, new Throwable("action is null"));
                response = new Response().setCode(Response.code_Error).setMsg(msg);
            }
        }
        if (response == null) {
            try {
                Action action = dispatcher.get(request.getAction());
                if (action != null) {
                    response = action.doAction(context, request);
                }
            } catch (Exception e) {
                String msg = "web server:action error(" + request.getAction() + "),参数处理异常";
                DebugDataTool.onError(msg, e);
                response = new Response().setCode(Response.code_Error).setMsg(msg + e.getMessage());
            }
        }
        byte[] bytes = new byte[0];
        if (response != null) {
            bytes = DebugDataTool.ObjectToJson(response).getBytes();
            DebugDataTool.onResponse(new String(bytes));
            onServerPostOK(printStream, Utils.detectMimeType(httpRequest.getPath()), bytes);
        }else {
            onServerError(printStream,"not found action");
        }

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

    private void onServerPostOK(PrintStream printStream, String contentType, byte[] bytes) throws IOException {
        printStream.println("HTTP/1.1 200 OK");
        printStream.println("Content-Type: " + contentType);
        printStream.println("access-control-allow-origin: *");
        printStream.println("Content-Length: " + bytes.length);
        printStream.println();
        printStream.write(bytes);
        printStream.flush();
        printStream.close();
    }

    private void onServerError(PrintStream output, String msg) {
        output.println("HTTP/1.0 500 Internal Server Error");
        output.println("Content-Type: application/json");
        output.println("access-control-allow-origin: *");
        output.println();
        output.println(new Response().setCode(Response.code_Error).setMsg("服务器异常  " + msg).toJson());
        output.println();
    }
}

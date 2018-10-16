package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;

import java.io.IOException;
import java.io.PrintStream;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.Utils;

public class ResponseHandler {
    private PrintStream printStream;

    public ResponseHandler(PrintStream printStream) {
        this.printStream = printStream;
    }

    /**
     * 响应跨域请求
     *
     */
    public void onRequestOptions() {
        printStream.println("HTTP/1.0 200 OK");
        printStream.println("Access-Control-Allow-Methods: *");
        printStream.println("Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept");
        printStream.println("Access-Control-Max-Age: Origin, 3600");
        printStream.println("access-control-allow-origin: *");
        printStream.println("Content-Length: " + 0);
        printStream.println();
        printStream.flush();
        printStream.close();
    }

    public void onServerPostOK( String contentType, byte[] bytes) throws IOException {
        printStream.println("HTTP/1.1 200 OK");
        printStream.println("Content-Type: " + contentType);
        printStream.println("access-control-allow-origin: *");
        printStream.println("Content-Length: " + bytes.length);
        printStream.println();
        printStream.write(bytes);
        printStream.flush();
    }

    public void onServerError(String msg) {
        printStream.println("HTTP/1.0 500 Internal Server Error");
        printStream.println("Content-Type: application/json");
        printStream.println("access-control-allow-origin: *");
        printStream.println();
        printStream.println(new Response().setCode(Response.code_Error).setMsg("服务器异常  " + msg).toJson());
        printStream.println();
        printStream.flush();
    }
    public void onServerPostError(String msg) {
        printStream.println("HTTP/1.1 200 OK");
        printStream.println("Content-Type: application/json");
        printStream.println("access-control-allow-origin: *");
        printStream.println();
        printStream.println(new Response().setCode(Response.code_Error).setMsg(msg).toJson());
        printStream.println();
        printStream.flush();
    }
    public void onServerNotFound(String msg){
        printStream.println("HTTP/1.1 404 Not Found");
        printStream.println("Content-Type: application/json");
        printStream.println("access-control-allow-origin: *");
        printStream.println();
        printStream.println(new Response().setCode(Response.code_FileNotFound).setMsg(msg).toJson());
        printStream.println();
        printStream.flush();
    }

    /**
     *
     * @param isAttachment 是否是附件下载
     * @param fileName
     * @param contentType
     * @param bytes
     * @throws IOException
     */
    public void onServerGetFile(boolean isAttachment,String fileName,String contentType,byte[] bytes) throws IOException {
        printStream.println("HTTP/1.1 200 OK");
        printStream.println("Content-Type: " + contentType);
        printStream.println("Cache-Control: max-age=3600");
        printStream.println("access-control-allow-origin: *");
        if (isAttachment) {
            printStream.println("Content-Disposition: attachment; filename = " + fileName);
        } else {
            printStream.println("Content-Disposition: filename = " + fileName);
        }
        printStream.println("Content-Length: " + bytes.length);
        printStream.println();
        printStream.write(bytes);
        printStream.println();
        printStream.flush();
    }
    public void sendPost(Object response) {
        byte[] bytes=null;
        if (response==null){
            onServerError("not found action");
        }else {
            bytes = DebugDataTool.ObjectToJson(response).getBytes();
            DebugDataTool.onResponse(new String(bytes));
            try {
                onServerPostOK(Utils.JSON, bytes);
            } catch (IOException e) {
                e.printStackTrace();
                onServerPostError(e.getLocalizedMessage());
            }
        }
    }
}

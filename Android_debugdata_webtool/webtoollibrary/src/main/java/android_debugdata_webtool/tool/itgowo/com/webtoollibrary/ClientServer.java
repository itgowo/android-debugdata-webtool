package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;

/**
 * Created by lujianchao on 17/8/16.
 */


import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ClientServer implements Runnable {

    private static final String TAG = "ClientServer";

    private int port;

    private boolean inRunning;
    /**
     * 是否使用多线程模式
     */
    private boolean isMultMode;

    private ServerSocket serverSocket;

    private final RequestHandler requestHandler;

    public ClientServer(Context context, int port, boolean isMultMode) {
        this.isMultMode = isMultMode;
        requestHandler = new RequestHandler(context);
        this.port = port;
    }

    public void start() {
        inRunning = true;
        new Thread(this).start();
    }

    public void resetServerPort(int newport) throws IOException, InterruptedException {
        stop();
        Thread.sleep(1000);
        port = newport;
        start();
    }

    public void stop() {
        try {
            inRunning = false;
            if (null != serverSocket) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new Socket("localhost", port).close();
                        } catch (IOException mE) {
                            mE.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            DebugDataTool.onError("web server error,服务器关闭异常", e);
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            DebugDataTool.onSystemMsg("服务器已启动");
            while (inRunning) {
                Socket socket = serverSocket.accept();
                if (inRunning) {
                    if (isMultMode) {
                        requestHandler.asynHandle(socket);
                    } else {
                        requestHandler.syncHandle(socket);
                    }
                }

            }
            serverSocket.close();
            serverSocket = null;
            DebugDataTool.onSystemMsg("服务器已关闭");
        } catch (Exception e) {
            DebugDataTool.onError("web server error", e);
        }
    }

    public void setCustomDatabaseFiles(HashMap<String, File> customDatabaseFiles) {
        DatabaseManager.setCustomDatabaseFiles(customDatabaseFiles);
    }

    public boolean isRunning() {
        return inRunning;
    }
}

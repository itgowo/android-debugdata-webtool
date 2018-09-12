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

    private int mPort;

    private boolean mIsRunning;
    /**
     * 是否使用多线程模式
     */
    private boolean isMultMode;

    private ServerSocket mServerSocket;

    private final RequestHandler mRequestHandler;

    public ClientServer(Context context, int port, boolean isMultMode) {
        this.isMultMode = isMultMode;
        mRequestHandler = new RequestHandler(context);
        mPort = port;
    }

    public void start() {
        mIsRunning = true;
        new Thread(this).start();
    }

    public void resetServerPort(int newport) throws IOException, InterruptedException {
        stop();
        Thread.sleep(1000);
        mPort = newport;
        start();
    }

    public void stop() {
        try {
            mIsRunning = false;
            if (null != mServerSocket) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new Socket("localhost", mPort).close();
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
            mServerSocket = new ServerSocket(mPort);
            DebugDataTool.onSystemMsg("服务器已启动");
            while (mIsRunning) {
                Socket socket = mServerSocket.accept();
                if (mIsRunning) {
                    if (isMultMode) {
                        mRequestHandler.asynHandle(socket);
                    } else {
                        mRequestHandler.syncHandle(socket);
                    }
                }

            }
            mServerSocket.close();
            mServerSocket = null;
            DebugDataTool.onSystemMsg("服务器已关闭");
        } catch (Exception e) {
            DebugDataTool.onError("web server error", e);
        }
    }

    public void setCustomDatabaseFiles(HashMap<String, File> customDatabaseFiles) {
        DatabaseManager.setCustomDatabaseFiles(customDatabaseFiles);
    }

    public boolean isRunning() {
        return mIsRunning;
    }
}

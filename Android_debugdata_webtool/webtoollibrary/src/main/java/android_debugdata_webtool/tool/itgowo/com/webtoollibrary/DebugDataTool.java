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
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.NetworkUtils;

/**
 * Created by lujianchao on 2017/8/22.
 */

public class DebugDataTool {

    private static final String TAG = DebugDataTool.class.getSimpleName();
    public static final String WARNING_JSON = "not found fastjson,not found Gson,onDebugToolListener is null,如果使用默认方式实现Json工具的方式，请在主工程中使用fastjson或者Gson库，或者实现自定义Json方法，不然无法处理Json，此工具内部不集成任何第三方，绿色无公害(ˇˍˇ) 想～";
    private static final int DEFAULT_PORT = 8080;
    private static ClientServer clientServer;

    private static String addressLog = "not available";
    private static onDebugToolListener mToolListener;
    private static Class mFastJson = null;
    private static Object mGsonJson = null;
    private static boolean isFastJson = true;
    private static Method mJsonMethodToJsonString = null;
    private static Method mJsonMethodToJsonObject = null;


    private DebugDataTool() {
    }

    protected static String ObjectToJson(Object mO) {
        if (mO != null) {
            if (mToolListener == null) {
                if (isFastJson) {
                    try {
                        return (String) mJsonMethodToJsonString.invoke(null, mO);
                    } catch (IllegalAccessException mE) {
                        mE.printStackTrace();
                    } catch (InvocationTargetException mE) {
                        mE.printStackTrace();
                    }
                } else {
                    try {
                        return (String) mJsonMethodToJsonString.invoke(mGsonJson, mO);
                    } catch (IllegalAccessException mE) {
                        mE.printStackTrace();
                    } catch (InvocationTargetException mE) {
                        mE.printStackTrace();
                    }
                }
            }
            return mToolListener.onObjectToJson(mO);
        }
        return "";
    }

    protected static <T> T JsonToObject(String mJsonString, Class<T> mClass) {
        if (mJsonString != null && mClass != null) {
            if (mToolListener == null) {
                if (isFastJson) {
                    try {
                        return (T) mJsonMethodToJsonObject.invoke(null, mJsonString, mClass);
                    } catch (IllegalAccessException mE) {
                        mE.printStackTrace();
                    } catch (InvocationTargetException mE) {
                        mE.printStackTrace();
                    }
                } else {
                    try {
                        return (T) mJsonMethodToJsonObject.invoke(mGsonJson, mJsonString, mClass);
                    } catch (IllegalAccessException mE) {
                        mE.printStackTrace();
                    } catch (InvocationTargetException mE) {
                        mE.printStackTrace();
                    }
                }
            }
            return mToolListener.onJsonStringToObject(mJsonString, mClass);
        }
        return null;
    }

    protected static void onRequest(String mS, HttpRequest mHttpRequest) {
        if (mS == null || mS.equals("")) {
            return;
        }
        if (mToolListener == null) {
            Log.d(TAG, mS + "\r\n" + mHttpRequest);
        } else {
            mToolListener.onGetRequest(mS, mHttpRequest);
        }
    }


    protected static void onResponse(String mS) {
        if (mToolListener == null) {
            Log.d(TAG, mS);
        } else {
            mToolListener.onResponse(mS);
        }
    }

    public synchronized static void initialize(Context context, int mPortNumber, boolean isMultMode, onDebugToolListener mOnDebugToolListener) {
        if (clientServer != null  ) {
            try {
                clientServer.resetServerPort(mPortNumber);
            } catch (IOException mE) {
                mE.printStackTrace();
            } catch (InterruptedException mE) {
                mE.printStackTrace();
            }
        } else {
            mToolListener = mOnDebugToolListener;
            int portNumber;
            if (mPortNumber < 10) {
                portNumber = DEFAULT_PORT;
            } else {
                portNumber = mPortNumber;
            }
            if (mOnDebugToolListener == null) {
                onSystemMsg("未设置onDebugToolListener，自动搜索当前APP内使用的Json工具，目前支持FastJson和Gson");
                if (!searchJsonTool()) {
                    return;
                }
            }
            clientServer = new ClientServer(context, portNumber, isMultMode);
            clientServer.start();
            addressLog = NetworkUtils.getAddressLog(context, portNumber);
            Log.d(TAG, "Open http://" + addressLog + " in your browser");
            Log.d(TAG, "请用浏览器打开 http://" + addressLog);
            DebugDataTool.onSystemMsg("请用浏览器打开 http://" + addressLog);
        }
//        System.out.println(TAG + "   请用浏览器打开 http://" + addressLog);
    }

    /**
     * 用反射检查APP内集成的Json工具。
     *
     * @return 是否找到并初始化
     */
    protected static boolean searchJsonTool() {
        try {
            mFastJson = Class.forName("com.alibaba.fastjson.JSON");
            isFastJson = true;
            mJsonMethodToJsonString = mFastJson.getMethod("toJSONString", Object.class);
            mJsonMethodToJsonObject = mFastJson.getMethod("parseObject", String.class, Class.class);
        } catch (ClassNotFoundException mE) {

        } catch (NoSuchMethodException mE) {

        }
        if (mFastJson == null || mJsonMethodToJsonString == null || mJsonMethodToJsonObject == null) {
            isFastJson = false;
            try {
                Class mGsonClass = Class.forName("com.google.gson.Gson");
                mGsonJson = mGsonClass.newInstance();
                mJsonMethodToJsonObject = mGsonClass.getDeclaredMethod("fromJson", String.class, Class.class);
                mJsonMethodToJsonString = mGsonClass.getDeclaredMethod("toJson", Object.class);
            } catch (ClassNotFoundException mE) {

            } catch (NoSuchMethodException mE) {
                mE.printStackTrace();
            } catch (IllegalAccessException mE) {
                mE.printStackTrace();
            } catch (InstantiationException mE) {
                mE.printStackTrace();
            }
        }
        if (mFastJson == null && mGsonJson == null || mJsonMethodToJsonString == null || mJsonMethodToJsonObject == null) {
            onError("警告", new Throwable(WARNING_JSON));
            onSystemMsg(WARNING_JSON);
            return false;
        }
        return true;

    }

    protected static void onSystemMsg(String mS) {
        if (mToolListener == null) {
            Log.i(TAG, mS);
        } else {
            mToolListener.onSystemMsg(mS);
        }
    }

    protected static void onError(String mTip, Throwable mThrowable) {
        if (mToolListener == null) {
            Log.e(TAG, mTip, mThrowable);
        } else {
            mToolListener.onError(mTip, mThrowable);
        }
    }

    public static String getAddressLog() {
        Log.d(TAG, addressLog);
        return addressLog;
    }

    public static void shutDown() {
        if (clientServer != null) {
            clientServer.stop();
            clientServer = null;
        }
    }

    /**
     * 指定appdata之外的可读数据库文件
     *
     * @param customDatabaseFiles
     */
    public static void setCustomDatabaseFiles(HashMap<String, File> customDatabaseFiles) {
        if (clientServer != null) {
            clientServer.setCustomDatabaseFiles(customDatabaseFiles);
        }
    }

    public static boolean isServerRunning() {
        return clientServer != null && clientServer.isRunning();
    }

}

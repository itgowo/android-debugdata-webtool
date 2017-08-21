package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.httpParser.HttpRequest;

/**
 * Created by hnvfh on 2017/8/17.
 */

public interface onDebugToolListener {
    String onObjectToJson(Object mObject);

    <T> T onJsonStringToObject(String mJsonString, Class<T> mClass);

    /**
     * 收到的所有请求
     *
     * @param mRequest
     */
    void onGetRequest(String mRequest,HttpRequest mHttpRequest);

    /**
     * 只返回请求操作数据，不返回文件
     *
     * @param mResponse
     */
    void onResponse(String mResponse);
}

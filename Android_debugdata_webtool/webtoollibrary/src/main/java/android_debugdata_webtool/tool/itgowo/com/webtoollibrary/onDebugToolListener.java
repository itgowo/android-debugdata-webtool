package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;

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
    void onGetRequest(String mRequest, HttpRequest mHttpRequest);

    /**
     * 只返回请求操作数据，不返回文件
     *
     * @param mResponse
     */
    void onResponse(String mResponse);

    /**
     * 异常
     *
     * @param mTip
     * @param mThrowable
     */
    void onError(String mTip, Throwable mThrowable);
}

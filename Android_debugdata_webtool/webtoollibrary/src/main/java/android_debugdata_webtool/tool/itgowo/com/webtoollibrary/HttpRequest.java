package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hnvfh on 2017/8/21.
 */

public class HttpRequest {
    private static final String TAG = "HttpRequest";
    private String method;// 请求方法
    private String protocol;// 协议版本
    private String requestURI = "";//请求的URI地址  在HTTP请求的第一行的请求方法后面
    private String path = "";//path地址
    private String body;//body信息

    public static String Accept_Charset = "Accept-Charset";//请求的字符编码  对应HTTP请求中的Accept-Charset
    public static String Connection = "Connection";//Http请求连接状态信息 对应HTTP请求中的Connection
    public static String Host = "Host";//请求的主机信息
    public static String User_Agent = "User-Agent";// 代理，用来标识代理的浏览器信息 ,对应HTTP请求中的User-Agent:
    public static String Accept_Language = "Accept-Language";//对应Accept-Language
    public static String Accept_Encoding = "Accept-Encoding";//请求的编码方式  对应HTTP请求中的Accept-Encoding

    private Map<String, String> mParameter = new HashMap<>();//get请求附加参数
    private Map<String, String> mHeaders = new HashMap<>();//head信息


    private HttpRequest() {
    }


    public static HttpRequest parser(String mHttp) throws Exception {
        if (mHttp == null || mHttp.isEmpty() || mHttp.length() < 5) {
            DebugDataTool.onError(TAG, new Throwable("http parser: 数据不对，http报文不对"));
            return null;
        }
        HttpRequest mHttpRequest = new HttpRequest();
        int position = mHttp.indexOf("\r\n\r\n");//查找是否有连续两个换行
        if (position > -1) {
            parserHeader(mHttp, mHttpRequest, position);
            mHttpRequest.setBody(mHttp.substring(position+4,mHttp.length()));
        } else {
            String[] mStrings = mHttp.split("\r\n");
            if (mStrings != null && mStrings.length > 0 && mStrings[0].contains("HTTP/1.1")) {
                parserHeader(mHttp, mHttpRequest, mHttp.trim().length());
            } else {
                mHttpRequest.setBody(mHttp);
            }
        }


        return mHttpRequest;
    }

    private static void parserHeader(String mHttp, HttpRequest mHttpRequest, int position) {
        String[] heads = mHttp.substring(0, position).trim().split("\r\n");
        if (heads != null && heads.length > 1) {
            String[] mRequestLine = heads[0].split(" ");
            mHttpRequest.setMethod(mRequestLine[0]);
            mHttpRequest.setProtocol(mRequestLine[2]);
            String[] mP = mRequestLine[1].split("\\?");
            if (mP != null && mP.length > 0) {
                mHttpRequest.setRequestURI(mRequestLine[1]);
                mHttpRequest.setPath(mP[0].isEmpty() ? "" : mP[0].substring(1));
                if (mP.length > 1) {
                    String[] mParameter1 = mP[1].split("&");
                    if (mParameter1 != null) {
                        for (int mI = 0; mI < mParameter1.length; mI++) {
                            String[] mS = mParameter1[mI].split("=");
                            if (mS != null && mS.length == 2) {
                                mHttpRequest.mParameter.put(mS[0], mS[1]);
                            }
                        }
                    }
                }
            }

            for (int mI = 0; mI < (heads.length - 1); mI++) {
                String[] head = heads[mI + 1].split(":");
                if (head == null || head.length != 2) {
                    continue;
                }
                mHttpRequest.mHeaders.put(head[0], head[1]);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public HttpRequest setMethod(String mMethod) {
        method = mMethod;
        return this;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public HttpRequest setHeaders(Map<String, String> mHeaders) {
        this.mHeaders = mHeaders;
        return this;
    }

    public Map<String, String> getParameter() {
        return mParameter;
    }

    public HttpRequest setParameter(Map<String, String> mParameter) {
        this.mParameter = mParameter;
        return this;
    }

    public String getPath() {
        return path;
    }

    public HttpRequest setPath(String mPath) {
        path = mPath;
        return this;
    }

    public String getProtocol() {
        return protocol;
    }

    public HttpRequest setProtocol(String mProtocol) {
        protocol = mProtocol;
        return this;
    }

    public String getBody() {
        return body;
    }

    public HttpRequest setBody(String mBody) {
        body = mBody;
        return this;
    }


    public String getRequestURI() {
        return requestURI;
    }

    public HttpRequest setRequestURI(String mRequestURI) {
        requestURI = mRequestURI;
        return this;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method='" + method + '\'' +
                ", requestURI='" + requestURI + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}

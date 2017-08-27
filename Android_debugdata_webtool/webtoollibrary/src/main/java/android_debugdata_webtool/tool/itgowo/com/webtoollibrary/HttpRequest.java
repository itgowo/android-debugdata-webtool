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
    private String host;//请求的主机信息
    private String Connection;//Http请求连接状态信息 对应HTTP请求中的Connection
    private String agent;// 代理，用来标识代理的浏览器信息 ,对应HTTP请求中的User-Agent:
    private String language;//对应Accept-Language
    private String encoding;//请求的编码方式  对应HTTP请求中的Accept-Encoding
    private String charset;//请求的字符编码  对应HTTP请求中的Accept-Charset
    private String body;//body信息
    private Map<String, String> mParameter = new HashMap<>();


    private HttpRequest() {
    }

    public static HttpRequest parser(String mHttp) throws Exception {
        if (mHttp == null || mHttp.isEmpty() || mHttp.length() < 5) {
            DebugDataTool.onError(TAG,new Throwable("http parser: 数据不对，http报文不对"));
            return null;
        }
        HttpRequest mHttpRequest = new HttpRequest();
        String[] mStrings = mHttp.split("\r\n\r\n");
        String[] heads = new String[0];
        if (mStrings != null && mStrings.length > 0) {
            if (mStrings.length == 2) {
                mHttpRequest.setBody(mStrings[1]);
            }
            heads = mStrings[0].split("\r\n");
        }
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
        }
        for (int mI = 0; mI < (heads.length - 1); mI++) {
            String[] head = heads[mI + 1].split(":");
            if (head == null || head.length != 2) {
                continue;
            }
            if (head[0].equalsIgnoreCase("User-Agent")) {
                mHttpRequest.setAgent(head[1]);
            } else if (head[0].equalsIgnoreCase("Host")) {
                mHttpRequest.setHost(head[1]);
            } else if (head[0].equalsIgnoreCase("Accept-Charset")) {
                mHttpRequest.setCharset(head[1]);
            } else if (head[0].equalsIgnoreCase("Accept-Encoding")) {
                mHttpRequest.setEncoding(head[1]);
            } else if (head[0].equalsIgnoreCase("Connection")) {
                mHttpRequest.setConnection(head[1]);
            } else if (head[0].equalsIgnoreCase("Accept-Language")) {
                mHttpRequest.setLanguage(head[1]);

            }
        }


        return mHttpRequest;
    }

    public String getMethod() {
        return method;
    }

    public HttpRequest setMethod(String mMethod) {
        method = mMethod;
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

    public String getHost() {
        return host;
    }

    public HttpRequest setHost(String mHost) {
        host = mHost;
        return this;
    }

    public String getConnection() {
        return Connection;
    }

    public HttpRequest setConnection(String mConnection) {
        Connection = mConnection;
        return this;
    }

    public String getAgent() {
        return agent;
    }

    public HttpRequest setAgent(String mAgent) {
        agent = mAgent;
        return this;
    }

    public String getLanguage() {
        return language;
    }

    public HttpRequest setLanguage(String mLanguage) {
        language = mLanguage;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public HttpRequest setEncoding(String mEncoding) {
        encoding = mEncoding;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public HttpRequest setCharset(String mCharset) {
        charset = mCharset;
        return this;
    }


}

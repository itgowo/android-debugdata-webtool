package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import java.io.File;
import java.util.HashMap;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.HttpRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.ResponseHandler;

/**
 * @author lujianchao
 */
public interface Action {
    Response doAction(Context context, Request request,HttpRequest httpRequest, ResponseHandler responseHandler);
}

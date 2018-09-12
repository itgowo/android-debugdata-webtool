package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import java.io.File;
import java.util.HashMap;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;

/**
 * @author lujianchao
 */
public interface Action {
    Response doAction(Context context, Request request);
}

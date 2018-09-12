package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import java.io.File;
import java.util.HashMap;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.PrefHelper;

/**
 * @author lujianchao
 * 获取共享参数列表
 */
public class ActionGetSpList implements Action {
    public static final String ACTION = "getSpList";

    @Override
    public Response doAction(Context context, Request request) {
        Response response = new Response();
        response.setSpList(PrefHelper.getSharedPreferenceTags(context));
        return response;
    }
}

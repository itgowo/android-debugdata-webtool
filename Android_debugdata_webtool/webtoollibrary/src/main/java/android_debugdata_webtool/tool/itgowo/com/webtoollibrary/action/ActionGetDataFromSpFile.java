package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.HttpRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.ResponseHandler;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.PrefHelper;

/**
 * @author lujianchao
 * 从共享参数文件获取数据
 */
public class ActionGetDataFromSpFile implements Action {
    public static final String ACTION = "getDataFromSpFile";

    @Override
    public Response doAction(Context context, Request request, HttpRequest httpRequest, ResponseHandler responseHandler) {
        if (request.getSpFileName() == null || request.getSpFileName().length() < 1) {
            return null;
        }
        Response response = PrefHelper.getAllPrefData(context, request.getSpFileName());
        responseHandler.sendPost(response);
        return response;
    }
}

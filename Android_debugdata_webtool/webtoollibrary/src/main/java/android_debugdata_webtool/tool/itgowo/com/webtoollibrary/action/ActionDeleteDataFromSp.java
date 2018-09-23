package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.HttpRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.ResponseHandler;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.PrefHelper;

/**
 * @author lujianchao
 * 从共享参数中删数据
 */
public class ActionDeleteDataFromSp implements Action {
    public static final String ACTION = "deleteDataFromSp";

    @Override
    public Response doAction(Context context, Request request, HttpRequest httpRequest, ResponseHandler responseHandler) {
        Response response = PrefHelper.deleteRow(context, request.getSpFileName(), request.getRowDataRequests());
        responseHandler.sendPost(response);
        return response;
    }
}

package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.DatabaseManager;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.HttpRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.ResponseHandler;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseHelper;

/**
 * @author lujianchao
 * 获取数据库表
 */
public class ActionGetTableList implements Action {
    public static final String ACTION = "getTableList";

    @Override
    public Response doAction(Context context, Request request, HttpRequest httpRequest, ResponseHandler responseHandler) {
        if (request.getDatabase() == null || request.getDatabase().length() < 1) {
            return null;
        }
        Response response=DatabaseHelper.getAllTableName(DatabaseManager.getDatabase(context, request.getDatabase()));
        responseHandler.sendPost(response);
        return response;
    }
}

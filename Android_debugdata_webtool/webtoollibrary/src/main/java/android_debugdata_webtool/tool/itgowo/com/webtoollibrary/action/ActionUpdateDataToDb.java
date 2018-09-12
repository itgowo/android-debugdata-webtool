package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.DatabaseManager;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseHelper;

/**
 * @author lujianchao
 * 更新数据库数据
 */
public class ActionUpdateDataToDb implements Action {
    public static final String ACTION = "updateDataToDb";

    @Override
    public Response doAction(Context context, Request request) {
        Response response = DatabaseHelper.updateRow(DatabaseManager.getDatabase(context, request.getDatabase()), request.getTableName(), request.getRowDataRequests());
        return response;
    }
}

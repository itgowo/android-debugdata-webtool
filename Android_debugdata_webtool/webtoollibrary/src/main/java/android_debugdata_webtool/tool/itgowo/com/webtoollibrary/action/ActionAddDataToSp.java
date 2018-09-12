package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.PrefHelper;

/**
 * @author lujianchao
 * 添加数据到共享参数
 */
public class ActionAddDataToSp implements Action {
    public static final String ACTION = "addDataToSp";

    @Override
    public Response doAction(Context context, Request request) {
        Response response = PrefHelper.addOrUpdateRow(context, request.getSpFileName(), request.getRowDataRequests());
        return response;
    }
}

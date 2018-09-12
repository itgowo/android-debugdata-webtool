package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.DatabaseManager;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;

/**
 * @author lujianchao
 * 获取数据库列表
 */
public class ActionGetDbList implements Action {
    public static final String ACTION = "getDbList";

    @Override
    public Response doAction(Context context, Request request) {
        HashMap<String, File> databaseFiles = DatabaseManager.getDatabaseFiles(context);
        Response response = new Response();
        if (databaseFiles != null) {
            List<Response.FileList.FileData> dblist = new ArrayList<>();
            for (Map.Entry<String, File> mStringFileEntry : databaseFiles.entrySet()) {
                if (!mStringFileEntry.getKey().contains("-journal")) {
                    dblist.add(new Response.FileList.FileData().setFileName(mStringFileEntry.getKey()).setPath(mStringFileEntry.getValue().getPath()));
                }
            }
            response.setDbList(dblist);
        }
        return response;
    }
}

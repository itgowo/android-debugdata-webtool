package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import java.io.File;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;

/**
 * @author lujianchao
 * 删除文件
 */
public class ActionDeleteFile implements Action {
    public static final String ACTION="deleteFile";
    @Override
    public Response doAction(Context context, Request request) {
        if (request.getData() == null || request.getData().length() < 5) {
            return new Response().setCode(Response.code_Error).setMsg("没有指定文件");
        }
        File mFile = new File(request.getData());
        if (mFile == null || !mFile.exists()) {
            return new Response().setCode(Response.code_FileNotFound).setMsg("文件不存在");
        }
        if (mFile.delete()) {
            return new Response();
        } else {
            return new Response().setCode(Response.code_Error).setMsg("文件删除失败");
        }
    }
}

package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.action;

import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.Utils;

/**
 * @author lujianchao
 * 获取data目录文件列表
 */
public class ActionGetFileList implements Action {
    public static final String ACTION = "getFileList";

    @Override
    public Response doAction(Context context, Request request) {
        File root = null;
        if (request.getData() == null || request.getData().length() < 5) {
            root = new File(context.getApplicationInfo().dataDir);
        } else {
            root = new File(request.getData());
        }
        if (root == null || !root.exists()) {
            return new Response().setCode(Response.code_FileNotFound).setMsg("请求的目录或文件不存在");
        }
        List<Response.FileList.FileData> mFileDatas = new ArrayList<>();
        List<Response.FileList.FileColumn> mFileColumns = new ArrayList<>();
        mFileColumns.add(new Response.FileList.FileColumn().setTitle("文件名").setRootPath(root.getPath()).setData("fileName"));
        mFileColumns.add(new Response.FileList.FileColumn().setTitle("文件大小").setRootPath(root.getPath()).setData("fileSize"));
        mFileColumns.add(new Response.FileList.FileColumn().setTitle("最后编辑时间").setRootPath(root.getPath()).setData("fileTime"));
//        mFileColumns.add(new Response.FileList.FileColumn().setTitle("文件夹").setRootPath(root.getPath()).setData("dir"));
        mFileColumns.add(new Response.FileList.FileColumn().setTitle("操作").setRootPath(root.getPath()).setData("delete"));
        File[] files = root.listFiles();
        if (files != null && files.length != 0) {
            for (File f : files) {
                Response.FileList.FileData mFileData = new Response.FileList.FileData();
                mFileData.setIsDir(f.isDirectory());
                mFileData.setFileName(f.getName());
                mFileData.setRootPath(root.getPath().equalsIgnoreCase(context.getApplicationInfo().dataDir) ? null : root.getParent());
                mFileData.setFileSize(f.isDirectory() ? "" : Utils.formatFileSize(context, f.length(), false));
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                mFileData.setFileTime(mSimpleDateFormat.format(f.lastModified()));
                mFileData.setPath(f.getPath());
                mFileData.setDelete(f.canExecute());
                mFileDatas.add(mFileData);
            }
        }
        Response mResponse = new Response().setFileList(new Response.FileList().setFileList(mFileDatas).setFileColumns(mFileColumns));
        return mResponse;
    }
}



package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.DebugDataTool;

/**
 * Created by amitshekhar on 16/11/16.
 */

public class DebugDataAutoInitProvider extends ContentProvider {


    public DebugDataAutoInitProvider() {
    }

    @Override
    public boolean onCreate() {
        DebugDataTool.initialize(getContext(), 8088, false, null);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


}

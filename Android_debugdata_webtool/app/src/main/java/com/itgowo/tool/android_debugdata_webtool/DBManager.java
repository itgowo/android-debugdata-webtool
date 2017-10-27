package com.itgowo.tool.android_debugdata_webtool;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lujianchao on 2017/6/14.
 */

public class DBManager {
    private static Application sApp;

    public static   String DBName = "appinfo.db";
    public static   Class TableClass = HistoryCache.class;

    public static void init(Application mApplication,String mDBName,Class mTableClass) {
        DBName=mDBName;
        sApp = mApplication;
        if (mTableClass!=null){
            TableClass=mTableClass;
        }
    }


    private static final String CREATE_CacheTABLE = "create table "+TableClass.getSimpleName()+" (id integer primary key autoincrement, key text, value text, lasttime long, bak text, flag text)";
    /**
     * 更改类文件必须更改版本号，否则不会更新缓存结构
     */
    public static final int DBVersion = 1;
    private static DBHelper mCacheDBHelper;
    private static SQLiteDatabase mSQLiteDatabase;

    /**
     * 删除数据库
     */
    public synchronized static void deleteDB() {
        sApp.deleteDatabase(DBName);
    }

    /**
     * 更新缓存
     *
     * @param key   预定义名称
     * @param value 待缓存数据
     */
    public synchronized static void updateCache(String key, String value) {
        if (mCacheDBHelper == null) {
            mCacheDBHelper = new DBHelper(sApp, DBName, null, DBVersion);
        }
        if (mSQLiteDatabase == null) {
            mSQLiteDatabase = mCacheDBHelper.getWritableDatabase();
        }
        ContentValues m = new ContentValues();
        m.put("value", value);
        m.put("lasttime", System.currentTimeMillis());
        try {
            mSQLiteDatabase.update(HistoryCache.class.getSimpleName(), m, "key=?", new String[]{key});
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }

    /**
     * 尽量不用，数据会越来越多
     *
     * @param key
     * @param value
     */
    public synchronized static void addCache(String key, String value) {
        if (mCacheDBHelper == null) {
            mCacheDBHelper = new DBHelper(sApp, DBName, null, DBVersion);
        }
        if (mSQLiteDatabase == null) {
            mSQLiteDatabase = mCacheDBHelper.getWritableDatabase();
        }
        ContentValues m = new ContentValues();
        m.put("key", key);
        m.put("value", value);
        m.put("lasttime", System.currentTimeMillis());
        try {
            mSQLiteDatabase.insert(HistoryCache.class.getSimpleName(), null, m);
        } catch (Exception mE) {
            mE.printStackTrace();
        }
    }

    /**
     * 获取缓存数据
     *
     * @param key 预定义名称
     * @return 缓存数据，异常或者不存在则返回null
     */
    public static String getCache(String key) {
        String string = null;
        if (mCacheDBHelper == null) {
            mCacheDBHelper = new DBHelper(sApp, DBName, null, DBVersion);
        }
        if (mSQLiteDatabase == null) {
            mSQLiteDatabase = mCacheDBHelper.getWritableDatabase();
        }
        Cursor mCursor = null;
        try {
            mCursor = mSQLiteDatabase.rawQuery("select * from " + HistoryCache.class.getSimpleName() + " where key=?", new String[]{key});
            if (mCursor != null && mCursor.getCount() == 1) {
                mCursor.moveToNext();
                string = mCursor.getString(2);
            }
        } catch (Exception mE) {
            mE.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
            return string;

        }

    }

    /**
     * Created by lujianchao on 2016/11/29.
     * SQLiteOpenHelper
     *
     * @author lujianchao
     */
    public static class DBHelper extends SQLiteOpenHelper {


        public DBHelper(final Context context, final String name, final SQLiteDatabase.CursorFactory factory, final int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            db.execSQL(CREATE_CacheTABLE);
            updatetable(db, TableClass);
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            updatetable(db, TableClass);
        }

        /**
         * 传入的类名即为表名，传入的类的属性即为表内的记录，字段固定，用来实现动态增减记录，记录为缓存内容，所以数量较少，
         * 只需要更改实体类属性，就可以管理数据库了，动态升级。
         *
         * @param db
         * @param mClass
         */
        private void updatetable(final SQLiteDatabase db, Class mClass) {
            /**
             * 通过反射拿到当前所有cache名
             */
            List<String> mList = new ArrayList<>();
            Field[] fields = mClass.getDeclaredFields();
            for (Field fd : fields) {
                fd.setAccessible(true);
                if (!fd.getName().equals("serialVersionUID") && !fd.getName().equals("$change")) {
                    mList.add(fd.getName());
                }
            }
            Cursor mCursor = db.rawQuery("select * from " + mClass.getSimpleName(), null);
            while (mCursor.moveToNext()) {
                boolean ishave = false;
                String string = mCursor.getString(1);
                Iterator<String> mStringIterator = mList.iterator();
                while (mStringIterator.hasNext()) {
                    if (mStringIterator.next().equals(string)) {
                        ishave = true;
                        mStringIterator.remove();
                        break;
                    }
                }
                /**
                 * 类里没有这个缓存名就将其删掉
                 */
                if (!ishave) {
                    db.delete(mClass.getSimpleName(), "key=?", new String[]{string});
                }
            }
            mCursor.close();
            for (int mI = 0; mI < mList.size(); mI++) {
                ContentValues values = new ContentValues();
                values.put("key", mList.get(mI));
                values.put("lasttime", System.currentTimeMillis());
                db.insert(mClass.getSimpleName(), null, values);
            }
        }
    }

    /**
     * Created by lujianchao on 2016/11/29.
     * 数据结构
     * 添加或者删除属性变量值，都必须更改数据库版本号，否则不会修改
     *
     * @author lujianchao
     */

    public static class HistoryCache {
        /**
         * 新派队首页
         */
        public static String GetHomeInfo = "GetHomeInfo";

        /**
         * 运动项目列表
         */
        public static String getSportItemListByParams = "getSportItemListByParams";
        public static String TestA = "TestA";
        public static String TestB = "TestB";
        public static String TestC = "TestC";
    }
}

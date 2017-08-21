package com.itgowo.tool.android_debugdata_webtool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.DebugDataTool;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.httpParser.HttpRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.onDebugToolListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBManager.init(getApplication());

        DBManager.addCache(""+System.currentTimeMillis(), "aaaaaaaaaaaaaaaa");
        DBManager.addCache(""+System.currentTimeMillis(), "bbbbbbbb");
        DBManager.updateCache(DBManager.HistoryCache.getSportItemListByParams, "234324");
        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putString("aaaaa", "test").commit();
        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putString("adfs", System.currentTimeMillis() + "").commit();
        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putString("ggg", "teddddst").commit();
        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putString("aaaasdfsafee3aa", "tadsfsadfest").commit();
        DebugDataTool.initialize(this, 8088, new onDebugToolListener() {

            @Override
            public String onObjectToJson(Object mObject) {
                return JSON.toJSONString(mObject);
            }

            @Override
            public <T> T onJsonStringToObject(String mJsonString, Class<T> mClass) {
                return JSON.parseObject(mJsonString, mClass);
            }

            @Override
            public void onGetRequest(String mRequest, HttpRequest mHttpRequest) {
                Log.d("onGetRequest",mHttpRequest.getRequestURI());
            }


            @Override
            public void onResponse(String mResponse) {
               Log.d("onResponse",mResponse);
            }
        });
    }
}

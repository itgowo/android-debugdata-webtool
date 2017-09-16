package com.itgowo.tool.android_debugdata_webtool;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.DebugDataTool;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.HttpRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.onDebugToolListener;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.msg);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText("");
            }
        });
        DBManager.init(getApplication());

//        DBManager.addCache("" + System.currentTimeMillis(), "aaaaaaaaaaaaaaaa");
//        DBManager.addCache("" + System.currentTimeMillis(), "bbbbbbbb");
//        DBManager.updateCache(DBManager.HistoryCache.getSportItemListByParams, "234324");
//        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putString("aaaaa", "test").commit();
//        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putString("adfs", System.currentTimeMillis() + "").commit();
//        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putString("ggg", "teddddst").commit();
//        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putString("aaaasdfsafee3aa", "tadsfsadfest").commit();
        DebugDataTool.initialize(this, 8088, true, new onDebugToolListener() {


            @Override
            public void onSystemMsg(final String mS) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SpannableStringBuilder mBuilder=new SpannableStringBuilder("onSystemMsg:" + mS + "\r\n\r\n");
                        mBuilder.setSpan(new ForegroundColorSpan(Color.GREEN),0,mBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mTextView.append(mBuilder);
                    }
                });
            }

            @Override
            public String onObjectToJson(Object mObject) {
                return JSON.toJSONString(mObject);
            }

            @Override
            public <T> T onJsonStringToObject(String mJsonString, Class<T> mClass) {
                return JSON.parseObject(mJsonString, mClass);
            }

            @Override
            public void onGetRequest(String mRequest, final HttpRequest mHttpRequest) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SpannableStringBuilder mBuilder=new SpannableStringBuilder("onGetRequest:" + mHttpRequest.toString() + "\r\n\r\n");
                        mBuilder.setSpan(new ForegroundColorSpan(Color.BLUE),0,12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mTextView.append(mBuilder);
                    }
                });
                Log.d("onGetRequest", mHttpRequest.toString());
            }


            @Override
            public void onResponse(final String mResponse) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SpannableStringBuilder mBuilder=new SpannableStringBuilder("onResponse:" + mResponse + "\r\n\r\n");
                        mBuilder.setSpan(new ForegroundColorSpan(Color.BLUE),0,10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mTextView.append(mBuilder);
                    }
                });
                Log.d("onResponse", mResponse);
            }

            @Override
            public void onError(final String mTip, final Throwable mThrowable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.append("onError:" + mTip + "   " + mThrowable.getMessage() + "\r\n\r\n");
                        SpannableStringBuilder mBuilder=new SpannableStringBuilder("onError:" + mTip + "   "  + mThrowable.getMessage() + "\r\n\r\n");
                        mBuilder.setSpan(new ForegroundColorSpan(Color.RED),0,7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mTextView.append(mBuilder);
                    }
                });
                Log.e("DebugDataWebTool", mTip + "  " + mThrowable.getMessage());
            }
        });
    }

    public void test() {

    }
}

package com.itgowo.tool.android_debugdata_webtool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

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
        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putBoolean("Booblean", true).commit();
        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putFloat("Float", 1.5f).commit();
        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putLong("Long", 1232131231).commit();
        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putString("String", "tadsfsadfest").commit();
        getSharedPreferences("appinfo", MODE_PRIVATE).edit().putInt("Int", 1234).commit();
        DBManager.init(getApplication(), "appinfo.db", null);
        DBManager.updateCache("first", "yes");
        DBManager.updateCache("second", "no");
        DBManager.updateCache("haha", "hehe");


//        DebugDataTool.initialize(this, 8088, false, new onDebugToolListener() {
//
//
//            @Override
//            public void onSystemMsg(final String mS) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        SpannableStringBuilder mBuilder = new SpannableStringBuilder("onSystemMsg:" + mS + "\r\n\r\n");
//                        mBuilder.setSpan(new ForegroundColorSpan(Color.GREEN), 0, mBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        mTextView.append(mBuilder);
//                    }
//                });
//            }
//
//            @Override
//            public String onObjectToJson(Object mObject) {
//                return JSON.toJSONString(mObject);
//            }
//
//            @Override
//            public <T> T onJsonStringToObject(String mJsonString, Class<T> mClass) {
//                return JSON.parseObject(mJsonString, mClass);
//            }
//
//            @Override
//            public void onGetRequest(String mRequest, final HttpRequest mHttpRequest) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        SpannableStringBuilder mBuilder = new SpannableStringBuilder("onGetRequest:" + mHttpRequest.toString() + "\r\n\r\n");
//                        mBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        mTextView.append(mBuilder);
//                    }
//                });
//                Log.d("onGetRequest", mHttpRequest.toString());
//            }
//
//
//            @Override
//            public void onResponse(final String mResponse) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        SpannableStringBuilder mBuilder = new SpannableStringBuilder("onResponse:" + mResponse + "\r\n\r\n");
//                        mBuilder.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        mTextView.append(mBuilder);
//                    }
//                });
//                Log.d("onResponse", mResponse);
//            }
//
//            @Override
//            public void onError(final String mTip, final Throwable mThrowable) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        SpannableStringBuilder mBuilder = new SpannableStringBuilder("onError:" + mTip + "   " + mThrowable.getMessage() + "\r\n\r\n");
//                        mBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        mTextView.append(mBuilder);
//                    }
//                });
//                Log.e("DebugDataWebTool", mTip + "  " + mThrowable.getMessage());
//                mThrowable.printStackTrace();
//            }
//        });
    }

}

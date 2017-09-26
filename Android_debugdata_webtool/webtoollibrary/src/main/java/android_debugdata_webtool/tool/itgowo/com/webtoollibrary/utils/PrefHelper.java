/*
 *
 *  *    Copyright (C) 2016 Amit Shekhar
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Request.RowDataRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;

import static android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseHelper.BOOLEAN;
import static android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseHelper.FLOAT;
import static android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseHelper.INTEGER;
import static android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseHelper.LONG;
import static android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseHelper.STRING_SET;
import static android_debugdata_webtool.tool.itgowo.com.webtoollibrary.utils.DatabaseHelper.TEXT;

/**
 * Created by lujianchao on 2017/8/22.
 */

public class PrefHelper {

    private static final String PREFS_SUFFIX = ".xml";

    private PrefHelper() {
        // This class in not publicly instantiable
    }

    public static TreeMap<String, File> getSharedPreference(Context mContext) {
        TreeMap<String, File> tags = new TreeMap<>();

        String rootPath = mContext.getApplicationInfo().dataDir + "/shared_prefs";
        File root = new File(rootPath);
        if (root.exists()) {
            for (File file : root.listFiles()) {
                String fileName = file.getName();
                if (file.getName().endsWith(PREFS_SUFFIX)) {
                    fileName = file.getName().substring(0, file.getName().length() - PREFS_SUFFIX.length());
                }
                tags.put(fileName, file);
            }
        }
        return tags;
    }

    public static List<Response.FileList.FileData> getSharedPreferenceTags(Context context) {
        ArrayList<Response.FileList.FileData> tags = new ArrayList<>();
        String rootPath = context.getApplicationInfo().dataDir + "/shared_prefs";
        File root = new File(rootPath);
        if (root.exists()) {
            for (File file : root.listFiles()) {
                String fileName = file.getName();
                if (fileName.endsWith(PREFS_SUFFIX)) {
                    tags.add(new Response.FileList.FileData().setFileName(fileName.substring(0, fileName.length() - PREFS_SUFFIX.length())).setPath(file.getPath()));
                }
            }
        }
        Collections.sort(tags);
        return tags;
    }

    /**
     * 获取共享参数list
     *
     * @param context
     * @return
     */
    public static Response getAllPrefData(Context context, String filename) {

        Response response = new Response();
        response.setEditable(true);

        /**
         * 设置表结构
         */
        Response.TableData.TableInfo keyInfo = new Response.TableData.TableInfo();
        keyInfo.setPrimary(true).setTitle("Key");
        Response.TableData.TableInfo valueInfo = new Response.TableData.TableInfo();
        valueInfo.setPrimary(false).setTitle("Value");
        Response.TableData.TableInfo typeInfo = new Response.TableData.TableInfo();
        typeInfo.setPrimary(false).setTitle("DataType");

        Response.TableData mTableData = new Response.TableData();
        mTableData.setTableColumns(new ArrayList<Response.TableData.TableInfo>());
        mTableData.getTableColumns().add(keyInfo);
        mTableData.getTableColumns().add(valueInfo);
        mTableData.getTableColumns().add(typeInfo);
        response.setTableData(mTableData);
        SharedPreferences preferences = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = preferences.getAll();

        mTableData.setTableDatas(new ArrayList<List<Object>>());
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            List<Object> row = new ArrayList<>();
            row.add(entry.getKey());
            row.add(entry.getValue());
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof String) {
                    row.add(TEXT);
                } else if (entry.getValue() instanceof Integer) {
                    row.add(INTEGER);
                } else if (entry.getValue() instanceof Long) {
                    row.add(LONG);
                } else if (entry.getValue() instanceof Float) {
                    row.add(FLOAT);
                } else if (entry.getValue() instanceof Boolean) {
                    row.add(BOOLEAN);
                } else if (entry.getValue() instanceof Set) {
                    row.add(STRING_SET);
                }
            } else {
                row.add(TEXT);
            }
            mTableData.getTableDatas().add(row);
        }

        return response;

    }

    public static Response addOrUpdateRow(Context context, String fileName, List<RowDataRequest> rowDataRequests) {
        Response updateRowResponse = new Response();
        if (fileName == null) {
            return updateRowResponse.setCode(Response.code_Error).setMsg("共享参数文件名未指定");
        }
        if (rowDataRequests == null || rowDataRequests.size() == 0) {
            return updateRowResponse.setCode(Response.code_Error).setMsg("rowDataRequests操作数据不存在，请确认参数是否正确");
        }
        RowDataRequest rowDataKey = rowDataRequests.get(0);
        RowDataRequest rowDataValue = rowDataRequests.get(1);

        String key = rowDataKey.value;
        String value = rowDataValue.value;
        String dataType = rowDataValue.dataType;

        if (Constants.NULL.equals(value)) {
            value = null;
        }
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        try {
            switch (dataType) {
                case TEXT:
                    preferences.edit().putString(key, value).apply();
                    break;
                case INTEGER:
                    preferences.edit().putInt(key, Integer.valueOf(value)).apply();
                    break;
                case LONG:
                    preferences.edit().putLong(key, Long.valueOf(value)).apply();
                    break;
                case FLOAT:
                    preferences.edit().putFloat(key, Float.valueOf(value)).apply();
                    break;
                case BOOLEAN:
                    preferences.edit().putBoolean(key, Boolean.valueOf(value)).apply();
                    break;
                case STRING_SET:
                    JSONArray jsonArray = new JSONArray(value);
                    Set<String> stringSet = new HashSet<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stringSet.add(jsonArray.getString(i));
                    }
                    preferences.edit().putStringSet(key, stringSet).apply();
                    break;
                default:
                    preferences.edit().putString(key, value).apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateRowResponse.setCode(Response.code_Error).setMsg("web server:SP addOrUpdateRow error,参数处理异常  " + e.getMessage());
        }

        return updateRowResponse;
    }


    public static Response deleteRow(Context context, String fileName, List<RowDataRequest> rowDataRequests) {
        Response updateRowResponse = new Response();

        if (fileName == null) {
            return updateRowResponse.setCode(Response.code_Error).setMsg("共享参数文件名未指定");
        }
        if (rowDataRequests == null || rowDataRequests.size() == 0) {
            return updateRowResponse.setCode(Response.code_Error).setMsg("rowDataRequests操作数据不存在，请确认参数是否正确");
        }
        RowDataRequest rowDataKey = rowDataRequests.get(0);
        String key = rowDataKey.value;
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        try {
            preferences.edit().remove(key).apply();
        } catch (Exception ex) {
            updateRowResponse.setCode(Response.code_Error).setMsg("共享参数删除错误  " + ex.getMessage());
        }
        return updateRowResponse;
    }
}

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

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.RowDataRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.UpdateRowResponse;

/**
 * Created by amitshekhar on 06/02/17.
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

    public static List<String> getSharedPreferenceTags(Context context) {

        ArrayList<String> tags = new ArrayList<>();

        String rootPath = context.getApplicationInfo().dataDir + "/shared_prefs";
        File root = new File(rootPath);
        if (root.exists()) {
            for (File file : root.listFiles()) {
                String fileName = file.getName();
                if (fileName.endsWith(PREFS_SUFFIX)) {
                    tags.add(fileName.substring(0, fileName.length() - PREFS_SUFFIX.length()));
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
     * @param tag
     * @return
     */
//    public static Response getAllPrefData(Context context, String tag) {
//
//        Response response = new Response();
//        response.setEditable(true);
//
//        /**
//         * 设置表结构
//         */
//        Response.TableInfo keyInfo = new Response.TableInfo();
//        keyInfo.isPrimary = true;
//        keyInfo.title = "Key";
//        Response.TableInfo valueInfo = new Response.TableInfo();
//        valueInfo.isPrimary = false;
//        valueInfo.title = "Value";
//        response.setTableColumns(new ArrayList<Response.TableInfo>());
//        response.getTableColumns().add(keyInfo);
//        response.getTableColumns().add(valueInfo);
//
//
//        response.setTableDatas(new ArrayList<Response.TableData>());
//        SharedPreferences preferences = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
//        Map<String, ?> allEntries = preferences.getAll();
//
//        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            List<Response.TableData.TableItemData> row = new ArrayList<>();
//            Response.TableData.TableItemData keyColumnData = new Response.TableData.TableItemData();
//            keyColumnData.setDataType(DataType.TEXT);
//            keyColumnData.setValue(entry.getKey());
//
//            row.add(keyColumnData);
//
//            Response.TableData.TableItemData valueColumnData = new Response.TableData.TableItemData();
//            valueColumnData.setValue(entry.getValue().toString());
//            if (entry.getValue() != null) {
//                if (entry.getValue() instanceof String) {
//                    valueColumnData.setDataType(DataType.TEXT);
//                } else if (entry.getValue() instanceof Integer) {
//                    valueColumnData.setDataType( DataType.INTEGER);
//                } else if (entry.getValue() instanceof Long) {
//                    valueColumnData.setDataType( DataType.LONG);
//                } else if (entry.getValue() instanceof Float) {
//                    valueColumnData.setDataType(DataType.FLOAT);
//                } else if (entry.getValue() instanceof Boolean) {
//                    valueColumnData.setDataType( DataType.BOOLEAN);
//                } else if (entry.getValue() instanceof Set) {
//                    valueColumnData.setDataType( DataType.STRING_SET);
//                }
//            } else {
//                valueColumnData.setDataType(DataType.TEXT);
//            }
//            row.add(valueColumnData);
//            response.getTableDatas().add(new Response.TableData().setTableitemdatas(row));
//        }
//
//        return response;
//
//    }

    public static UpdateRowResponse addOrUpdateRow(Context context, String tableName, List<RowDataRequest> rowDataRequests) {
        UpdateRowResponse updateRowResponse = new UpdateRowResponse();

        if (tableName == null) {
            return updateRowResponse;
        }

        RowDataRequest rowDataKey = rowDataRequests.get(0);
        RowDataRequest rowDataValue = rowDataRequests.get(1);

        String key = rowDataKey.value;
        String value = rowDataValue.value;
        String dataType = rowDataValue.dataType;

        if (Constants.NULL.equals(value)) {
            value = null;
        }

        SharedPreferences preferences = context.getSharedPreferences(tableName, Context.MODE_PRIVATE);

        try {
            switch (dataType) {
                case DataType.TEXT:
                    preferences.edit().putString(key, value).apply();
                    updateRowResponse.isSuccessful = true;
                    break;
                case DataType.INTEGER:
                    preferences.edit().putInt(key, Integer.valueOf(value)).apply();
                    updateRowResponse.isSuccessful = true;
                    break;
                case DataType.LONG:
                    preferences.edit().putLong(key, Long.valueOf(value)).apply();
                    updateRowResponse.isSuccessful = true;
                    break;
                case DataType.FLOAT:
                    preferences.edit().putFloat(key, Float.valueOf(value)).apply();
                    updateRowResponse.isSuccessful = true;
                    break;
                case DataType.BOOLEAN:
                    preferences.edit().putBoolean(key, Boolean.valueOf(value)).apply();
                    updateRowResponse.isSuccessful = true;
                    break;
                case DataType.STRING_SET:
                    JSONArray jsonArray = new JSONArray(value);
                    Set<String> stringSet = new HashSet<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        stringSet.add(jsonArray.getString(i));
                    }
                    preferences.edit().putStringSet(key, stringSet).apply();
                    updateRowResponse.isSuccessful = true;
                    break;
                default:
                    preferences.edit().putString(key, value).apply();
                    updateRowResponse.isSuccessful = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return updateRowResponse;
    }


    public static UpdateRowResponse deleteRow(Context context, String tableName,
                                              List<RowDataRequest> rowDataRequests) {
        UpdateRowResponse updateRowResponse = new UpdateRowResponse();

        if (tableName == null) {
            return updateRowResponse;
        }

        RowDataRequest rowDataKey = rowDataRequests.get(0);

        String key = rowDataKey.value;


        SharedPreferences preferences = context.getSharedPreferences(tableName, Context.MODE_PRIVATE);

        try {
            preferences.edit()
                    .remove(key).apply();
            updateRowResponse.isSuccessful = true;
        } catch (Exception ex) {
            updateRowResponse.isSuccessful = false;
        }

        return updateRowResponse;
    }
}

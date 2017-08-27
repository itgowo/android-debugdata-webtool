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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.Response;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.RowDataRequest;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.TableDataResponse;
import android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model.UpdateRowResponse;

/**
 * Created by amitshekhar on 06/02/17.
 */

public class DatabaseHelper {

    private DatabaseHelper() {
        // This class in not publicly instantiable
    }

    public static Response getAllTableName(SQLiteDatabase database) {
        Response response = new Response();
        Cursor c = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table' OR type='view'", null);
        List<String> mStrings = new ArrayList<>();
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                mStrings.add(c.getString(0));
                c.moveToNext();
            }
        }
        c.close();
        response.setTableList(mStrings);
        response.setDbVersion(database.getVersion());
        return response;
    }

    public static Response getTableData(SQLiteDatabase db, String selectQuery, String tableName) {

        Response tableData = new Response();
        if (tableName == null) {
            tableName = getTableName(selectQuery);
        }

        final String quotedTableName = getQuotedTableName(tableName);

        if (tableName != null) {
            final String pragmaQuery = "PRAGMA table_info(" + quotedTableName + ")";
            tableData.setTableColumns(getTableInfo(db, pragmaQuery));
        }
        Cursor cursor = null;
        boolean isView = false;
        try {
            cursor = db.rawQuery("SELECT type FROM sqlite_master WHERE name=?",
                    new String[]{quotedTableName});
            if (cursor.moveToFirst()) {
                isView = "view".equalsIgnoreCase(cursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        tableData.setEditable(tableName != null && tableData.getTableColumns() != null && !isView);


        if (!TextUtils.isEmpty(tableName)) {
            selectQuery = selectQuery.replace(tableName, quotedTableName);
        }

        try {
            cursor = db.rawQuery(selectQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
            tableData.setCode(Response.code_SQLERROR);
            tableData.setMsg("database error 数据库异常");
            return tableData;
        }

        if (cursor != null) {
            cursor.moveToFirst();
            // setting tableInfo when tableName is not known and making
            // it non-editable also by making isPrimary true for all
            if (tableData.getTableColumns() == null) {
                List<Response.TableInfo> mTableDatas = new ArrayList<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Response.TableInfo tableInfo = new Response.TableInfo();
                    tableInfo.title = cursor.getColumnName(i);
                    tableInfo.isPrimary = true;

                }
                tableData.setTableColumns(mTableDatas);
            }
            tableData.setTableDatas(new ArrayList<Response.TableData>());
            List<Response.TableData> mTableDatas = new ArrayList<>();
            if (cursor.getCount() > 0) {
                do {
                    List<Response.TableData> row = new ArrayList<>();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        Response.TableData columnData = new Response.TableData();
                        switch (cursor.getType(i)) {
                            case Cursor.FIELD_TYPE_BLOB:
                                columnData.dataType = DataType.TEXT;
                                columnData.value = ConverterUtils.blobToString(cursor.getBlob(i));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                columnData.dataType = DataType.REAL;
                                columnData.value = cursor.getDouble(i);
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                columnData.dataType = DataType.INTEGER;
                                columnData.value = cursor.getLong(i);
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                columnData.dataType = DataType.TEXT;
                                columnData.value = cursor.getString(i);
                                if (columnData.value == null) {
                                    columnData.value = "";
                                }
                                break;
                            default:
                                columnData.dataType = DataType.TEXT;
                                columnData.value = cursor.getString(i);
                                if (columnData.value == null) {
                                    columnData.value = "";
                                }
                        }
                        row.add(columnData);
                    }
                    tableData.getTableDatas().addAll(row);

                } while (cursor.moveToNext());
            }
            cursor.close();
            return tableData;
        } else {
            tableData.setCode(Response.code_SQLERROR);
            tableData.setMsg("数据库 查询异常，游标为空");
            return tableData;
        }

    }


    private static String getQuotedTableName(String tableName) {
        return String.format("[%s]", tableName);
    }

    private static List<Response.TableInfo> getTableInfo(SQLiteDatabase db, String pragmaQuery) {
        Cursor cursor;
        List<Response.TableInfo> tableInfoList = new ArrayList<>();
        try {
            cursor = db.rawQuery(pragmaQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    Response.TableInfo tableInfo = new Response.TableInfo();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        final String columnName = cursor.getColumnName(i);
                        switch (columnName) {
                            case Constants.PK:
                                tableInfo.isPrimary = cursor.getInt(i) == 1;
                                break;
                            case Constants.NAME:
                                tableInfo.title = cursor.getString(i);
                                break;
                            default:

                        }

                    }
                    tableInfoList.add(tableInfo);
                } while (cursor.moveToNext());
            }
            cursor.close();
            return tableInfoList;
        }
        return tableInfoList;
    }


    public static UpdateRowResponse addRow(SQLiteDatabase db, String tableName, List<RowDataRequest> rowDataRequests) {
        UpdateRowResponse updateRowResponse = new UpdateRowResponse();

        if (rowDataRequests == null || tableName == null) {
            updateRowResponse.isSuccessful = false;
            return updateRowResponse;
        }

        tableName = getQuotedTableName(tableName);

        ContentValues contentValues = new ContentValues();

        for (RowDataRequest rowDataRequest : rowDataRequests) {
            if (Constants.NULL.equals(rowDataRequest.value)) {
                rowDataRequest.value = null;
            }

            switch (rowDataRequest.dataType) {
                case DataType.INTEGER:
                    contentValues.put(rowDataRequest.title, Long.valueOf(rowDataRequest.value));
                    break;
                case DataType.REAL:
                    contentValues.put(rowDataRequest.title, Double.valueOf(rowDataRequest.value));
                    break;
                case DataType.TEXT:
                    contentValues.put(rowDataRequest.title, rowDataRequest.value);
                    break;
                default:
                    contentValues.put(rowDataRequest.title, rowDataRequest.value);
                    break;
            }
        }

        long result = db.insert(tableName, null, contentValues);
        updateRowResponse.isSuccessful = result > 0;

        return updateRowResponse;

    }


    public static UpdateRowResponse updateRow(SQLiteDatabase db, String tableName, List<RowDataRequest> rowDataRequests) {

        UpdateRowResponse updateRowResponse = new UpdateRowResponse();

        if (rowDataRequests == null || tableName == null) {
            updateRowResponse.isSuccessful = false;
            return updateRowResponse;
        }

        tableName = getQuotedTableName(tableName);

        ContentValues contentValues = new ContentValues();

        String whereClause = null;
        List<String> whereArgsList = new ArrayList<>();

        for (RowDataRequest rowDataRequest : rowDataRequests) {
            if (Constants.NULL.equals(rowDataRequest.value)) {
                rowDataRequest.value = null;
            }
            if (rowDataRequest.isPrimary) {
                if (whereClause == null) {
                    whereClause = rowDataRequest.title + "=? ";
                } else {
                    whereClause = whereClause + "and " + rowDataRequest.title + "=? ";
                }
                whereArgsList.add(rowDataRequest.value);
            } else {
                switch (rowDataRequest.dataType) {
                    case DataType.INTEGER:
                        contentValues.put(rowDataRequest.title, Long.valueOf(rowDataRequest.value));
                        break;
                    case DataType.REAL:
                        contentValues.put(rowDataRequest.title, Double.valueOf(rowDataRequest.value));
                        break;
                    case DataType.TEXT:
                        contentValues.put(rowDataRequest.title, rowDataRequest.value);
                        break;
                    default:
                }
            }
        }

        String[] whereArgs = new String[whereArgsList.size()];

        for (int i = 0; i < whereArgsList.size(); i++) {
            whereArgs[i] = whereArgsList.get(i);
        }

        db.update(tableName, contentValues, whereClause, whereArgs);
        updateRowResponse.isSuccessful = true;
        return updateRowResponse;
    }


    public static UpdateRowResponse deleteRow(SQLiteDatabase db, String tableName,
                                              List<RowDataRequest> rowDataRequests) {

        UpdateRowResponse updateRowResponse = new UpdateRowResponse();

        if (rowDataRequests == null || tableName == null) {
            updateRowResponse.isSuccessful = false;
            return updateRowResponse;
        }

        tableName = getQuotedTableName(tableName);


        String whereClause = null;
        List<String> whereArgsList = new ArrayList<>();

        for (RowDataRequest rowDataRequest : rowDataRequests) {
            if (Constants.NULL.equals(rowDataRequest.value)) {
                rowDataRequest.value = null;
            }
            if (rowDataRequest.isPrimary) {
                if (whereClause == null) {
                    whereClause = rowDataRequest.title + "=? ";
                } else {
                    whereClause = whereClause + "and " + rowDataRequest.title + "=? ";
                }
                whereArgsList.add(rowDataRequest.value);
            }
        }

        if (whereArgsList.size() == 0) {
            updateRowResponse.isSuccessful = true;
            return updateRowResponse;
        }

        String[] whereArgs = new String[whereArgsList.size()];

        for (int i = 0; i < whereArgsList.size(); i++) {
            whereArgs[i] = whereArgsList.get(i);
        }

        db.delete(tableName, whereClause, whereArgs);
        updateRowResponse.isSuccessful = true;
        return updateRowResponse;
    }


    public static TableDataResponse exec(SQLiteDatabase database, String sql) {
        TableDataResponse tableDataResponse = new TableDataResponse();
        tableDataResponse.isSelectQuery = false;
        try {

            String tableName = getTableName(sql);

            if (!TextUtils.isEmpty(tableName)) {
                String quotedTableName = getQuotedTableName(tableName);
                sql = sql.replace(tableName, quotedTableName);
            }

            database.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
            tableDataResponse.isSuccessful = false;
            tableDataResponse.errorMessage = e.getMessage();
            return tableDataResponse;
        }
        tableDataResponse.isSuccessful = true;
        return tableDataResponse;
    }

    private static String getTableName(String selectQuery) {
        // TODO: 24/4/17 Handle JOIN Query
        TableNameParser tableNameParser = new TableNameParser(selectQuery);
        HashSet<String> tableNames = (HashSet<String>) tableNameParser.tables();

        for (String tableName : tableNames) {
            if (!TextUtils.isEmpty(tableName)) {
                return tableName;
            }
        }

        return null;
    }

}

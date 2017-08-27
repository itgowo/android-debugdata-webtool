package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;

import java.util.List;

/**
 * Created by hnvfh on 2017/8/22.
 */

public class Response {
    public static final int code_OK = 200;
    public static final int code_Error = 201;
    public static final int code_SQLERROR = 202;
    public static final int code_SQLNODATA = 203;
    private int code = code_OK;
    private String msg = "success";
    private String action;
    private Integer dbVersion;
    /**
     * 数据库列表
     */
    private List<String> dbList;
    /**
     * 共享参数列表
     */
    private List<String> spList;
    /**
     * 数据库中所有表
     */
    private List<String> tableList;
    /**
     * 表结构信息
     */
    private List<TableInfo> tableColumns;
    /**
     * 表数据
     */
    private List<TableData> tableDatas;
    /**
     * 是否可编辑数据
     */
    private Boolean isEditable ;


    public static class TableInfo {
        public String title;
        public boolean isPrimary;
    }

    public static class TableData {
        public String dataType;
        public Object value;
    }

    public Boolean isEditable() {
        return isEditable;
    }

    public Response setEditable(Boolean mEditable) {
        isEditable = mEditable;
        return this;
    }

    public List<TableInfo> getTableColumns() {
        return tableColumns;
    }

    public Response setTableColumns(List<TableInfo> mTableColumns) {
        tableColumns = mTableColumns;
        return this;
    }

    public List<TableData> getTableDatas() {
        return tableDatas;
    }

    public Response setTableDatas(List<TableData> mTableDatas) {
        tableDatas = mTableDatas;
        return this;
    }

    public List<String> getTableList() {
        return tableList;
    }

    public Response setTableList(List<String> mTableList) {
        tableList = mTableList;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Response setCode(int mCode) {
        code = mCode;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Response setMsg(String mMsg) {
        msg = mMsg;
        return this;
    }

    public String getAction() {
        return action;
    }

    public Response setAction(String mAction) {
        action = mAction;
        return this;
    }

    public Integer getDbVersion() {
        return dbVersion;
    }

    public Response setDbVersion(Integer mDbVersion) {
        dbVersion = mDbVersion;
        return this;
    }

    public List<String> getDbList() {
        return dbList;
    }

    public Response setDbList(List<String> mDbList) {
        dbList = mDbList;
        return this;
    }

    public List<String> getSpList() {
        return spList;
    }

    public Response setSpList(List<String> mSpList) {
        spList = mSpList;
        return this;
    }
}

package android_debugdata_webtool.tool.itgowo.com.webtoollibrary;



import java.util.Comparator;
import java.util.List;

/**
 * Created by lujianchao on 2017/8/22.
 */

public class Response {
    public static final int code_OK = 200;
    public static final int code_Error = 201;
    public static final int code_SQLERROR = 202;
    public static final int code_SQLNODATA = 203;
    public static final int code_FileNotFound = 204;
    private int code = code_OK;
    private String msg = "success";
    private String action;
    private Integer dbVersion;
    /**
     * 数据库列表
     */
    private List<FileList.FileData> dbList;
    /**
     * 共享参数列表
     */
    private List<FileList.FileData> spList;
    /**
     * 数据库中所有表
     */
    private List<String> tableList;


    private TableData tableData;


    /**
     * 是否可编辑数据
     */
    private Boolean isEditable;
    private FileList fileList;

    public FileList getFileList() {
        return fileList;
    }

    public Response setFileList(FileList mFileList) {
        fileList = mFileList;
        return this;
    }

    public static class FileList{
    private List<FileData> fileList;
    private List<FileColumn> mFileColumns;

    public List<FileData> getFileList() {
        return fileList;
    }

    public FileList setFileList(List<FileData> mFileList) {
        fileList = mFileList;
        return this;
    }

        public List<FileColumn> getFileColumns() {
            return mFileColumns;
        }

        public FileList setFileColumns(List<FileColumn> mFileColumns) {
            this.mFileColumns = mFileColumns;
            return this;
        }

        public static class FileColumn{
        private String rootPath;
        private String title;
        private String data;

        public String getRootPath() {
            return rootPath;
        }

        public FileColumn setRootPath(String mRootPath) {
            rootPath = mRootPath;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public FileColumn setTitle(String mTitle) {
            title = mTitle;
            return this;
        }

        public String getData() {
            return data;
        }

        public FileColumn setData(String mData) {
            data = mData;
            return this;
        }
    }
    public static class FileData implements Comparable<FileData> {
        private Boolean IsDir ;
        private String path;
        private String rootPath;
        private String fileName;
        private String fileSize;
        private String fileTime;
        private Boolean delete;

        public Boolean getDelete() {
            return delete;
        }

        public FileData setDelete(Boolean mDelete) {
            delete = mDelete;
            return this;
        }

        public String getRootPath() {
            return rootPath;
        }

        public FileData setRootPath(String mRootPath) {
            rootPath = mRootPath;
            return this;
        }

        public Boolean isDir() {
            return IsDir;
        }

        public FileData setIsDir(Boolean mDir) {
            IsDir = mDir;
            return this;
        }

        public String getPath() {
            return path;
        }

        public FileData setPath(String mPath) {
            path = mPath;
            return this;
        }

        public String getFileName() {
            return fileName;
        }

        public FileData setFileName(String mFileName) {
            fileName = mFileName;
            return this;
        }

        public String getFileSize() {
            return fileSize;
        }

        public FileData setFileSize(String mFileSize) {
            fileSize = mFileSize;
            return this;
        }

        public String getFileTime() {
            return fileTime;
        }

        public FileData setFileTime(String mFileTime) {
            fileTime = mFileTime;
            return this;
        }
        @Override
        public int compareTo( FileData o) {
            return this.getFileName().compareTo(o.getFileName());
        }
    }
}


    public static class TableData {
        /**
         * 表结构信息
         */
        private List<TableInfo> tableColumns;
        /**
         * 表数据
         */
        private List<List<Object>> tableDatas;

        private Long dataCount;

        public List<TableInfo> getTableColumns() {
            return tableColumns;
        }

        public TableData setTableColumns(List<TableInfo> mTableColumns) {
            tableColumns = mTableColumns;
            return this;
        }

        public List<List<Object>> getTableDatas() {
            return tableDatas;
        }

        public TableData setTableDatas(List<List<Object>> mTableDatas) {
            tableDatas = mTableDatas;
            return this;
        }

        public Long getDataCount() {
            return dataCount;
        }

        public TableData setDataCount(Long mDataCount) {
            dataCount = mDataCount;
            return this;
        }

        public static class TableInfo {
            private String title;
            private boolean isPrimary;
            private Boolean isNotNull;
            private String defaultValue;
            private String dataType;

            public Boolean isNotNull() {
                return isNotNull;
            }

            public TableInfo setNotNull(Boolean mNotNull) {
                isNotNull = mNotNull;
                return this;
            }

            public String getDefaultValue() {
                return defaultValue;
            }

            public TableInfo setDefaultValue(String mDefaultValue) {
                defaultValue = mDefaultValue;
                return this;
            }

            public String getTitle() {
                return title;
            }

            public TableInfo setTitle(String mTitle) {
                title = mTitle;
                return this;
            }

            public boolean isPrimary() {
                return isPrimary;
            }

            public TableInfo setPrimary(boolean mPrimary) {
                isPrimary = mPrimary;
                return this;
            }

            public String getDataType() {
                return dataType;
            }

            public TableInfo setDataType(String mDataType) {
                dataType = mDataType;
                return this;
            }


        }
    }


    public TableData getTableData() {
        return tableData;
    }

    public Response setTableData(TableData mTableData) {
        tableData = mTableData;
        return this;
    }

    public Boolean getEditable() {
        return isEditable;
    }

    public Boolean isEditable() {
        return isEditable;
    }

    public Response setEditable(Boolean mEditable) {
        isEditable = mEditable;
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

    public List<FileList.FileData> getDbList() {
        return dbList;
    }

    public Response setDbList(List<FileList.FileData> mDbList) {
        dbList = mDbList;
        return this;
    }

    public List<FileList.FileData> getSpList() {
        return spList;
    }

    public Response setSpList(List<FileList.FileData> mSpList) {
        spList = mSpList;
        return this;
    }

    public String toJson() {
        return DebugDataTool.ObjectToJson(this);
    }
}

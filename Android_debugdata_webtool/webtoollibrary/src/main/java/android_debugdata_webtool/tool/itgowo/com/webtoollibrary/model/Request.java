package android_debugdata_webtool.tool.itgowo.com.webtoollibrary.model;

import java.util.List;

/**
 * Created by hnvfh on 2017/8/17.
 */

public class Request {
    private List<RowDataRequest> mRowDataRequests;

    public List<RowDataRequest> getRowDataRequests() {
        return mRowDataRequests;
    }

    public Request setRowDataRequests(List<RowDataRequest> mRowDataRequests) {
        this.mRowDataRequests = mRowDataRequests;
        return this;
    }
    public static class RowDataRequest {

        public String title;
        public boolean isPrimary;
        public String dataType;
        public String value;

    }
}

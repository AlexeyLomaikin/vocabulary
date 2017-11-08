package com.localhost.gwt.shared;

import java.io.Serializable;

/**
 * Created by AlexL on 08.11.2017.
 */
public class PagerItem implements Serializable {
    int pageNum = 1;
    int pageLimit = 10;

    public PagerItem() {}
    public PagerItem(int pageNum, int pageLimit) {
        this.pageLimit = pageLimit;
        this.pageNum = pageNum;
    }

    public void setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageNum() {
        return pageNum;
    }
}

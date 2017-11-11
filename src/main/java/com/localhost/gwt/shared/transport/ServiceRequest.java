package com.localhost.gwt.shared.transport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AlexL on 11.11.2017.
 */
public class ServiceRequest implements Serializable {
    private String levelId;
    private String langId;
    private PagerItem pagerItem;
    private String searchKey;
    private Map<String, String> params = new HashMap<String, String>();

    public PagerItem getPagerItem() {
        return pagerItem;
    }

    public String getLevelId() {
        return levelId;
    }

    public String getLangId() {
        return langId;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setPagerItem(PagerItem pagerItem) {
        this.pagerItem = pagerItem;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public void setLangId(String langId) {
        this.langId = langId;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void addParam(String name, String value) {
        params.put(name, value);
    }

    public String getParam(String name) {
        return params.get(name);
    }
}

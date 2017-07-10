package com.caishi.bigdata.categorize.textseg.common.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by fuli.shen on 2016/11/23.
 */
public class CategoryInfo implements Serializable {


    private String id;
    private String categoryIds;
    private String origin;
    private Integer sourceId;
    @JSONField(name = "m_classifier")
    private String mClassifier;
    private String pubtime;
    private String title;
    private String content;


    public CategoryInfo() {
    }

    public String getmClassifier() {
        return mClassifier;
    }

    public void setmClassifier(String mClassifier) {
        this.mClassifier = mClassifier;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(String categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getPubtime() {
        return pubtime;
    }

    public void setPubtime(String pubtime) {
        this.pubtime = pubtime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

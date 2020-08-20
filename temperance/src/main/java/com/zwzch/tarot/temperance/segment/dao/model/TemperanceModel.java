package com.zwzch.tarot.temperance.segment.dao.model;

import java.sql.Date;

public class TemperanceModel {
    private String tag;

    private Long maxId;

    private Integer step;

    private String description;

    private Date updateTime;

    public TemperanceModel() {
    }

    public TemperanceModel(String tag, Long maxId, Integer step) {
        this.tag = tag;
        this.maxId = maxId;
        this.step = step;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

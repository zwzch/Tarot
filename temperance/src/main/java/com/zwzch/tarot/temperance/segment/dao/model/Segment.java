package com.zwzch.tarot.temperance.segment.dao.model;

import java.sql.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Segment {

    private volatile boolean init;

    private AtomicLong value = new AtomicLong(0);

    private long maxId;

    private Integer step;

    private volatile long updateTimestamp;

    public AtomicLong getValue() {
        return value;
    }

    public void setValue(AtomicLong value) {
        this.value = value;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public long getIdle() {
        return this.getMaxId() - getValue().get();
    }

    @Override
    public String toString() {
        return "Segment{" +
                "init=" + init +
                ", value=" + value +
                ", maxId=" + maxId +
                ", step=" + step +
                '}';
    }
}

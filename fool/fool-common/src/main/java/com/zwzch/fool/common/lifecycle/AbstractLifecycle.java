package com.zwzch.fool.common.lifecycle;

public class AbstractLifecycle implements Lifecycle {
    private final Object lock = new Object();
    protected volatile boolean isInited = false;

    @Override
    public void init() {
        synchronized (lock) {
            if (isInited()) {
                return;
            }
            isInited = true;
            doInit();
        }
    }

    @Override
    public void destroy() {
        synchronized (lock) {
            if (!isInited()) {
                return;
            }
            doDestory();
            isInited = false;
        }
    }

    @Override
    public boolean isInited() {
        return this.isInited;
    }

    protected void doInit() {

    }

    protected void doDestory() {
    }

}

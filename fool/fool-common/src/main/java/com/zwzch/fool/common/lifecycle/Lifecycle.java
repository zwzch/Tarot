package com.zwzch.fool.common.lifecycle;

public interface Lifecycle {

    void init();

    void destroy();

    boolean isInited();
}

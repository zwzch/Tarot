package com.zwzch.fool.common.exception;

public class CommonExpection extends RuntimeException {
    public CommonExpection() {
        super();
    }
    public CommonExpection(String message) {
        super(message);
    }
    public CommonExpection(Exception e) {
        super(e);
    }

    public CommonExpection(String message, Exception e) {
        super(message, e);
    }

    public CommonExpection(String message, Throwable e) {
        super(message, e);
    }
}

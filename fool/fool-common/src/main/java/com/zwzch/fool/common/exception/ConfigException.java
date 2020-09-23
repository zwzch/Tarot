package com.zwzch.fool.common.exception;

public class ConfigException extends CommonExpection {
    public ConfigException() {
        super();
    }

    public ConfigException(String msg) {
        super(msg);
    }

    public ConfigException(String msg, Exception e) {
        super(msg, e);
    }
}

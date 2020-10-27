package com.zwzch.fool.rule.exception;


import com.zwzch.fool.common.exception.CommonExpection;

public class RuleRuntimeException extends CommonExpection {
    public RuleRuntimeException() {
        super();
    }

    public RuleRuntimeException(String msg) {
        super(msg);
    }

    public RuleRuntimeException(String msg, Exception e) {
        super(msg, e);
    }
}

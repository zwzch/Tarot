package com.zwzch.fool.repo.expection;

import com.zwzch.fool.common.exception.CommonExpection;

public class PdbRuntimeException extends CommonExpection {
    public PdbRuntimeException() {
        super();
    }

    public PdbRuntimeException(String msg) {
        super(msg);
    }

    public PdbRuntimeException(String msg, Exception e) {
        super(msg, e);
    }

}

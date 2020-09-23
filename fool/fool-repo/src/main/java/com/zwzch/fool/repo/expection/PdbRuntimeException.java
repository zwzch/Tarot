package com.zwzch.fool.repo.expection;

public class PdbRuntimeException extends Exception {
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

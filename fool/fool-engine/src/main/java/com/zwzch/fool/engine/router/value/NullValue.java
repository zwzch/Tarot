package com.zwzch.fool.engine.router.value;

public class NullValue implements Comparable{

    private static NullValue instance = new NullValue();
    private String           str      = "null";

    public static NullValue getNullValue() {
        return instance;
    }

    private NullValue(){
    }

    public int compareTo(Object o) {
        if (o == this) {
            return 0;
        }
        if (o instanceof NullValue) {
            return 0;
        }
        return -1;

    }

    public String toString() {
        return str;
    }

}

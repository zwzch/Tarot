package com.zwzch.fool.rule.value;

public class EmptyValue implements Value {
    @Override
    public boolean equals(Object v) {
        if(v instanceof EmptyValue) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[empty value]");
        return sb.toString();
    }
}

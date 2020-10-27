package com.zwzch.fool.rule.value;

import com.zwzch.fool.common.exception.CommonExpection;

public class SingleRangeValue implements Value {
    public final String column;
    public final Long left;
    public final Long right;

    public SingleRangeValue(String column, long left, long right) {
        this.column = column;
        this.left = left;
        this.right = right;
        if(this.left>this.right) {
            throw new CommonExpection("SingleRangeValue error, left>right, left:" + left + " ,right:" + right);
        }

        if(column==null) {
            throw new CommonExpection("SingleRnageValue error, column is null");
        }
    }
}

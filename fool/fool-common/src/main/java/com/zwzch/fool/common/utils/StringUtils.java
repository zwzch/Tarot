package com.zwzch.fool.common.utils;

import com.zwzch.fool.common.exception.CommonExpection;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static int getPositiveInt(String str) throws CommonExpection {
        if(str==null) {
            throw new CommonExpection("str is null");
        }

        str = trim(str);

        if(str.matches("[0-9]+")) {
            return Integer.valueOf(str);
        } else {
            throw new CommonExpection("str is not positive int:" + str);
        }
    }

    /* 0000-0009 */
    public static List<String> getNumRange(String str, int numLen) throws CommonExpection {
        if(str == null) {
            throw new CommonExpection("str is null");
        }

        str = trim(str);
        List<String> ret = new ArrayList<String>();

        if(str.matches("[0-9]+-[0-9]+")) {
            String[] splitArray = str.split("-");
            int low = Integer.valueOf(splitArray[0]);
            int high = Integer.valueOf(splitArray[1]);

            if(low>high) {
                throw new CommonExpection("getNumRange str is worng, str:" + str);
            }

            for(int i=low; i<=high; i++) {
                Formatter fmt = new Formatter();
                if(numLen<=0) {
                    fmt.format("%d", i);
                } else {
                    fmt.format("%0" + numLen + "d", i);
                }
                ret.add(fmt.toString());
            }
        } else {
            ret.add(str);
        }

        return ret;
    }

    public static boolean isEquals(Object o1, Object o2) {
        if(o1==null) {
            if(o2!=null) {
                return false;
            } else {
                return true;
            }
        } else {
            return o1.equals(o2);
        }
    }
}

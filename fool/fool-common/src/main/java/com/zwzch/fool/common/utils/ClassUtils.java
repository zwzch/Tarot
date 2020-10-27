package com.zwzch.fool.common.utils;

import com.zwzch.fool.common.IBase;

import java.util.HashMap;
import java.util.Map;

public class ClassUtils implements IBase {
    private static Map<String, Class> classMap = new HashMap<String, Class>();

    public static Class getObjectByClassName(String className, String vitaminServiceName) throws Exception {
        try {
            return Class.forName(className);

        } catch (ClassNotFoundException e) {
            log.info("class is not found local, try vitamin, className:" + className);
        } catch (Exception e) {
            throw e;
        }
        return null;
    }

}

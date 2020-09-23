package com.zwzch.fool.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.zwzch.fool.common.exception.CommonExpection;

import java.io.StringReader;

public class JsonUtils {
    public static Boolean isExist(JsonObject obj, String name) {
        return obj.get(name)!=null;
    }


    public static JsonObject parseJsonStr(String str) throws CommonExpection {
        if(str == null) {
            throw new CommonExpection("param is null");
        }

        JsonObject root = null;
        try {
            JsonParser parser = new JsonParser();
            JsonReader reader = new JsonReader(new StringReader(str));
            reader.setLenient(true);

            root = parser.parse(reader).getAsJsonObject();
        } catch (Exception e) {
            throw new CommonExpection("json parse error", e);
        }

        if(root == null) {
            throw new CommonExpection("json parse get null");
        } else {
            return root;
        }
    }

    public static JsonElement getElementFromObject(JsonObject jsonObject, String name) throws CommonExpection {
        if(jsonObject==null || name==null) {
            throw new CommonExpection("param is null, name:" + name);
        }

        try {
            if(jsonObject.get(name) == null){
                throw new CommonExpection("json get null entry, name:" + name);
            } else {
                return jsonObject.get(name);
            }
        } catch (Exception e) {
            throw new CommonExpection("json Exception, name" + name, e);
        }
    }

    public static String getStringFromObject(JsonObject jsonObject, String name) throws CommonExpection {
        if(jsonObject==null || name==null) {
            throw new CommonExpection("param is null, name:" + name);
        }

        try {
            JsonElement jsonElement = jsonObject.get(name);
            if(jsonElement == null || jsonElement.getAsString()==null) {
                throw new CommonExpection("json get null entry, name:" + name);
            } else {
                return jsonElement.getAsString().trim();
            }
        } catch(Exception e) {
            throw new CommonExpection("json Exception, name:" + name, e);
        }
    }

    public static int getIntFromObject(JsonObject jsonObject, String name) throws CommonExpection {
        if(jsonObject==null || name==null) {
            throw new CommonExpection("param is null, name:" + name);
        }

        try {
            JsonElement jsonElement = jsonObject.get(name);
            if(jsonElement == null) {
                throw new CommonExpection("json get null entry, name:" + name);
            } else {
                return jsonElement.getAsInt();
            }
        } catch(Exception e) {
            throw new CommonExpection("json Exception, name:" + name, e);
        }
    }

    public static boolean getBoolFromObject(JsonObject jsonObject, String name) throws CommonExpection {
        if(jsonObject==null || name==null) {
            throw new CommonExpection("param is null, name:" + name);
        }

        try {
            JsonElement jsonElement = jsonObject.get(name);
            if(jsonElement == null) {
                throw new CommonExpection("json get null entry, name:" + name);
            } else {
                return jsonElement.getAsBoolean();
            }
        } catch(Exception e) {
            throw new CommonExpection("json Exception, name:" + name, e);
        }
    }


}

package com.zwzch.fool.engine.utils;

public class PreParseUtil {
    public static boolean isDDL(String str) {
        if(str.trim().toUpperCase().startsWith("CREATE")
                ||str.trim().toUpperCase().startsWith("ALTER")
                ||str.trim().toUpperCase().startsWith("RENAME")
                ||str.trim().toUpperCase().startsWith("DROP")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isLock(String str) {
        if(str.trim().toUpperCase().startsWith("LOCK")
                ||str.trim().toUpperCase().startsWith("UNLOCK")){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isUpdate(String str) {
        if(str.trim().toUpperCase().startsWith("INSERT")
                ||str.trim().toUpperCase().startsWith("UPDATE")
                ||str.trim().toUpperCase().startsWith("REPLACE")
                ||str.trim().toUpperCase().startsWith("DELETE")){
            return true;
        }else{
            return false;
        }
    }
}

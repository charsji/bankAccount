package com.hodo.jjaccount.util;

import java.util.Map;

public class jjUtil {

    public static String handleParams(Map<String, Object> params
            , String key) {
        if (params.get(key) != null) {
            String value = params.get(key).toString();
            params.remove(key);
            return value;
        }
        return "";
    }
}

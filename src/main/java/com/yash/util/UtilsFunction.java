package com.yash.util;

public class UtilsFunction {

    public static String extractFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }


}

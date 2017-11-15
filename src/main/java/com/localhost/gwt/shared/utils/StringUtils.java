package com.localhost.gwt.shared.utils;

/**
 * Created by AlexL on 15.11.2017.
 */
public class StringUtils {
    private StringUtils() {
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isSpace(String s) {
        return s != null && s.matches("\\s+");
    }

    public static boolean isEmptyOrSpace(String s) {
        return isEmpty(s) ||isSpace(s);
    }
}

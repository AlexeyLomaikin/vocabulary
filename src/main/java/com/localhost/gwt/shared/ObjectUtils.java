package com.localhost.gwt.shared;

import java.util.Collection;

/**
 * Created by AlexL on 07.10.2017.
 */
public class ObjectUtils {
    private ObjectUtils(){}

    public static boolean isEmpty(Object o) {
        if (o instanceof String) {
            return isEmpty((String)o);
        }
        if (o instanceof Collection) {
            return isEmpty((Collection)o);
        }
        return false;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    private static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }
}

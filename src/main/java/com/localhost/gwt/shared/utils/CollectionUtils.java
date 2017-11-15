package com.localhost.gwt.shared.utils;

import java.util.Collection;

/**
 * Created by AlexL on 07.10.2017.
 */
public class CollectionUtils {
    private CollectionUtils(){}

    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }
}

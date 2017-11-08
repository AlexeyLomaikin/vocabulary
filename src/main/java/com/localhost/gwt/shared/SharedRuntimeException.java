package com.localhost.gwt.shared;

import java.io.Serializable;

/**
 * Created by AlexL on 07.10.2017.
 */
public class SharedRuntimeException extends RuntimeException implements Serializable {
    String message;

    public SharedRuntimeException() {
    }
    public SharedRuntimeException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

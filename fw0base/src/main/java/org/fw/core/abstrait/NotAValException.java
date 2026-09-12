package org.fw.core.abstrait;

import org.fw.core.NativeExecutionException;

public class NotAValException extends NativeExecutionException {
    public NotAValException(Throwable cause) {
        super(cause);
    }

    public NotAValException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAValException(String message) {
        super(message);
    }
}

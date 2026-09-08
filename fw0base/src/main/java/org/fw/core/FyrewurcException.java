package org.fw.core;

public class FyrewurcException extends Exception {
    public FyrewurcException() {
    }

    public FyrewurcException(String message) {
        super(message);
    }

    public FyrewurcException(String message, Throwable cause) {
        super(message, cause);
    }

    public FyrewurcException(Throwable cause) {
        super(cause);
    }

    public FyrewurcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

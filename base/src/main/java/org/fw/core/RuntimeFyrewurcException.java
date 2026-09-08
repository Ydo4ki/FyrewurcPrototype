package org.fw.core;

public class RuntimeFyrewurcException extends RuntimeException {
    public RuntimeFyrewurcException() {
    }

    public RuntimeFyrewurcException(String message) {
        super(message);
    }

    public RuntimeFyrewurcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RuntimeFyrewurcException(Throwable cause) {
        super(cause);
    }

    public RuntimeFyrewurcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

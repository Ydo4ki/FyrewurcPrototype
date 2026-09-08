package org.fw.core;

public class NativeExecutionException extends RuntimeFyrewurcException {
    public NativeExecutionException(Throwable cause) {
        super(cause);
    }

    public NativeExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NativeExecutionException(String message) {
        super(message);
    }
}

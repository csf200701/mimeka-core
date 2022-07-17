package com.igd.mimeka.exceptions;

public class MimekaException extends Exception {

	private static final long serialVersionUID = 1L;

	public MimekaException(String msg) {
        super(msg);
    }

    public MimekaException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

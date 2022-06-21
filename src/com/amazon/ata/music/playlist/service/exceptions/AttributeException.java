package com.amazon.ata.music.playlist.service.exceptions;

public class AttributeException extends RuntimeException{

    private static final long serialVersionUID = -7718695886295024481L;

    public AttributeException() {
        super();
    }

    public AttributeException(String message) {
        super(message);
    }

    public AttributeException(Throwable cause) {
        super(cause);
    }

    public AttributeException(String message, Throwable cause) {
        super(message, cause);
    }

}

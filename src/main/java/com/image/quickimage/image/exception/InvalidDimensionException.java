package com.image.quickimage.image.exception;

public class InvalidDimensionException extends RuntimeException {
    public InvalidDimensionException(String dimensionsInvalid) {
        super(dimensionsInvalid);
    }
}

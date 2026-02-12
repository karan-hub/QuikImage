package com.image.quickimage.image.exception;

public class InvalidDimensionException extends Throwable {
    public InvalidDimensionException(String dimensionsInvalid) {
        super(dimensionsInvalid);
    }
}

package com.image.quickimage.image.service;

public class InvalidDimensionException extends Throwable {
    public InvalidDimensionException(String dimensionsInvalid) {
        super(dimensionsInvalid);
    }
}

package com.image.quickimage.image.exception;

import com.image.quickimage.image.service.InvalidDimensionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidDimensionException.class)
    public ResponseEntity<String> handleInvalidDimensionException(Exception e){
        return  new ResponseEntity<>(e.getMessage() , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<String> handleImageNotFoundException(Exception e){
        return  new ResponseEntity<>(e.getMessage() , HttpStatus.NOT_FOUND);
    }
}


package com.image.quickimage.image.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidDimensionException.class)
    public ResponseEntity<String> handleInvalidDimensionException(InvalidDimensionException e){
        return  new ResponseEntity<>(e.getMessage() , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<String> handleImageNotFoundException(ImageNotFoundException e){
        return  new ResponseEntity<>(e.getMessage() , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedMediaException.class)
    public ResponseEntity<String> handleUnsupportedMediaException(UnsupportedMediaException e){
        return  new  ResponseEntity<>(e.getMessage() , HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<String> handleIO(DuplicateNameException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIO(IOException e) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
    }


}


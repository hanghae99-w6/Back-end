package com.springw6.backend.controller;

import com.springw6.backend.domain.Message;
import com.springw6.backend.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException exception) {
    String errorMessage = exception.getBindingResult()
        .getAllErrors()
        .get(0)
        .getDefaultMessage();

    return new ResponseEntity<>(Message.fail("BAD_REQUEST", errorMessage), HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<?> handlingInvalidTokenExceptions(){
    return new ResponseEntity<>(Message.fail("INVALID_TOKEN", "token is invalid")
            , HttpStatus.UNAUTHORIZED);
  }
  @ExceptionHandler(PostNotFoundException.class)
  public ResponseEntity<?> handlingPostNotFoundExceptions(){
    return new ResponseEntity<>(Message.fail("NOT_FOUND", "post id is not exist")
            , HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(SubCommentNotFoundException.class)
  public ResponseEntity<?> handlingSubCommentNotFoundExceptions(){
    return new ResponseEntity<>(Message.fail("NOT_FOUND", "sub comment id is not exist")
            ,HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(CommentNotFoundException.class)
  public ResponseEntity<?> handlingCommentNotFoundExceptions(){
    return new ResponseEntity<>(Message.fail("NOT_FOUND", "comment id is not exist")
            ,HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(NotAuthorException.class)
  public ResponseEntity<?> handlingNotAuthorExceptions(){
    return new ResponseEntity<>(Message.fail("BAD_REQUEST", "작성자가 아닙니다.")
            ,HttpStatus.BAD_REQUEST);

  }
}

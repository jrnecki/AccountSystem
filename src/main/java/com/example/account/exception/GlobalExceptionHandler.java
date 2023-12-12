package com.example.account.exception;

import com.example.account.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.account.type.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 커스텀한 예외
    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountException(AccountException e){
        log.error("{} is occured.", e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }
    // 자주 일어나는 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException is occured.",e);

        return new ErrorResponse(INVALID_REQUEST,INVALID_REQUEST.getDescription());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e){
        log.error("DataIntegrityViolationException",e);

        return new ErrorResponse(INVALID_REQUEST,INVALID_REQUEST.getDescription());
    }

    // 그외의 것들
    @ExceptionHandler(Exception.class)
    public ErrorResponse handlexception(AccountException e){
        log.error("Exception is occured.", e);

        return new ErrorResponse(
                INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR.getDescription());
    }
}

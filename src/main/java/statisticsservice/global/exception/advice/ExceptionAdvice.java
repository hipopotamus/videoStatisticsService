package statisticsservice.global.exception.advice;

import feign.RetryableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import statisticsservice.global.exception.BusinessLogicException;
import statisticsservice.global.exception.ExceptionCode;
import statisticsservice.global.exception.dto.ErrorResponse;

import java.io.IOException;

@Component
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> basicExceptionHandler(Exception e) {

         ErrorResponse errorResponse =
                 new ErrorResponse(e.getClass().getSimpleName(), e.getMessage(), ExceptionCode.GLOBAL_EXCEPTION.getCode());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> businessLoginExceptionHandler(BusinessLogicException e) {

        ExceptionCode exceptionCode = e.getExceptionCode();
        ErrorResponse errorResponse =
                new ErrorResponse(e.getClass().getSimpleName(), exceptionCode.getMessage(), exceptionCode.getCode());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(exceptionCode.getStatus()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> bindExceptionHandler(BindException e) {

        ExceptionCode bindException = ExceptionCode.BIND_EXCEPTION;
        ErrorResponse errorResponse =
                new ErrorResponse(e.getClass().getSimpleName(), bindException.getMessage(), bindException.getCode());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> ioExceptionHandler(IOException e) {

        ErrorResponse errorResponse =
                new ErrorResponse(e.getClass().getSimpleName(), e.getMessage(), ExceptionCode.IO_EXCEPTION.getCode());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorResponse> handleRetryableException(RetryableException e) {

        ErrorResponse errorResponse =
                new ErrorResponse(e.getClass().getSimpleName(), e.getMessage(), ExceptionCode.RETRYABLE_EXCEPTION.getCode());

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }
}

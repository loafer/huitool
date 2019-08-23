package com.huitool.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <描述信息>
 */
@ControllerAdvice
public class ServletExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServletExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, "数据解析失败", headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, "服务不可用", headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, "数据验证失败", headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Object body = null;
        if (ex.getBindingResult().hasErrors()) {
            List<FormErrorMessage> messages = ex.getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> new FormErrorMessage(fieldError.getField(), fieldError.getDefaultMessage()))
                    .collect(Collectors.toList());

            if (StringUtils.hasText(request.getHeader(HttpHeaders.ACCEPT))
                    && request.getHeader(HttpHeaders.ACCEPT).equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
                body = messages;
            } else {
                List<String> list = messages.stream()
                        .map(message -> message.getField() + ':' + message.getMessage())
                        .collect(Collectors.toList());
                body = StringUtils.collectionToCommaDelimitedString(list);
            }
        }

        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        logger.error("{}", status, ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ResponseEntity<Object> handleDataAccess(Exception ex, WebRequest request){
        return handleExceptionInternal(ex, "数据操作异常", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    static class FormErrorMessage{
        private String field;
        private String message;

        public FormErrorMessage(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}

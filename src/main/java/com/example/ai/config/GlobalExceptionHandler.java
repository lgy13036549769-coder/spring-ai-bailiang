package com.example.ai.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.ai.model.response.CommonResponse;

/**
 * 全局异常处理类，用于统一处理应用中的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有未捕获的异常
     * @param e 异常对象
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleException(Exception e) {
        CommonResponse<?> response = CommonResponse.error(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "Internal server error: " + e.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理AI相关异常
     * @param e AI异常对象
     * @return 统一响应结果
     */
    @ExceptionHandler(AIException.class)
    public ResponseEntity<CommonResponse<?>> handleAIException(AIException e) {
        CommonResponse<?> response = CommonResponse.error(
            HttpStatus.BAD_REQUEST.value(), 
            "AI service error: " + e.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * AI相关异常类
     */
    public static class AIException extends RuntimeException {
        public AIException(String message) {
            super(message);
        }

        public AIException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
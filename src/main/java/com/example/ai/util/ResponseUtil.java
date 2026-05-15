package com.example.ai.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.ai.model.response.CommonResponse;

/**
 * 响应工具类，用于处理响应相关的通用功能
 */
public class ResponseUtil {
    
    /**
     * 创建成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return ResponseEntity对象
     */
    public static <T> ResponseEntity<CommonResponse<T>> success(T data) {
        CommonResponse<T> response = CommonResponse.success(data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * 创建错误响应
     * @param statusCode HTTP状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity对象
     */
    public static <T> ResponseEntity<CommonResponse<T>> error(HttpStatus statusCode, String message) {
        CommonResponse<T> response = CommonResponse.error(statusCode.value(), message);
        return new ResponseEntity<>(response, statusCode);
    }
    
    /**
     * 创建错误响应（默认使用BAD_REQUEST状态码）
     * @param message 错误消息
     * @param <T> 数据类型
     * @return ResponseEntity对象
     */
    public static <T> ResponseEntity<CommonResponse<T>> error(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }
}
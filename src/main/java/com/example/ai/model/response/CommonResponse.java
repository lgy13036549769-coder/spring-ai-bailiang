package com.example.ai.model.response;

/**
 * 通用响应模型，用于统一API响应格式
 * @param <T> 响应数据类型
 */
public class CommonResponse<T> {
    
    private int code;
    private String message;
    private T data;
    
    // 构造方法
    public CommonResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    // Getters and Setters
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    /**
     * 成功响应静态方法
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 通用响应对象
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(200, "success", data);
    }
    
    /**
     * 错误响应静态方法
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 通用响应对象
     */
    public static <T> CommonResponse<T> error(int code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}
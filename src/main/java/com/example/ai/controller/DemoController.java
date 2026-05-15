package com.example.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai.model.response.CommonResponse;
import com.example.ai.util.ResponseUtil;

/**
 * 示例控制器，提供健康检查等基础接口
 */
@RestController
@RequestMapping("/demo")
public class DemoController {
    
    /**
     * 健康检查接口
     * @return 健康状态响应
     */
    @GetMapping("/health")
    public ResponseEntity<CommonResponse<String>> health() {
        return ResponseUtil.success("AI Service is running");
    }
    
    /**
     * 示例接口
     * @return 示例响应
     */
    @GetMapping("/example")
    public ResponseEntity<CommonResponse<String>> example() {
        return ResponseUtil.success("This is an example endpoint");
    }
}
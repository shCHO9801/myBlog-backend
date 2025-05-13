package com.shcho.myBlog.libs.dto;

public record ExceptionResponseDto(Integer status, String message) {

    public static ExceptionResponseDto of(Integer status, String message) {
        return new ExceptionResponseDto(status, message);
    }
}

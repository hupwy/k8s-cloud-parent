package com.itartisan.common.core.exception;

public class PageArgumentResolverException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PageArgumentResolverException() {
        super("分页参数解析失败！");
    }
}

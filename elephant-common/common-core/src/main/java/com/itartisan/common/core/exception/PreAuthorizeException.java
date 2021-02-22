package com.itartisan.common.core.exception;

/**
 * 权限异常
 */
public class PreAuthorizeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PreAuthorizeException() {
        super("没有权限，请联系管理员授权");
    }

    public PreAuthorizeException(String message){
        super(message);
    }
}

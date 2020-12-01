package com.max.network;

/**
 * @author: maker
 * @date: 2020/11/30 16:54
 * @description:
 */
public class ApiResponse<T> {
    public boolean success;
    public int status;
    public String message;
    public T body;

} 
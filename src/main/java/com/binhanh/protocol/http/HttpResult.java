package com.binhanh.protocol.http;

/**
 * Lưu trữ dữ liệu trả về từ server
 * @author lucnd
 *
 */
public class HttpResult<T>{

	public Throwable exception;

    public T result;

    public int requestId;

    public HttpResult() {
    	this.exception = null;
	}

    public boolean hasResult() {
        return result != null;
    }
	public void setResult(T taskResult) {
        this.result = taskResult;
    }

    public T getResult() {
        return result;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public boolean hasException() {
        return exception != null;
    }

    public Throwable getException() {
        return exception;
    }

}

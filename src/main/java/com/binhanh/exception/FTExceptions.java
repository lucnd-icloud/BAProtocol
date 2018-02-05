package com.binhanh.exception;
/**
 * Dùng để lưu trữ các exception cho việc xử lý sau
 * @author lucnd
 *
 */
public class FTExceptions extends Exception{

	private static final long serialVersionUID = 1L;
	
	private ErrorCode errorCodes;
	
	private String message;

	public FTExceptions(ErrorCode errorCodes) {
		super();
		this.errorCodes = errorCodes;
	}
	
	public FTExceptions(ErrorCode errorCodes, String log) {
		super();
		this.errorCodes = errorCodes;
		this.message = log;
	}

    public FTExceptions(String log) {
        this.errorCodes = ErrorCode.INVALIDE_VALUE;
        this.message = log;
    }

	public ErrorCode getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(ErrorCode errorCodes) {
		this.errorCodes = errorCodes;
	}

	/**
	 * @return the message
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}

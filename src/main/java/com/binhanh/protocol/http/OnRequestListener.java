package com.binhanh.protocol.http;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * interface cho việc xử lý dữ liệu trả về từ server
 * @author Nguyen Duc Luc
 *
 */
public interface OnRequestListener<T> {
	
	/**giá trị key khi request lên server*/
	public static final String REQ_PARAM = "Param";
	
	/**giá trị tự động tăng, là id của request*/
	public static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

	public Parameter buildParams(String params);
	public void handleExeption(Exception e);
	public void postExecute(T data);
}

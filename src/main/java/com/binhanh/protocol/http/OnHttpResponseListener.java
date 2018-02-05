package com.binhanh.protocol.http;


/**
 * Là interface, dùng để cập nhật kết quả trả về khi nhận dữ liệu từ server
 * @author lucnd
 *
 */
public interface OnHttpResponseListener<T> {

	/**
	 * Cập nhật kết quả nhận về từ server
	 * @param response: dữ liệu đối tượng trả về
	 * @param reqId: đây là request id được gửi từ trước
	 */
	public void onHttpComplete(int reqId, T response);

    /**
     * Cập nhật kết quả nhận về từ server
     * @param throwable: xảy ra lỗi
     * @param reqId: đây là request id được gửi từ trước
     */
    public void onHttpError(int reqId, Throwable throwable);
}

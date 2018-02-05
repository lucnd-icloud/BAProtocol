package com.binhanh.protocol.http;


/**
 * lớp này không xử lý lỗi server trả về
 * @author lucnd
 *
 */
public abstract class SingleHttpResponse<T> implements OnHttpResponseListener {
    /**
     * Cập nhật kết quả nhận về từ server
     * @param throwable: xảy ra lỗi
     * @param reqId: đây là request id được gửi từ trước
     */
    public void onHttpError(int reqId, Throwable throwable){
        //ko xử lý gì
    }
}

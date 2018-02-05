package com.binhanh.protocol.http;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;

import com.binhanh.exception.ErrorCode;
import com.binhanh.exception.FTExceptions;
import com.binhanh.protocol.http.Parameter.RequestMethod;

/**
 * lớp này dùng để xử lý khi có thể thực hiện nhiều request cùng 1 lúc
 */

public abstract class RequestExecute<Data, Response> implements ServiceConnection.Callback<Response> {

    protected OnHttpResponseListener<Response> listener;

    /**Lớp lưu trữ danh sách các task đang request*/
    @Nullable
    private SparseArray<ServiceConnection<Data, Response>> mServiceConnections;

    /**Dữ liệu cần gửi đi*/
    private Data dataData;

    public RequestExecute(){}

    /**
     * @param listener
     */
    public RequestExecute(OnHttpResponseListener<Response> listener){
        this.listener = listener;
    }

    public abstract String getUrl();

    public abstract IRequestMethodName getMethodName();

    public String getKeyParam(){
        return OnRequestListener.REQ_PARAM;
    }

    public int getRequestTimeout(){
        return Parameter.REQUEST_TIMEOUT;
    }

    /**
     * sử dụng hàm {{@link #getMethodName()}}
     * @return
     */
    @Deprecated
    public IRequestMethodName getRequestMethod(){
        return getMethodName();
    }

    public @RequestMethod int getRequestMethodType() {
        return Parameter.POST;
    }

    private void onHttpComplete(int reqId, Response t) {
        if(listener != null){
            listener.onHttpComplete(reqId, t);
        }
    }

    private void onHttpError(int reqId, Throwable throwable) {
        if(listener != null){
            listener.onHttpError(reqId, throwable);
        }
    }

    @MainThread
    @Override
    public synchronized void apply(int requestId, HttpResult<Response> result) {

        //xóa queue task request
        if(mServiceConnections != null && mServiceConnections.size() > 0){
            mServiceConnections.remove(requestId);
        }

        //xử lý nhận dữ liệu trả về
        if(result.hasException() || result.getResult() == null){
            if(result.getResult() == null) result.setException(new FTExceptions(ErrorCode.NO_RESULT_ERROR));
            Log.d("","doRequest không nhận được dữ liệu từ server: " + result.getException());
            onHttpError(requestId, result.getException());
        }else{
            onHttpComplete(requestId, result.getResult());
        }
    }

    public int request() {
        return request(null, dataData, 1, true);
    }

    public int request(Data t) {
        return request(null, t, 1, true);
    }

    public int request(IRequestMethodName methodName, Data t) {
        return request(methodName, t, 1, true);
    }

    public int request(Data t, int number) {
        return request(null, t, number, true);
    }

    public int request(Data t, int number, boolean isEncrypt) {
        return request(null, t, number, isEncrypt);
    }

    public int request(@NonNull IRequestMethodName methodName, Data t, int number, boolean isEncrypt) {
        this.dataData = t;
        //khởi tạo danh sách lưu trữ task
        if(mServiceConnections == null){
            mServiceConnections = new SparseArray<>(1);
        }
        ServiceConnection<Data, Response> connection = new ServiceConnection<>(this, t);
        mServiceConnections.put(connection.getRequestId(), connection);
        Parameter parameter = getParameter(number, isEncrypt);
        parameter.setMethodName(methodName.getName());
        connection.execute(parameter);
        return connection.getRequestId();
    }

    private Parameter getParameter(int number, boolean isEncrypt){
        Parameter parameter = new Parameter();
        // thiết lập dữ liệu tùy chọn cho khách hàng khi tìm kiếm
        parameter.setUrl(getUrl());
        if(getMethodName() != null){
            parameter.setMethodName(getMethodName().getName());
        }
        parameter.setRequestMethod(getRequestMethodType());
        parameter.setRequestTimeout(getRequestTimeout());
        parameter.setEncrypt(isEncrypt);
        parameter.setRequestRetry(number);
        return parameter;
    }
    /**
     * Hủy request
     */
    public void cancel(){
        if(mServiceConnections != null && mServiceConnections.size() > 0){
            for (int i = 0; i < mServiceConnections.size(); i++){
                mServiceConnections.valueAt(i).cancel(true);
            }
        }
    }

    /**
     * lấy dữ liệu request
     * @return
     */
    public Data getData(){
        return dataData;
    }

    /**
     * thiết lập dữ liệu request
     * @param dataData
     * @return
     */
    public RequestExecute setData(Data dataData){
        this.dataData = dataData;
        return this;
    }

    public RequestExecute setListener(OnHttpResponseListener<Response> listener) {
        this.listener = listener;
        return this;
    }
}

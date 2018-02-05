package com.binhanh.protocol.http;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.binhanh.exception.ErrorCode;
import com.binhanh.exception.FTExceptions;
import com.binhanh.utils.ByteUtils;
import com.binhanh.utils.Log;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Lớp kết nối lấy dữ liệu từ server
 * @author Nguyen Duc Luc
 *
 */
public class ServiceConnection<Data, Response> extends AsyncTask<Parameter, Void, HttpResult<Response>>{

    /**giá trị tự động tăng, là id của request*/
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();

    public static final String REQ_PARAM = "Param";

    /**id của request*/
    private int requestId;

    private Callback<Response> mCallback;

    /**Dữ liệu cần gửi đi*/
    private Data data;

    /**
     * xử lý cho các loại đối tượng <T> không phải là List, Array
     * @param responseListener
     */
    public ServiceConnection(@NonNull Callback<Response> responseListener, Data data) {
        this.mCallback = responseListener;
        this.requestId = ATOMIC_INTEGER.incrementAndGet();
        this.data = data;
    }

    @Override
    protected HttpResult<Response> doInBackground(Parameter... params) {

        if(params == null || params.length == 0) return null;

        Parameter parameter = params[0];

        try {
            byte[] input;
            if(data instanceof byte[]){
                input = (byte[])data;
            }else{
                input = ByteUtils.serialize(data);
            }
            String base64 = encode(input, parameter.isEncrypt());
            parameter.addParameters(REQ_PARAM, base64);
            return doRequest(parameter);
        } catch (Exception e) {
            Log.e("",e);
        }
        return null;
    }

    /**
     * Hàm đệ quy để gọi request lên server
     * @param parameter
     * @return ServiceTaskResult
     */
    private HttpResult<Response> doRequest(Parameter parameter){
        HttpResult<Response> result = new HttpResult<>();
        if(parameter.getRequestRetry() > 0){
            try {
                String data;

                if(parameter.getRequestMethod() == Parameter.POST){

                    //request dữ liệu theo phương thức POST
                    data = HttpUtils.getServiceByPost(parameter);

                }else{
                    //request lấy dữ liệu theo phương thức GET
                    data = HttpUtils.getServiceByGet(parameter);
                }

                byte[] buffer = decode(data, parameter.isEncrypt());

                Class<Response> tClass = getGenericType();
                if(tClass != null){
                    result.setResult(ByteUtils.deserializeClass(tClass, buffer));
                }else{
                    result.setResult((Response)buffer);
                }
            } catch (Exception e) {
                if (e instanceof FTExceptions) {
                    result.setException(e);
                    parameter.setRequestRetry(parameter.getRequestRetry() - 1);
                    doRequest(parameter);
                }
            }
        }else{
            result.setException(new FTExceptions(ErrorCode.NO_RESULT_ERROR));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Class<Response> getGenericType() {
        try {
            ParameterizedType parameterizedType = (ParameterizedType) mCallback.getClass().getGenericSuperclass();
            Type type = parameterizedType.getActualTypeArguments()[1];
            return (Class<Response>) type;
        } catch (Exception e) {
            Log.w("", e);
        }
        return null;
    }


    @Override
    protected void onPostExecute(HttpResult<Response> result) {
        result.requestId = requestId;
        mCallback.apply(requestId, result);
    }

    /**
     * Mã hóa tham số
     * @param params
     * @return
     */
    public static String encode(byte[]params, boolean isCrypt){

        if(params == null || params.length == 0) return "";

        if(!isCrypt){
            return Base64.encodeToString(params, Base64.DEFAULT);
        }

        //mã hóa base64
        return HttpUtils.httpEnCodeV2(params);
    }
    /**
     * giải mã thông tin từ server trả về
     * @param data
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decode(String data, boolean isCrypt) throws Exception{

        //nếu không có dữ liệu
        if(data.isEmpty())
            throw new FTExceptions(ErrorCode.NO_RESULT_ERROR);

//		LogFile.e("response: " + data);

        if(!isCrypt){
            return Base64.decode(data, Base64.DEFAULT);
        }

        return HttpUtils.httpDeCodeV2(data);
    }

    public int getRequestId() {
        return requestId;
    }

    public Data getData() {
        return data;
    }

    public interface Callback<T>{
        void apply(int requestid, HttpResult<T> t);
    }
}

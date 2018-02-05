package com.binhanh.protocol.http;

import android.support.annotation.IntDef;

import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Lưu trữ các thuộc tính và tham số đầu vào để gửi tới server
 * @author Nguyen Duc Luc
 *
 */
public class Parameter {

    public static final int REQUEST_TIMEOUT = 10*1000;

	/**Link để kết nối với server*/
	private String url;
	
	/**Tên phương thức kết nối*/
	private String methodName;
	
	/**Thời gian timeout cho request*/
	private int requestTimeout = REQUEST_TIMEOUT;

	/**Danh sách tham số request*/
	private HashMap<String, String> parameters;
	
	/**Loại paramater kiểu gì: text bình thường (param = value) hay kiểu json*/
	private @ParamType int contentType = TEXT;

	/**Loại request*/
	private @RequestMethod int requestMethod = POST;

	/**có mã hóa database không*/
	private boolean isEncrypt;

	/**số lượng request thử lại*/
	private int requestRetry;

	/**Lớp lưu thông tin chung*/
	public Object tag;

	public Parameter() {}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}

	public int getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public void addParameters(String key, String value){
	    if(parameters == null) parameters  = new HashMap<>(1);
		parameters.put(key, value);
	}
	
	/**
	 * @return the contentType
	 */
	public @ParamType int getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(@ParamType int contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * @return the requestMethod
	 */
	public @RequestMethod int getRequestMethod() {
		return requestMethod;
	}

	/**
	 * @param requestMethod the requestMethod to set
	 */
	public void setRequestMethod(@RequestMethod int requestMethod) {
		this.requestMethod = requestMethod;
	}

    public boolean isEncrypt() {
        return isEncrypt;
    }

    public void setEncrypt(boolean encrypt) {
        isEncrypt = encrypt;
    }

    public int getRequestRetry() {
        return requestRetry;
    }

    public void setRequestRetry(int requestRetry) {
        this.requestRetry = requestRetry;
    }

    /**
     * Loại tham số cần tạo
     */
    public static final int TEXT = 0;
    public static final int JSON = 1;

    @IntDef({TEXT, JSON})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ParamType{}

    /**
     * đây là loại phương thức request
     */
	public static final int GET = 0;
    public static final int POST = 1;

    @IntDef({GET, POST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestMethod{}

    public static String getQuery(HashMap<String, String> parameterList,
                                  int ContentType) {
        String result = "";
        switch (ContentType) {
            case Parameter.TEXT:
                result = raiseURLParams(parameterList);
                break;

            case Parameter.JSON:
                result = raiseJSONParams(parameterList);
                break;

            default:
                break;
        }
        return result;
    }

    /**
     * tạo param theo thuộc tính key=value
     *
     * @param parameterList
     * @return
     */
    public static String raiseURLParams(HashMap<String, String> parameterList) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Set<String> set = parameterList.keySet();
        Iterator<String> iterator = set.iterator();
        try {
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = parameterList.get(key);
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value, "UTF-8"));
            }

        } catch (Exception e) {
            first = true;
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = parameterList.get(key);
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(key);
                result.append("=");
                result.append(value);
            }
        }

        return result.toString();
    }

    /**
     * tạo param theo chuỗi json
     *
     * @param parameterList
     * @return
     */
    public static String raiseJSONParams(HashMap<String, String> parameterList) {
        JSONObject result = new JSONObject();
        try {
            Set<String> set = parameterList.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = parameterList.get(key);
                result.put(key, value);
            }
        } catch (Exception e) {
            // Log.e(TAG,"Không thể tạo tham số json");
        }
        return result.toString();
    }

    /**
     * Thiết lập URL
     *
     * @param parameter
     *            : Tham số config
     * @param isGetMethod
     *            : là phương thức gửi true là GET và false là POST
     * @return
     */

    public static String convertURL(Parameter parameter,
                                    boolean isGetMethod, int contentType) {
        StringBuilder url = convertURL(parameter);
        if (parameter != null && isGetMethod) {
            if (parameter.getParameters() != null) {
                // thêm các tham số
                url.append("?");
                HashMap<String, String> parameterList = parameter
                        .getParameters();
                url.append(getQuery(parameterList, contentType));
            }
        }
        return url.toString();

    }

    /**
     * convert url
     * @param configParameters
     * @return
     */
    public static StringBuilder convertURL(Parameter configParameters) {
        StringBuilder url = new StringBuilder("");
        if (configParameters != null) {

            // thêm url
            url.append(configParameters.getUrl());

            //thêm flash
            url.append("/");

            //thêm phương thức
            url.append(configParameters.getMethodName());
        }
        return url;
    }
}

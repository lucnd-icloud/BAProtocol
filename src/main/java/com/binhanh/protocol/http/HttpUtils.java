package com.binhanh.protocol.http;

import android.util.Base64;
import android.util.Log;

import com.binhanh.exception.ErrorCode;
import com.binhanh.exception.FTExceptions;
import com.binhanh.utils.BinaryUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Random;

/**
 * kết nối, lấy dữ liệu từ server và xử lý timeout
 *
 * @author lunnd
 *
 */
public class HttpUtils {

    private static final String TAG = "HttpUtils";
    private static final int BUFFER_SIZE = 1024;
    private static final String CHARSET = "UTF-8";

    /**
     * Lấy dữ liệu từ server bằng cách truyền url
     *
     * @param parameter
     * @return String
     * @throws IOException
     */
    public static String getServiceByGet(Parameter parameter) throws Exception {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(Parameter.convertURL(parameter, true, parameter.getContentType()));

            Log.d(TAG, url.toString());
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Thiết lập timeout
            urlConnection.setConnectTimeout(parameter.getRequestTimeout());
            urlConnection.setReadTimeout(parameter.getRequestTimeout());

            // Kết nối đến tài nguyên
            urlConnection.connect();

            // lấy mã kết quả
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Reading data from url
                iStream = urlConnection.getInputStream();
                data = convertInputStreamToString(iStream);
            } else {
                Log.e(TAG, "Response Code:" + responseCode);
            }

            if (iStream != null) {
                iStream.close();
            }

        } catch (MalformedURLException e) {// xảy ra khi url không đúng định
            // dạng
            // Log.e(TAG, e.toString());
        } catch (ConnectException e) {
            // Xảy ra khi kết nối bị từ chối
            throw new FTExceptions(ErrorCode.REFUSE_CONNECTION);
        } catch (SocketTimeoutException e) {
            // xảy ra khi thời gian kết nối vượt quá mức cho phép
            throw new FTExceptions(ErrorCode.TIMEOUT_CONNECTION);

        } catch (SocketException e) {
            // xảy ra khi thời gian kết nối vượt quá mức cho phép
            throw new FTExceptions(ErrorCode.REFUSE_CONNECTION);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return data;
    }

    /**
     * Lấy dữ liệu từ server bằng cách truyền url
     *
     * @param link
     * @return String
     * @throws IOException
     */
    public static String getServiceByUrl(String link) throws Exception {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(link);

            Log.d(TAG, url.toString());
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Thiết lập timeout
            urlConnection.setConnectTimeout(Parameter.REQUEST_TIMEOUT);
            urlConnection.setReadTimeout(Parameter.REQUEST_TIMEOUT);

            // Kết nối đến tài nguyên
            urlConnection.connect();

            // lấy mã kết quả
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Reading data from url
                iStream = urlConnection.getInputStream();
                data = convertInputStreamToString(iStream);
            } else {
                Log.e(TAG, "Response Code:" + responseCode);
            }

            if (iStream != null) {
                iStream.close();
            }

        } catch (MalformedURLException e) {// xảy ra khi url không đúng định
            // dạng
            // Log.e(TAG, e.toString());
        } catch (ConnectException e) {
            // Xảy ra khi kết nối bị từ chối
            throw new FTExceptions(ErrorCode.REFUSE_CONNECTION);
        } catch (SocketTimeoutException e) {
            // xảy ra khi thời gian kết nối vượt quá mức cho phép
            throw new FTExceptions(ErrorCode.TIMEOUT_CONNECTION);

        } catch (SocketException e) {
            // xảy ra khi thời gian kết nối vượt quá mức cho phép
            throw new FTExceptions(ErrorCode.REFUSE_CONNECTION);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return data;
    }

    /**
     * Lấy dữ liệu từ server bằng cách truyền theo phương thức POST
     *
     * @param params
     * @return
     * @throws IOException
     */
    public static String getServiceByPost(Parameter params) throws Exception {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            // tạo kết nối
            urlConnection = getHttpURLConnectionByPost(params);

            // gửi dữ liệu lên server
            writeStream(urlConnection, params);

            // Kết nối đến tài nguyên
            urlConnection.connect();
            // lấy mã kết quả
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // nhận kết quả trả về từ server
                iStream = urlConnection.getInputStream();
                data = convertInputStreamToString(iStream);
            } else {
                Log.e(TAG, "Response Code:" + responseCode);
            }

            if (iStream != null) {
                iStream.close();
            }

        } catch (MalformedURLException e) {// xảy ra khi url không đúng định
            // dạng
            // Log.e(TAG, e.toString());
        } catch (ConnectException e) {// Xảy ra khi kết nối bị từ chối
            throw new FTExceptions(ErrorCode.REFUSE_CONNECTION);
        } catch (SocketTimeoutException e) {// xảy ra khi thời gian kết nối vượt
            throw new FTExceptions(ErrorCode.TIMEOUT_CONNECTION);
        } catch (Exception e) {
            throw e;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }

        return data;
    }

    public static HttpURLConnection getHttpURLConnectionByPost(Parameter params) throws Exception{
        HttpURLConnection urlConnection = null;
        String urlParameters = "";
        urlParameters = Parameter.getQuery(params.getParameters(), params.getContentType());

        StringBuilder strUrl = Parameter.convertURL(params);

        URL url = new URL(strUrl.toString());

        Log.d(TAG, strUrl + "?" + urlParameters);

        // tạo kết nối
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setChunkedStreamingMode(0);
        urlConnection.setRequestProperty("Accept-Charset", CHARSET);
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection
                .setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Content-Length", ""
                + urlParameters.getBytes().length);
        urlConnection.setConnectTimeout(params.getRequestTimeout());
        urlConnection.setReadTimeout(params.getRequestTimeout());
        return urlConnection;
    }

    /**
     * gửi param theo stream lên server
     * @param urlConnection
     * @param params
     * @throws Exception
     */
    public static void writeStream(HttpURLConnection urlConnection, Parameter params) throws Exception{
        String urlParameters = Parameter.getQuery(params.getParameters(), params.getContentType());
        OutputStream os = urlConnection.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(os, CHARSET);
        writer.write(urlParameters);
        writer.flush();
        writer.close();
        os.close();
    }



    /**
     * chuyển đổi inputstream thành mảng byte
     *
     * @param in
     * @return
     */
    public static byte[] convertInputStreamToBytes(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] tempBuffer = new byte[BUFFER_SIZE];
        try {
            while (true) {
                int r = in.read(tempBuffer, 0, BUFFER_SIZE);
                if (r == -1)
                    break;
                out.write(tempBuffer, 0, r);
            }
            out.flush();
        } catch (IOException e) {
            return null;
        }
        return out.toByteArray();
    }

    // convert inputstream to String
    public static String convertInputStreamToString(InputStream inputStream) {
        String result = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    inputStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString().trim();
            br.close();
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * Mã hóa dữ liệu qua http
     * @param p
     * @return
     */
    public static String httpEnCode(byte[] p)
    {
        int a, c, e;
        Random random = new Random();

        a = random.nextInt(6) + 10;
        c = random.nextInt(6) + 10;

        byte[] f = new byte[a];
        byte[] g = new byte[c];
        byte[] k;

        random.nextBytes(f);
        random.nextBytes(g);
        if(a >= c){
            k = new byte[a];
            int i = 0;
            for(; i < c; i++){
                k[i] = (byte)(f[i] ^ g[i]);
            }
            for (; i < a; i++)
            {
                k[i] = f[i];
            }
        }
        else
        {
            k = new byte[c];
            int i = 0;
            for (; i < a; i++)
            {
                k[i] = (byte)(f[i] ^ g[i]);
            }
            for (; i < c; i++)
            {
                k[i] = g[i];
            }
        }

        e = p.length;

        int q = a + c + e + 4;
        int s = 0;
        if (q < 128)
        {
            s = 131 - q;
        }
        int l = s + q;

        byte[] b = new byte[l];

        b[0] = (byte)a;
        BinaryUtils.putShort(b, (short)e, 1);
        b[3] = (byte)c;
        int o = 4;
        for (int j = 0; j < a; j++)
        {
            b[o + j] = f[j];
        }
        o += a;
        for (int j = 0; j < c; j++)
        {
            b[o + j] = g[j];
        }
        o += c;
        int t = k.length;
        for (int j = 0; j < e; j++)
        {
            b[o + j] = (byte)(p[j] ^ k[j % t]);
        }
        o += e;
        if (s > 0)
        {
            b[o] = (byte)s;
            o += 1;
            byte[] r = new byte[s];
            random.nextBytes(r);
            for (int j = 0; j < s - 1; j++)
            {
                b[o + j] = r[j];
            }
        }

        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    /**
     * Giải mã dữ liệu qua http
     * @param text
     * @return
     */
    public static byte[] httpDeCode(String text)
    {
        byte[] b = Base64.decode(text, Base64.DEFAULT);
        //byte[] b = System.Text.Encoding.UTF8.GetBytes(text);

        int c, e, f;
        c = b[0];
        f = BinaryUtils.getShort(b, 1);
        e = b[3];
        int a = 4;
        byte[] g = new byte[c];
        for (int j = 0; j < c ; j++)
        {
            g[j] = b[4+j];
        }
        a += c;
        byte[] h = new byte[e];
        for (int j = 0; j < e; j++)
        {
            h[j] = b[a + j];
        }
        a += e;

        byte[] k;
        if (c >= e)
        {
            k = new byte[c];
            int i = 0;
            for (; i < e; i++)
            {
                k[i] = (byte)(g[i] ^ h[i]);
            }
            for (; i < c; i++)
            {
                k[i] = g[i];
            }
        }
        else
        {
            k = new byte[e];
            int i = 0;
            for (; i < c; i++)
            {
                k[i] = (byte)(g[i] ^ h[i]);
            }
            for (; i < e; i++)
            {
                k[i] = h[i];
            }
        }
        //int off = k1l + k2l + 4;
        int m = k.length;
        byte[] d = new byte[f];
        for (int j = 0; j < f; j++)
        {
            d[j] = (byte)(b[a + j] ^ k[j % m]);
        }

        return d;
    }

    /**
     * Mã hóa dữ liệu qua http
     * @param p
     * @return
     */
    public static String httpEnCodeV2(byte[] p)
    {
        int a, c, e;
        Random random = new Random();

        a = random.nextInt(6) + 10;
        c = random.nextInt(6) + 10;

        byte[] f = new byte[a];
        byte[] g = new byte[c];
        byte[] k;

        random.nextBytes(f);
        random.nextBytes(g);
        if(a >= c){
            k = new byte[a];
            int i = 0;
            for(; i < c; i++){
                k[i] = (byte)(f[i] ^ g[i]);
            }
            for (; i < a; i++)
            {
                k[i] = f[i];
            }
        }
        else
        {
            k = new byte[c];
            int i = 0;
            for (; i < a; i++)
            {
                k[i] = (byte)(f[i] ^ g[i]);
            }
            for (; i < c; i++)
            {
                k[i] = g[i];
            }
        }

        e = p.length;

        int q = a + c + e + 6;
        int s = 0;
        if (q < 128)
        {
            s = 133 - q;
        }
        int l = s + q;

        byte[] b = new byte[l];

        b[0] = (byte)a;
        BinaryUtils.putInt(b, e, 1);
        b[5] = (byte)c;
        int o = 6;
        for (int j = 0; j < a; j++)
        {
            b[o + j] = f[j];
        }
        o += a;
        for (int j = 0; j < c; j++)
        {
            b[o + j] = g[j];
        }
        o += c;
        int t = k.length;
        for (int j = 0; j < e; j++)
        {
            b[o + j] = (byte)(p[j] ^ k[j % t]);
        }
        o += e;
        if (s > 0)
        {
            b[o] = (byte)s;
            o += 1;
            byte[] r = new byte[s];
            random.nextBytes(r);
            for (int j = 0; j < s - 1; j++)
            {
                b[o + j] = r[j];
            }
        }

        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    /**
     * Giải mã dữ liệu qua http
     * @param text
     * @return
     */
    public static byte[] httpDeCodeV2(String text)
    {
        byte[] b = Base64.decode(text, Base64.DEFAULT);
        //byte[] b = System.Text.Encoding.UTF8.GetBytes(text);

        int c, e, f;
        c = b[0];
        f = BinaryUtils.getShort(b, 1);
        e = b[5];
        int a = 6;
        byte[] g = new byte[c];
        for (int j = 0; j < c ; j++)
        {
            g[j] = b[6+j];
        }
        a += c;
        byte[] h = new byte[e];
        for (int j = 0; j < e; j++)
        {
            h[j] = b[a + j];
        }
        a += e;

        byte[] k;
        if (c >= e)
        {
            k = new byte[c];
            int i = 0;
            for (; i < e; i++)
            {
                k[i] = (byte)(g[i] ^ h[i]);
            }
            for (; i < c; i++)
            {
                k[i] = g[i];
            }
        }
        else
        {
            k = new byte[e];
            int i = 0;
            for (; i < c; i++)
            {
                k[i] = (byte)(g[i] ^ h[i]);
            }
            for (; i < e; i++)
            {
                k[i] = h[i];
            }
        }
        //int off = k1l + k2l + 4;
        int m = k.length;
        byte[] d = new byte[f];
        for (int j = 0; j < f; j++)
        {
            d[j] = (byte)(b[a + j] ^ k[j % m]);
        }

        return d;
    }
}

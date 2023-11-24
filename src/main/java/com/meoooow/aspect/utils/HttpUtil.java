package com.meoooow.aspect.utils;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpClient工具类
 */
@SuppressWarnings("unused")
@Slf4j
public class HttpUtil {

    public static int DEFAULT_TIME_OUT = 10;
    // 设置超时时间
    private final static RequestConfig config = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofSeconds(DEFAULT_TIME_OUT))
            .setResponseTimeout(Timeout.ofSeconds(DEFAULT_TIME_OUT))
            .build();
    private final static CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(config).build();

    /**
     * get请求
     *
     * @return string
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * get请求
     *
     * @param url    String
     * @param header 请求头
     * @return string
     */
    public static String get(String url, Map<String, String> header) {
        if (header == null) {
            header = Maps.newHashMap();
        }
        return getResult(new HttpGet(url), null, header);
    }

    /**
     * post请求(用于key-value格式的参数)
     *
     * @param url    String
     * @param params Map
     * @return result
     */
    public static String post(String url, Map<String, Object> params) {
        return post(url, params, null);
    }

    /**
     * post请求(application/x-www-form-urlencoded; charset=utf-8)
     *
     * @param url    String
     * @param params Map
     * @param header 请求头
     * @return result
     */
    public static String post(String url, Map<String, Object> params, Map<String, String> header) {
        if (header == null) {
            header = Maps.newHashMap();
        }
        HttpPost httpPost = new HttpPost(url);
        // 表单参数
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        // POST 请求参数
        params.forEach((k, v) -> nameValuePairs.add(new BasicNameValuePair(k, (String) v)));
        nameValuePairs.add(new BasicNameValuePair("password", "secret"));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        return getResult(httpPost, params, header);
    }


    /**
     * post请求（用于请求json格式的参数）
     *
     * @param url    String
     * @param params jsonString
     * @return result
     */
    public static String post(String url, String params) {
        return post(url, params, null);
    }

    /**
     * post请求（用于请求json格式的参数）
     *
     * @param url    String
     * @param params jsonString
     * @param header 请求头
     * @return result
     */
    public static String post(String url, String params, Map<String, String> header) {
        if (StringUtils.isBlank(params)) {
            params = "";
        }
        if (header == null) {
            header = Maps.newHashMap();
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new StringEntity(params, ContentType.APPLICATION_JSON));
        return getResult(httpPost, params, header);
    }


    /**
     * 获取响应结果
     *
     * @param request 请求
     * @return String
     */
    private static String getResult(HttpUriRequestBase request, Object params, Map<String, String> header) {
        LocalDateTime startTime = LocalDateTime.now();
        String result = null;
        Exception exception = null;
        try {
            header.forEach(request::setHeader);
            result = httpclient.execute(request, response -> {
                // 处理响应内容
                if (response.getCode() == HttpStatus.OK.value()) {
                    return EntityUtils.toString(response.getEntity());
                }
                return null;
            });
        } catch (IOException e) {
            exception = e;
        } finally {
            PrintLog printLog = new PrintLog();
            printLog.printLog(startTime, request.getRequestUri(), request.getMethod(), header, params, result, exception);
        }
        return result;
    }

}

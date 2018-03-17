package com.secrething.tools.client;

import com.secrething.tools.common.RequestEntity;

import java.util.Map;

/**
 * Created by songsusu on 16/5/11.
 */
public class ProxyHttpPoolManage {
    public static final String CONTENT_TYPE_KEY = "Content-Type";
    public static final String CONTENT_TYPE_VALUE = "text/xml;charset=UTF-8";

    public static final String USER_AGENT_KEY = "User-Agent";
    public static final String USER_AGENT_VALUE = "Apache-HttpClient/4.1.1";

    public static final String ACCEPT_KEY = "Accept";
    public static final String ACCEPT_VALUE = "*/*";

    public static final String ACCEPT_LANGUAGE_KEY = "Accept-Language";
    public static final String ACCEPT_LANGUAGE_VALUE = "zh-cn";

    public static final String ACCEPT_ENCODING_KEY = "Accept-Encoding";
    public static final String ACCEPT_ENCODING_VALUE = "gzip, deflate";

    public static final String SOAPACTION = "SOAPAction";
    public static final String HOST = "Host";
    public static final String ENTITY = "entity";
    public static final String SESSION = "Session";

    private final static String proxy(String methodName, Object... params) {
        RequestEntity entity = new RequestEntity();
        entity.setParams(params);
        entity.setMethodName(methodName);
        try {
            Client client = new Client();
            return client.sendRequest(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    /**
     * @param url      数据库配置
     * @param request  请求数据
     * @param waittime 等待时长
     * @param domain   供应商
     * @return
     */
    public static String sendJsonPostRequestWithDomain(String url, String request, int waittime, String domain) {
        return proxy("sendJsonPostRequestWithDomain", url, request, waittime, domain);
    }


    public static String sendJsonPostRequestwithProxy(String url, String request, int waittime, String hosts, int port) {
        return proxy("sendJsonPostRequestwithProxy", url, request, waittime, hosts, port);
    }


    public static String sendJsonPostRequest(String url, String request, int waittime) {
        return proxy("sendJsonPostRequest", url, request, waittime);
    }

    public static String sendSoapZIPPostRequest(String url, String request, int connecttime, int waittime, String contentType) {
        return proxy("sendSoapZIPPostRequest", url, request, connecttime, waittime, contentType);
    }

    public static String sendJsonPostRequest(String url, String request, int waittime, String contentType) {
        return proxy("sendJsonPostRequest", url, request, waittime, contentType);
    }

    public static String sendWsldPostRequestWithTLSv1_2(String url, String request, int connecttimeout, int waittime, String soapAction, String host, String contenttype, String proxIp, int proxport) {
        return proxy("sendWsldPostRequestWithTLSv1_2", url, request, connecttimeout, waittime, soapAction, host, contenttype, proxIp, proxport);
    }


    public static String sendWsldPostRequest(String url, String request, int connecttimeout, int waittime, String soapAction, String host, String contenttype, String proxIp, int proxport) {
        return proxy("sendWsldPostRequest", url, request, connecttimeout, waittime, soapAction, host, contenttype, proxIp, proxport);
    }


    public static String sendPostRequest(String url, Map<String, String> requestMap, int waittime) {
        return proxy("sendPostRequest", url, requestMap, waittime);
    }

    public static String sendGetRequest(String url, int connectTimeOut, int waittime) {
        return proxy("sendGetRequest", url, connectTimeOut, waittime);
    }


    public static String sendGetRequest(String url, int connectTimeOut, int waittime, String contentType) {
        return proxy("sendGetRequest", url, connectTimeOut, waittime, contentType);
    }


    public static String sendGetRequest(String url, int waittime) {
        return proxy("sendGetRequest", url, waittime);
    }


}

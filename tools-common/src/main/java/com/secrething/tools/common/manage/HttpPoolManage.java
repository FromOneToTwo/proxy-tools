package com.secrething.tools.common.manage;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by songsusu on 16/5/11.
 */
public class HttpPoolManage {
    private final static Logger logger = LoggerFactory.getLogger(HttpPoolManage.class);
    protected static CloseableHttpClient closeableHttpClient = null;

    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "text/xml;charset=UTF-8";

    private static final String USER_AGENT_KEY = "User-Agent";
    private static final String USER_AGENT_VALUE = "Apache-HttpClient/4.1.1";

    private static final String ACCEPT_KEY = "Accept";
    private static final String ACCEPT_VALUE = "*/*";

    private static final String ACCEPT_LANGUAGE_KEY = "Accept-Language";
    private static final String ACCEPT_LANGUAGE_VALUE = "zh-cn";

    private static final String ACCEPT_ENCODING_KEY = "Accept-Encoding";
    private static final String ACCEPT_ENCODING_VALUE = "gzip, deflate";

    private static final String SOAPACTION = "SOAPAction";
    private static final String HOST = "Host";
    private static final String ENTITY = "entity";
    private static final String SESSION = "Session";
    private static X509TrustManager xtm = null;
    private static X509HostnameVerifier hostnameVerifier = null;

    static {
        generatorHttpClient();
    }

    private static void generatorHttpClient() {
        xtm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        hostnameVerifier = new X509HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }

            public void verify(String arg0, SSLSocket arg1) throws IOException {
            }

            public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {
            }

            public void verify(String arg0, X509Certificate arg1) throws SSLException {
            }
        };

        try {

            PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager();
            poolingmgr.setMaxTotal(6000);
            poolingmgr.setDefaultMaxPerRoute(2000);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            closeableHttpClient = HttpClients.custom()
                    .setConnectionManager(poolingmgr).setSSLSocketFactory(sslsf).disableAutomaticRetries().build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private static CloseableHttpClient generatorOneHttpClient() {

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{xtm}, null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf).disableAutomaticRetries().build();
            return httpClient;
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    private static CloseableHttpClient generatorOneHttpClientWithTLSv1_2() {

        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, new TrustManager[]{xtm}, null);
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf).disableAutomaticRetries().build();
            return httpClient;
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }

    /**
     * @param url      数据库配置
     * @param request  请求数据
     * @param waittime 等待时长
     * @param domain   供应商
     * @return
     */
    public static String sendJsonPostRequestWithDomain(String url, String request, int waittime, String domain) {
        HttpPost post = new HttpPost("http://" + url);
        StringEntity se = new StringEntity(request, Consts.UTF_8);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, Consts.UTF_8.toString()));
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "text/plain; charset=UTF-8"));
        post.setEntity(se);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(waittime).setConnectTimeout(15000)
                .setSocketTimeout(waittime).build();
        post.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        long beginTime = System.currentTimeMillis();
        try {
            //从ota端返回
            response = closeableHttpClient.execute(post);
            if (System.currentTimeMillis() - beginTime > 5000) {
            }
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            if (System.currentTimeMillis() - beginTime > 5000) {
            }
            //logger.error("send json post fail url {} request {} error {}",url,request,e);
            if (System.currentTimeMillis() - beginTime < 3000) {
                CloseableHttpClient client = null;
                try {
                    client = HttpClients.createDefault();
                    response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    String returnValue = EntityUtils.toString(entity);
                    return returnValue;
                } catch (Exception e1) {
                    return null;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return null;
        } finally {
            post.releaseConnection();
        }
    }


    public static String sendJsonPostRequestwithProxy(String url, String request, int waittime, String hosts, int port) {
        if (StringUtils.isBlank(url)) return null;

        HttpPost post = null;
        if (url.length() > 4 && url.substring(0, 4).equals("http")) {
            post = new HttpPost(url);
        } else {
            post = new HttpPost("http://" + url);
        }
        HttpHost proxy = null;
        if (StringUtils.isNotBlank(hosts)) {
            proxy = new HttpHost(hosts, port, "http");
        }
        RequestConfig requestConfig = null;
        if (proxy != null) {
            requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(waittime).setProxy(proxy).setConnectTimeout(5000)
                    .setSocketTimeout(waittime).build();
        } else {
            requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(waittime).setConnectTimeout(5000)
                    .setSocketTimeout(waittime).build();
        }
        post.setConfig(requestConfig);
        StringEntity se = new StringEntity(request, Consts.UTF_8);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, Consts.UTF_8.toString()));
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "text/plain; charset=UTF-8"));
        post.setEntity(se);
        CloseableHttpResponse response = null;
        long beginTime = System.currentTimeMillis();
        try {
            response = closeableHttpClient.execute(post);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("send json post fail url {} request {} error {}", url, request, e);
            if (System.currentTimeMillis() - beginTime < 10000) {
                CloseableHttpClient client = null;
                try {
                    client = HttpClients.createDefault();
                    response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    String returnValue = EntityUtils.toString(entity);
                    return returnValue;
                } catch (Exception e1) {
                    return null;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }

            return null;
        } finally {
            post.releaseConnection();
        }
    }


    public static String sendJsonPostRequest(String url, String request, int waittime) {
        if (StringUtils.isBlank(url)) return null;

        HttpPost post = null;
        if (url.length() > 4 && url.substring(0, 4).equals("http")) {
            post = new HttpPost(url);
        } else {
            post = new HttpPost("http://" + url);
        }

        StringEntity se = new StringEntity(request, Consts.UTF_8);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, Consts.UTF_8.toString()));
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "text/plain; charset=UTF-8"));
        post.setEntity(se);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(waittime).setConnectTimeout(10000)
                .setSocketTimeout(waittime).build();
        post.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        long beginTime = System.currentTimeMillis();
        try {
            response = closeableHttpClient.execute(post);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("send json post fail url {} request {} error {}", url, request, e);
            if (System.currentTimeMillis() - beginTime < 10000) {
                CloseableHttpClient client = null;
                try {
                    client = HttpClients.createDefault();
                    response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    String returnValue = EntityUtils.toString(entity);
                    return returnValue;
                } catch (Exception e1) {
                    return null;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }

            return null;
        } finally {
            post.releaseConnection();
        }
    }

    public static String sendSoapZIPPostRequest(String url, String request, int connecttime, int waittime, String contentType) {
        if (StringUtils.isBlank(url)) return null;

        HttpPost post = null;
        if (url.length() > 4 && url.substring(0, 4).equals("http")) {
            post = new HttpPost(url);
        } else {
            post = new HttpPost("http://" + url);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] result = null;
        try {
            GZIPOutputStream gout = new GZIPOutputStream(out);
            gout.write(request.getBytes(Consts.UTF_8.toString()));
            gout.close();
            result = out.toByteArray();
        } catch (IOException e) {
            logger.error("zip request fail {}", e);
            return null;
        }

        ByteArrayEntity se = new ByteArrayEntity(result);
        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_ENCODING, Consts.UTF_8.toString()));
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, contentType));
        post.setEntity(se);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(waittime).setConnectTimeout(connecttime)
                .setSocketTimeout(waittime).build();
        post.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        long beginTime = System.currentTimeMillis();
        try {
            response = closeableHttpClient.execute(post);
            HttpEntity entity = response.getEntity();
            ByteArrayOutputStream outStream =
                    new ByteArrayOutputStream(2220);
            GZIPInputStream inStream =
                    new GZIPInputStream(entity.getContent());
            byte[] buf = new byte[10000];
            while (true) {
                int size = inStream.read(buf);
                if (size <= 0)
                    break;
                outStream.write(buf, 0, size);
            }
            outStream.close();
            return new String(outStream.toByteArray(), Consts.UTF_8.toString());
        } catch (Exception e) {
            logger.error("send json post fail url {} request {} error {}", url, request, e);
            if (System.currentTimeMillis() - beginTime < 10000) {
                CloseableHttpClient client = null;
                try {
                    client = HttpClients.createDefault();
                    response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    String returnValue = EntityUtils.toString(entity);
                    return returnValue;
                } catch (Exception e1) {
                    return null;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return null;
        } finally {
            post.releaseConnection();
        }
    }

    public static String sendJsonPostRequest(String url, String request, int waittime, String contentType) {
        if (StringUtils.isBlank(url)) return null;

        HttpPost post = null;
        if (url.length() > 4 && url.substring(0, 4).equals("http")) {
            post = new HttpPost(url);
        } else {
            post = new HttpPost("http://" + url);
        }
        StringEntity se = new StringEntity(request, Consts.UTF_8);
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, contentType));

        post.setEntity(se);
        //HttpHost proxy = new HttpHost("101.201.116.23", 23128, "http");
        RequestConfig requestConfig = RequestConfig.custom()//.setProxy(proxy)
                .setConnectionRequestTimeout(waittime).setConnectTimeout(10000)
                .setSocketTimeout(waittime).build();
        post.setConfig(requestConfig);
        post.removeHeaders("Cookie2");
        CloseableHttpResponse response = null;
        long beginTime = System.currentTimeMillis();
        try {
            response = closeableHttpClient.execute(post);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("send json post fail url {} request {} error {}", url, request, e);
            if (System.currentTimeMillis() - beginTime < 10000) {
                CloseableHttpClient client = null;
                try {
                    client = HttpClients.createDefault();
                    response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    String returnValue = EntityUtils.toString(entity);
                    return returnValue;
                } catch (Exception e1) {
                    return null;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return null;
        } finally {
            post.releaseConnection();
        }
    }


    public static String sendJsonPostRequest(HttpPost post, String request, int waittime, String contentType) {
        StringEntity se = new StringEntity(request, Consts.UTF_8);
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, contentType));

        post.setEntity(se);
        //HttpHost proxy = new HttpHost("101.201.116.23", 23128, "http");
        RequestConfig requestConfig = RequestConfig.custom()//.setProxy(proxy)
                .setConnectionRequestTimeout(waittime).setConnectTimeout(10000)
                .setSocketTimeout(waittime).build();
        post.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        long beginTime = System.currentTimeMillis();
        try {
            response = closeableHttpClient.execute(post);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("send json post fail request {} error {}", request, e);
            if (System.currentTimeMillis() - beginTime < 10000) {
                CloseableHttpClient client = null;
                try {
                    client = HttpClients.createDefault();
                    response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    String returnValue = EntityUtils.toString(entity);
                    return returnValue;
                } catch (Exception e1) {
                    return null;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return null;
        } finally {
            post.releaseConnection();
        }
    }

    public static String sendWsldPostRequestWithTLSv1_2(String url, String request, int connecttimeout, int waittime, String soapAction, String host, String contenttype, String proxIp, int proxport) {
        if (StringUtils.isBlank(url)) return null;
        HttpPost post = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = generatorOneHttpClientWithTLSv1_2();
        try {
            post = new HttpPost(url);
            HttpHost proxy = null;
            if (StringUtils.isNotBlank(proxIp)) {
                proxy = new HttpHost(proxIp, proxport, "http");
            }
            RequestConfig requestConfig = null;
            if (proxy != null) {
                requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(waittime).setProxy(proxy).setConnectTimeout(connecttimeout)
                        .setSocketTimeout(waittime).build();
            } else {
                requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(waittime).setConnectTimeout(connecttimeout)
                        .setSocketTimeout(waittime).build();
            }
            post.setConfig(requestConfig);
            byte[] bytes = request.getBytes("UTF-8");
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes, 0, bytes.length);
            post.setEntity(byteArrayEntity);
            if (StringUtils.isNotBlank(contenttype)) {
                post.setHeader(CONTENT_TYPE_KEY, contenttype);
            } else {
                post.setHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
            }
            post.setHeader(ACCEPT_LANGUAGE_KEY, ACCEPT_LANGUAGE_VALUE);
            post.setHeader(ACCEPT_KEY, ACCEPT_VALUE);
            post.setHeader(USER_AGENT_KEY, USER_AGENT_VALUE);
            post.setHeader(ACCEPT_ENCODING_KEY, ACCEPT_ENCODING_VALUE);
            post.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
            if (StringUtils.isNotBlank(soapAction)) {
                post.setHeader(SOAPACTION, soapAction);
            }
            if (StringUtils.isNotBlank(host)) {
                post.setHeader(HOST, host);
            }
            response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("send json post fail url {} request {} error {}", url, request, e);
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (post != null) {
                post.abort();
                post.releaseConnection();
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }


    public static String sendWsldPostRequest(String url, String request, int connecttimeout, int waittime, String soapAction, String host, String contenttype, String proxIp, int proxport) {
        if (StringUtils.isBlank(url)) return null;
        HttpPost post = null;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = generatorOneHttpClient();
        try {
            post = new HttpPost(url);
            HttpHost proxy = null;
            if (StringUtils.isNotBlank(proxIp)) {
                proxy = new HttpHost(proxIp, proxport, "http");
            }
            RequestConfig requestConfig = null;
            if (proxy != null) {
                requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(waittime).setProxy(proxy).setConnectTimeout(connecttimeout)
                        .setSocketTimeout(waittime).build();
            } else {
                requestConfig = RequestConfig.custom()
                        .setConnectionRequestTimeout(waittime).setConnectTimeout(connecttimeout)
                        .setSocketTimeout(waittime).build();
            }
            post.setConfig(requestConfig);
            byte[] bytes = request.getBytes("UTF-8");
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes, 0, bytes.length);
            post.setEntity(byteArrayEntity);
            if (StringUtils.isNotBlank(contenttype)) {
                post.setHeader(CONTENT_TYPE_KEY, contenttype);
            } else {
                post.setHeader(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
            }
            post.setHeader(ACCEPT_LANGUAGE_KEY, ACCEPT_LANGUAGE_VALUE);
            post.setHeader(ACCEPT_KEY, ACCEPT_VALUE);
            post.setHeader(USER_AGENT_KEY, USER_AGENT_VALUE);
            post.setHeader(ACCEPT_ENCODING_KEY, ACCEPT_ENCODING_VALUE);
            post.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
            if (StringUtils.isNotBlank(soapAction)) {
                post.setHeader(SOAPACTION, soapAction);
            }
            if (StringUtils.isNotBlank(host)) {
                post.setHeader(HOST, host);
            }
            response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("send json post fail url {} request {} error {}", url, request, e);
            return null;
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (post != null) {
                post.abort();
                post.releaseConnection();
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }


    public static String sendPostRequest(String url, Map<String, String> requestMap, int waittime) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (requestMap != null)
            for (Map.Entry<String, String> onepram : requestMap.entrySet()) {
                params.add(new BasicNameValuePair(onepram.getKey(), onepram.getValue()));
            }
        HttpPost post = new HttpPost(url);
        if (params.size() > 0)
            post.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(waittime).setConnectTimeout(10000)
                .setSocketTimeout(waittime).build();
        post.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        long beginTime = System.currentTimeMillis();
        try {
            response = closeableHttpClient.execute(post);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("send json post fail url {} request {} error {}", url, JSONObject.toJSONString(requestMap), e);
            if (System.currentTimeMillis() - beginTime < 10000) {
                CloseableHttpClient client = null;
                try {
                    client = HttpClients.createDefault();
                    response = client.execute(post);
                    HttpEntity entity = response.getEntity();
                    String returnValue = EntityUtils.toString(entity);
                    return returnValue;
                } catch (Exception e1) {
                    return null;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return null;
        } finally {
            if (post != null)
                post.releaseConnection();
        }
    }

    public static String sendGetRequest(String url, int connectTimeOut, int waittime) {

        HttpGet get = new HttpGet(url);
        //HttpHost proxy = new HttpHost("47.90.33.42", 23128, "http");
        RequestConfig requestConfig = RequestConfig.custom()//.setProxy(proxy)
                .setConnectionRequestTimeout(waittime).setConnectTimeout(connectTimeOut)
                .setSocketTimeout(waittime).build();
        get.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        long beginTime = System.currentTimeMillis();
        try {
            response = closeableHttpClient.execute(get);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("send json post fail url {} error {}", url, e);
            if (System.currentTimeMillis() - beginTime < 10000) {
                CloseableHttpClient client = null;
                try {
                    client = HttpClients.createDefault();
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    String returnValue = EntityUtils.toString(entity);
                    return returnValue;
                } catch (Exception e1) {
                    return null;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return null;
        } finally {
            get.releaseConnection();
        }
    }


    public static String sendGetRequest(String url, int connectTimeOut, int waittime, String contentType) {

        HttpGet get = new HttpGet(url);
        //HttpHost proxy = new HttpHost("47.90.33.42", 23128, "http");
        RequestConfig requestConfig = RequestConfig.custom()//.setProxy(proxy)
                .setConnectionRequestTimeout(waittime).setConnectTimeout(connectTimeOut)
                .setSocketTimeout(waittime).build();
        get.setConfig(requestConfig);
        get.setHeader(CONTENT_TYPE_KEY, contentType);
        CloseableHttpResponse response = null;
        long beginTime = System.currentTimeMillis();
        try {
            response = closeableHttpClient.execute(get);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("send json post fail url {} error {}", url, e);
            if (System.currentTimeMillis() - beginTime < 10000) {
                CloseableHttpClient client = null;
                try {
                    client = HttpClients.createDefault();
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    String returnValue = EntityUtils.toString(entity);
                    return returnValue;
                } catch (Exception e1) {
                    return null;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return null;
        } finally {
            get.releaseConnection();
        }
    }


    public static String sendGetRequest(String url, int waittime) {
        return sendGetRequest(url, 10000, waittime);
    }


    public static Map<String, String> sendPostRquest(HttpPost post, String url, String request, int waitTime) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(waitTime).setConnectTimeout(10000)
                .setSocketTimeout(waitTime).build();
        post.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        Map<String, String> httpresultMap = new HashMap<>();
        StringEntity se = new StringEntity(request, Consts.UTF_8);
        post.setEntity(se);
        long beginTime = System.currentTimeMillis();
        try {
            response = closeableHttpClient.execute(post);
            String entity = EntityUtils.toString(response.getEntity());
            httpresultMap.put(ENTITY, entity);
            httpresultMap.put(SESSION, response.getHeaders(SESSION)[0].getValue());
            return httpresultMap;
        } catch (Exception e) {
            logger.error("send json post fail url {} request {} error {}", url, e);
            if (System.currentTimeMillis() - beginTime < 10000) {
                CloseableHttpClient client = null;
                try {
                    client = HttpClients.createDefault();
                    response = client.execute(post);
                    String entity = EntityUtils.toString(response.getEntity());
                    httpresultMap.put(ENTITY, entity);
                    httpresultMap.put(SESSION, response.getHeaders(SESSION)[0].getValue());
                    return httpresultMap;
                } catch (Exception e1) {
                    return null;
                } finally {
                    if (response != null) {
                        try {
                            response.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (client != null) {
                        try {
                            client.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return null;
        } finally {
            if (post != null)
                post.releaseConnection();
        }

    }

}

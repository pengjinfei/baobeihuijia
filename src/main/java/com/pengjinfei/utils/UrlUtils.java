package com.pengjinfei.utils;

import com.pengjinfei.bean.Child;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pengjinfei
 * DATE: 3/15/16
 * Description:
 */
public class UrlUtils {

    private final static String BASE_URL = "http://www.baobeihuijia.com";

    private final static String SEARCH_URL = "/result.aspx";

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static PoolingHttpClientConnectionManager httpClientConnectionManager = null;

    private static HttpRequestRetryHandler httpRequestRetryHandler=null;

    private static   DefaultProxyRoutePlanner routePlanner = null;

    static {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
                .getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
                .<ConnectionSocketFactory> create().register("http", plainsf).build();
        httpClientConnectionManager=new PoolingHttpClientConnectionManager(registry);
        httpClientConnectionManager.setMaxTotal(20);
        httpClientConnectionManager.setDefaultMaxPerRoute(4);
        HttpHost host = new HttpHost(BASE_URL);
        httpClientConnectionManager.setMaxPerRoute(new HttpRoute(host),10);
        // 请求重试处理
        httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception,
                                        int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext
                        .adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                return !(request instanceof HttpEntityEnclosingRequest);
            }
        };

        routePlanner=new DefaultProxyRoutePlanner(new HttpHost("10.17.171.11",8080));
    }

    public static CloseableHttpClient getHttpClient(){
        return  HttpClients.custom()
                .setConnectionManager(httpClientConnectionManager)
                .setRetryHandler(httpRequestRetryHandler)
                .setRoutePlanner(routePlanner)
                .build();
    }

    //设置请求消息头
    private static void config(HttpRequestBase httpRequestBase) {
        // 设置Header等
        httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");
        int timeOut=3000;
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeOut)
                .setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httpRequestBase.setConfig(requestConfig);
    }

    public static Child parseUrl2Child(String href) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet=new HttpGet(BASE_URL +"/"+href);
        CloseableHttpResponse response = httpClient.execute(httpGet, HttpClientContext.create());
        HttpEntity entity = response.getEntity();
        Document document = Jsoup.parse(entity.getContent(),"UTF-8",BASE_URL);
        EntityUtils.consume(entity);

        Child child = new Child();
        child.setId(href.replace("view.aspx?id=", ""));
        Element tableDiv = document.getElementById("table_1_normaldivr");
        Elements children = tableDiv.child(1).children();
        //name
        Element element = children.get(2);
        child.setName(getInfo(element));

        element = element.nextElementSibling();
        child.setSex(getInfo(element));

        element = element.nextElementSibling();
        child.setBirthday(getInfo(element));

        element = element.nextElementSibling();
        child.setHeight(getInfo(element));

        element = element.nextElementSibling();
        child.setDipearDay(getInfo(element));

        element = element.nextElementSibling();
        child.setLocation(getInfo(element));

        element = element.nextElementSibling();
        child.setDispearLocation(getInfo(element));

        element = element.nextElementSibling();
        child.setDiscreption(getInfo(element));

        Element img = document.select("img[src~=/photo/.*]").first();
        String imgSrc = img.attr("src");
        String imgName = imgSrc.substring(imgSrc.lastIndexOf("/") + 1);
        if (!imgName.startsWith("none")) {
            child.setUrl(BASE_URL+imgSrc);
        }
        return child;
    }

    private static String getInfo(Element element) {
        List<Node> nodes = element.childNodes();
        if (nodes.size() == 2) {
            return nodes.get(1).toString();
        }
        return null;
    }

    public static Document initSearch() throws IOException {
        return getDocument(setInitParams());
    }

    public static String pageParam(int page) {
        return "&GridView1$ctl33$tbPage="+String.valueOf(page);
    }

    public static Document getDocument(List<NameValuePair> nameValuePairs) throws IOException {
        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost=new HttpPost(BASE_URL + SEARCH_URL);
        setHeaders(httpPost);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost,
                HttpClientContext.create());
        HttpEntity entity = httpResponse.getEntity();
        Document document = Jsoup.parse(entity.getContent(), "UTF-8", BASE_URL);
        EntityUtils.consume(entity);
        return document;
    }

    private static void setHeaders(HttpRequestBase httpRequestBase){
        httpRequestBase.setHeader("Origin", "http://www.baobeihuijia.com");
        httpRequestBase.setHeader("Proxy-Connection", "keep-alive");
        httpRequestBase.setHeader("Referer", "http://www.baobeihuijia.com/result.aspx");
    }

    private static List<NameValuePair> setInitParams(){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        nvps.add(new BasicNameValuePair("type","3"));
        nvps.add(new BasicNameValuePair("findtype","1"));
        nvps.add(new BasicNameValuePair("sex",""));
        nvps.add(new BasicNameValuePair("datetype","2"));
        nvps.add(new BasicNameValuePair("beginyear","2000"));
        nvps.add(new BasicNameValuePair("endyear=",""));
        nvps.add(new BasicNameValuePair("beginmonth","1"));
        nvps.add(new BasicNameValuePair("endmonth","12"));
        nvps.add(new BasicNameValuePair("prov",""));
        nvps.add(new BasicNameValuePair("keyword",""));
        nvps.add(new BasicNameValuePair("by",""));
        nvps.add(new BasicNameValuePair("submit","+%E6%90%9C+%E7%B4%A2+"));

        return nvps;
    }

    public static List<NameValuePair> prepareParams(Document document){
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        String viewstategenerator = document.getElementById("__VIEWSTATEGENERATOR").val();
        nvps.add(new BasicNameValuePair("__VIEWSTATEGENERATOR",viewstategenerator));
        String viewstate = document.getElementById("__VIEWSTATE").val();
        nvps.add(new BasicNameValuePair("__VIEWSTATE", viewstate));
        String eventvalidation = document.getElementById("__EVENTVALIDATION").val();
        nvps.add(new BasicNameValuePair("__EVENTVALIDATION",eventvalidation));
        nvps.add(new BasicNameValuePair("__EVENTTARGET", "GridView1$ctl33$lbGO"));
        return nvps;
    }

    public static BasicNameValuePair genPageParam(int page){
       return new BasicNameValuePair("GridView1$ctl33$tbPage", String.valueOf(page));
    }

}

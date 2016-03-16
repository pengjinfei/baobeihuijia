package com.pengjinfei.utils;

import com.pengjinfei.bean.Child;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-16
 * Description:
 */
public class UrlUtilsTest {

    @Test
    public void testParseUrl2Chile() throws IOException {
        String hre="view.aspx?id=24346";
        Child child = UrlUtils.parseUrl2Child(hre);
        System.out.println(child);
    }

    @Test
    public void testInitSearch() throws IOException {
        Document document = UrlUtils.initSearch();
        System.out.println(document);
    }

    @Test
    public void testJsoup() throws IOException {
        String s = "此次查询共找到【364】条记录！";
        Pattern pattern=Pattern.compile("^.*【(\\d*)】.*$");
        Matcher matcher = pattern.matcher(s);
        if (matcher.matches()){
            System.out.println(matcher.group(1));
        }
    }

    @Test
    public void testHttpClient() throws IOException {
        CloseableHttpClient httpClient = UrlUtils.getHttpClient();
        HttpPost httpPost=new HttpPost("http://www.baobeihuijia.com/result.aspx");
        httpPost.setHeader("Origin", "http://www.baobeihuijia.com");
        httpPost.setHeader("Proxy-Connection", "keep-alive");
        httpPost.setHeader("Referer", "http://www.baobeihuijia.com/result.aspx");

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

        httpPost.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost,
                HttpClientContext.create());
        HttpEntity entity = httpResponse.getEntity();
        Document document = Jsoup.parse(entity.getContent(), "UTF-8", "www.baidu.com");
        EntityUtils.consume(entity);


        httpPost=new HttpPost("http://www.baobeihuijia.com/result.aspx");
        httpPost.setHeader("Origin", "http://www.baobeihuijia.com");
        httpPost.setHeader("Proxy-Connection", "keep-alive");
        httpPost.setHeader("Referer", "http://www.baobeihuijia.com/result.aspx");
        nvps = new ArrayList<NameValuePair>();

        String viewstategenerator = document.getElementById("__VIEWSTATEGENERATOR").val();
        nvps.add(new BasicNameValuePair("__VIEWSTATEGENERATOR",viewstategenerator));
        String viewstate = document.getElementById("__VIEWSTATE").val();
        nvps.add(new BasicNameValuePair("__VIEWSTATE", viewstate));
        String eventvalidation = document.getElementById("__EVENTVALIDATION").val();
        nvps.add(new BasicNameValuePair("__EVENTVALIDATION",eventvalidation));
        nvps.add(new BasicNameValuePair("__EVENTTARGET", "GridView1$ctl33$lbGO"));
        nvps.add(new BasicNameValuePair("GridView1$ctl33$tbPage", String.valueOf(2)));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        httpClient = UrlUtils.getHttpClient();
        httpResponse = httpClient.execute(httpPost,
                HttpClientContext.create());
        entity = httpResponse.getEntity();
        document = Jsoup.parse(entity.getContent(), "UTF-8", "www.baidu.com");
        EntityUtils.consume(entity);
        System.out.println(document);
    }

}
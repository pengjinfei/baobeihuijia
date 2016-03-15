package com.pengjinfei;

import junit.framework.TestCase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    @Test
    public void testGetFirst() throws IOException {
        String params="type=3&findtype=&sex=&datetype=1&beginyear=&beginmonth=1&endyear=&endmonth=12&prov=&keyword=&by=&submit=+%E6%90%9C+%E7%B4%A2+";
        Document document = getDocument(params);
        //获得所有页数
        int totalPage = Integer.parseInt(document.getElementById("GridView1_ctl33_Label1").html());
        String requst = parseRequst(document);
        document = getDocument(requst);
        System.out.println(document);

    }

    public String parseRequst(Document document) throws UnsupportedEncodingException {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("__VIEWSTATEGENERATOR=").append(document.getElementById("__VIEWSTATEGENERATOR").val());
        stringBuilder.append("&__VIEWSTATE=").append(document.getElementById("__VIEWSTATE").val());
        stringBuilder.append("&__EVENTVALIDATION=").append(document.getElementById("__EVENTVALIDATION").val());
        stringBuilder.append("&GridView1$ctl33$tbPage=").append(2);
        stringBuilder.append("&__EVENTTARGET=GridView1%24ctl33%24lbNext");
        return URLEncoder.encode(stringBuilder.toString(),"UTF-8");
    }

    public Document getDocument(String params) throws IOException {
        URL url=new URL("http://www.baobeihuijia.com/result.aspx");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("10.17.171.11",8080)));
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        urlConnection.setRequestMethod("POST");// 设置URL请求方法
        urlConnection.setRequestProperty("accept", "*/*");
        urlConnection.setRequestProperty("Origin","http://www.baobeihuijia.com");
        urlConnection.setRequestProperty("Proxy-Connection","keep-alive");
        urlConnection.setRequestProperty("Referer","http://www.baobeihuijia.com/result.aspx");
        urlConnection.setRequestProperty("Content-Length", String
                .valueOf(params.length()));
        urlConnection.getOutputStream().write(params.getBytes());
        urlConnection.connect();
        Document document = Jsoup.parse(urlConnection.getInputStream(), "UTF-8", "http://www.baobeihuijia.com");
        urlConnection.disconnect();
        return document;
    }
}

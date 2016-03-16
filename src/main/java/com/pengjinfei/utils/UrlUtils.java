package com.pengjinfei.utils;

import com.pengjinfei.bean.Child;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * Created by pengjinfei
 * DATE: 3/15/16
 * Description:
 */
public class UrlUtils {

    private final static String BASE_URL = "http://www.baobeihuijia.com/";

    private final static String SEARCH_URL = "result.aspx";

    private final static String IMG_FOLDER = System.getProperty("user.home") + File.separator + "img";

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static void openProxy() {
        System.getProperties().setProperty("proxySet", "true");
        System.getProperties().setProperty("http.proxyHost", "10.17.171.11");
        System.getProperties().setProperty("http.proxyPort", "8080");
    }

    public static void cancelProxy() {
        System.getProperties().setProperty("proxySet", "false");
    }

    public static void saveImg(String href) {
        try {
            if (href.startsWith("/")) {
                href=href.substring(1);
            }
            URL url = new URL(BASE_URL + href);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            String imgName = href.substring(href.lastIndexOf("/") + 1);

            File imgFolder = new File(IMG_FOLDER);
            if (!imgFolder.exists()) {
                imgFolder.mkdirs();
            }
            FileOutputStream writer = new FileOutputStream(IMG_FOLDER + File.separator + imgName);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) != -1) {
                writer.write(buff, 0, len);
            }
            inputStream.close();
            writer.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Child parseUrl2Child(String href) throws IOException {
        Document document = Jsoup.connect(BASE_URL + href).get();
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
            child.setUrl(imgName);
            saveImg(imgSrc);
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
        return getDocument(initRequest());
    }

    private static String initRequest() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("type=").append("3");
        stringBuilder.append("&findtype=").append("1");
        stringBuilder.append("&sex=").append("");
        stringBuilder.append("&datetype=").append("2");
        stringBuilder.append("&beginyear=").append("2000");
        stringBuilder.append("&endyear=").append("");
        stringBuilder.append("&beginmonth=").append("1");
        stringBuilder.append("&endmonth=").append("12");
        stringBuilder.append("&prov=").append("");
        stringBuilder.append("&keyword=").append("");
        stringBuilder.append("&by=").append("");
        stringBuilder.append("&submit=").append("+%E6%90%9C+%E7%B4%A2+");
        return stringBuilder.toString();
    }

    public static String parseInitParams(Document document) throws UnsupportedEncodingException {
        StringBuilder stringBuilder = new StringBuilder();
        String viewstategenerator = document.getElementById("__VIEWSTATEGENERATOR").val();
        stringBuilder.append("__VIEWSTATEGENERATOR=").append(URLEncoder.encode(viewstategenerator, "UTF-8"));
        String viewstate = document.getElementById("__VIEWSTATE").val();
        stringBuilder.append("&__VIEWSTATE=").append(URLEncoder.encode(viewstate, "UTF-8"));
        String eventvalidation = document.getElementById("__EVENTVALIDATION").val();
        stringBuilder.append("&__EVENTVALIDATION=").append(URLEncoder.encode(eventvalidation, "UTF-8"));
//        stringBuilder.append("&GridView1$ctl33$tbPage=").append(page);
        //GridView1%24ctl33%24lbNext
        //GridView1%24ctl33%24lbGO
        stringBuilder.append("&__EVENTTARGET=").append(URLEncoder.encode("GridView1$ctl33$lbGO", "UTF-8"));
        return stringBuilder.toString();
    }

    public static String pageParam(int page) {
        return "&GridView1$ctl33$tbPage="+String.valueOf(page);
    }

    public static Document getDocument(String params) throws IOException {
        URL url = new URL(BASE_URL + SEARCH_URL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("accept", "*/*");
        urlConnection.setRequestProperty("Origin", "http://www.baobeihuijia.com");
        urlConnection.setRequestProperty("Proxy-Connection", "keep-alive");
        urlConnection.setRequestProperty("Referer", "http://www.baobeihuijia.com/result.aspx");
        urlConnection.setRequestProperty("Content-Length", String
                .valueOf(params.length()));
        urlConnection.getOutputStream().write(params.getBytes());
        urlConnection.connect();
        Document document = Jsoup.parse(urlConnection.getInputStream(), "UTF-8", "http://www.baobeihuijia.com");
        urlConnection.disconnect();
        return document;
    }
}

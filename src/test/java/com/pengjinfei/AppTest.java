package com.pengjinfei;

import com.pengjinfei.bean.Child;
import junit.framework.TestCase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    @Test
    public void testGetFirst() throws IOException {
        String params="type=3&findtype=1&sex=&datetype=1&beginyear=&beginmonth=1&endyear=&endmonth=12&prov=&keyword=&by=&submit=+%E6%90%9C+%E7%B4%A2+";
        Document document = getDocument(params);
        //获得所有页数
        int totalPage = Integer.parseInt(document.getElementById("GridView1_ctl33_Label1").html());
        String requst = parseRequst(document,1);
        document=getDocument(requst);
        Elements links = document.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
            if(href.startsWith("view.aspx")){
                Document viewDoc=getView(href);
                Child child=new Child();
                child.setId(href.replace("view.aspx?id=",""));
                Element tableDiv = viewDoc.getElementById("table_1_normaldivr");
                Elements children = tableDiv.child(1).children();
                //name
                child.setName(getInfo(children.get(2)));
                child.setSex(getInfo(children.get(3)));
                child.setBirthday(getInfo(children.get(4)));
                child.setHeight(getInfo(children.get(5)));
                child.setDipearDay(getInfo(children.get(6)));
                child.setLocation(getInfo(children.get(7)));
                child.setDispearLocation(getInfo(children.get(8)));
                child.setDiscreption(getInfo(children.get(9)));
                System.out.println(child);

                Element img = viewDoc.select("img[src~=/photo/.*]").first();
                String imgSrc = img.attr("src");
                String imgName=imgSrc.substring(imgSrc.lastIndexOf("/")+1);
                InputStream stream = getImg(imgSrc);
                FileOutputStream writer=new FileOutputStream(System.getProperty("user.dir")+ File.separator+imgName);
                byte[] buff=new byte[1024];
                int len=0;
                while ((len=stream.read(buff))!=-1){
                    writer.write(buff,0,len);
                }
                stream.close();
                writer.close();
                System.out.println(img);
                System.exit(0);
            }
        }
    }

    private String getInfo(Element element){
        List<Node> nodes = element.childNodes();
        if(nodes.size()==2){
            return nodes.get(1).toString();
        }
        return null;
    }

    @Test
    public void testView() throws IOException {
        String href="view.aspx?id=2234";
        Document view = getView(href);
        System.out.println(view);
    }

    public String parseRequst(Document document,int page) throws UnsupportedEncodingException {
        StringBuilder stringBuilder=new StringBuilder();
        String viewstategenerator = document.getElementById("__VIEWSTATEGENERATOR").val();
        stringBuilder.append("__VIEWSTATEGENERATOR=").append(URLEncoder.encode(viewstategenerator,"UTF-8"));
        String viewstate = document.getElementById("__VIEWSTATE").val();
        stringBuilder.append("&__VIEWSTATE=").append(URLEncoder.encode(viewstate,"UTF-8"));
        String eventvalidation = document.getElementById("__EVENTVALIDATION").val();
        stringBuilder.append("&__EVENTVALIDATION=").append(URLEncoder.encode(eventvalidation,"UTF-8"));
        stringBuilder.append("&GridView1$ctl33$tbPage=").append(page);
        stringBuilder.append("&__EVENTTARGET=").append(URLEncoder.encode("GridView1%24ctl33%24lbNext","UTF-8"));
        return stringBuilder.toString();
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

    public Document getView(String href) throws IOException {
        URL url=new URL("http://www.baobeihuijia.com/"+href);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("10.17.171.11",8080)));
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.connect();
        Document document = Jsoup.parse(urlConnection.getInputStream(), "UTF-8", "http://www.baobeihuijia.com");
        urlConnection.disconnect();
        return document;
    }

    public InputStream getImg(String href) throws IOException {
        URL url=new URL("http://www.baobeihuijia.com/"+href);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("10.17.171.11",8080)));
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        return  inputStream;
    }
}

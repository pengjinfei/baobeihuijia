package com.pengjinfei.utils;

import java.io.*;
import java.net.*;

/**
 * Created by pengjinfei
 * DATE: 3/15/16
 * Description:
 */
public class UrlConnctionUtils {

    private final String BASE_URL="http://www.baobeihuijia.com/";

    private final String IMG_FOLDER=System.getProperty("user.home")+File.separator+"img";

    public static void openProxy() {
        System.getProperties().setProperty("proxySet", "true");
        System.getProperties().setProperty("http.proxyHost", "10.17.171.11");
        System.getProperties().setProperty("http.proxyPort", "8080");
    }

    public static void cancelProxy(){
        System.getProperties().setProperty("proxySet", "false");
    }

    public void saveImg(String href){
        try {
            URL url=new URL(BASE_URL+href);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            String imgName=href.substring(href.lastIndexOf("/")+1);
            FileOutputStream writer=new FileOutputStream(IMG_FOLDER+ File.separator+imgName);
            byte[] buff=new byte[1024];
            int len=0;
            while ((len=inputStream.read(buff))!=-1){
                writer.write(buff,0,len);
            }
            inputStream.close();
            writer.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

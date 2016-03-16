package com.pengjinfei.utils;

import com.pengjinfei.bean.Child;
import org.jsoup.nodes.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-16
 * Description:
 */
public class UrlUtilsTest {

    @Before
    public void openProxy(){
        UrlUtils.openProxy();
    }

    @After
    public void closeProxy(){
        UrlUtils.cancelProxy();
    }

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
    public void testController(){

    }

}
package com.pengjinfei;

import com.pengjinfei.bean.Child;
import com.pengjinfei.thread.ExcelWriter;
import com.pengjinfei.thread.Spider;
import com.pengjinfei.thread.UrlChildParser;
import com.pengjinfei.utils.UrlUtils;
import org.apache.http.NameValuePair;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-16
 * Description:
 */
public class Controller {

    private int urlParser = 3;
    private int childParser = 3;
    private CyclicBarrier urlBarrier;
    private CyclicBarrier childBarrier;
    private final BlockingQueue<String> urlPool = new LinkedBlockingDeque<String>();
    private final BlockingQueue<Child> children = new LinkedBlockingDeque<Child>();
    private List<NameValuePair> nameValuePairs;
    private int totalPage;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public Controller(int urlParser, int childParser) {
        this.urlParser = urlParser;
        urlBarrier = new CyclicBarrier(urlParser, new Runnable() {
            public void run() {
                try {
                    urlPool.put(UrlChildParser.END_FLAG_URL);
                    logger.info("爬取数据的线程已经全部完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        this.childParser = childParser;
        childBarrier = new CyclicBarrier(childParser, new Runnable() {
            public void run() {
                try {
                    children.put(UrlChildParser.END_FLAG_CHILD);
                    logger.info("解析数据到对象的线程已经全部完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void init() {
        try {
            Document document = UrlUtils.initSearch();
            nameValuePairs = UrlUtils.prepareParams(document);
            totalPage = Integer.parseInt(document.getElementById("GridView1_ctl33_Label1").html());
            logger.info("共有" + totalPage + "页的数据需要爬取");

            String h = document.getElementById("lbCount").html();
            Pattern pattern = Pattern.compile("^.*【(\\d*)】.*$");
            Matcher matcher = pattern.matcher(h);
            if (matcher.matches()) {
                logger.info("共有" + matcher.group(1) + "条的数据需要爬取");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startParseUrl() {
        int everyPage = totalPage / urlParser;

        if (everyPage * urlParser != totalPage) {
            everyPage++;
        }
        logger.info("每个线程抓取" + everyPage + "页的数据");
        for (int i = 0; i < urlParser; i++) {
            int start = i * everyPage + 1;
            int end = start + everyPage;
            if (end > totalPage + 1) {
                end = totalPage + 1;
            }
            Spider spider = new Spider(start, end, urlPool, nameValuePairs, urlBarrier);
            Thread thread = new Thread(spider);
            thread.start();
        }
    }

    private void startParseChild() {

        for (int i = 0; i < childParser; i++) {
            UrlChildParser parser = new UrlChildParser(urlPool, children, childBarrier);
            Thread thread = new Thread(parser);
            thread.start();
        }
    }

    private void startWriteExcel() {
        ExcelWriter writer = new ExcelWriter(children);
        Thread thread = new Thread(writer);
        thread.start();
    }

    public void start() {
        init();

        startParseUrl();
        startParseChild();
        startWriteExcel();
    }

    public static void main(String[] args) {
        Controller controller = new Controller(5, 5);
        controller.start();
    }

}

package com.pengjinfei.thread;

import com.pengjinfei.utils.UrlUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-16
 * Description:
 */
public class Spider implements Runnable {

    private final BlockingQueue<String> urlPool;
    private int startPage;
    private int endPage;
    private String initParam;
    private CyclicBarrier barrier;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public Spider(int startPage, int endPage, BlockingQueue<String> urlPool,String  initParam,CyclicBarrier barrier) {
        this.urlPool=urlPool;
        this.startPage=startPage;
        this.endPage=endPage;
        this.initParam=initParam;
        this.barrier=barrier;
    }
    public void run() {
        logger.info("开始爬取"+startPage+"页到"+endPage+"页的数据");
        for (int i = startPage; i < endPage; i++) {
            String params=initParam+ UrlUtils.pageParam(i);
            try {
                Document document = UrlUtils.getDocument(params);
                Elements links = document.select("a[href]");
                for (Element link : links) {
                    String href = link.attr("href");
                    if (href.startsWith("view.aspx")) {
                        urlPool.put(href);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            logger.info(startPage+"页到"+endPage+"页的数据爬取完成，线程开始等待");
            barrier.await();
            logger.info(startPage+"页到"+endPage+"页的的线程完成等待");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

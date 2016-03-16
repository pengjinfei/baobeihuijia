package com.pengjinfei.thread;

import com.pengjinfei.bean.Child;
import com.pengjinfei.utils.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by pengjinfei
 * DATE: 3/15/16
 * Description:该线程类从urlPool中取出详细地址：view.aspx?id=*，解析并封装成Child对象放入children队列
 */
public class UrlChildParser implements Runnable {

    private final BlockingQueue<String> urlPool;
    private final BlockingQueue<Child> children;
    private CyclicBarrier barrier;
    public final static String END_FLAG_URL = UUID.randomUUID().toString();
    private Logger logger = LoggerFactory.getLogger(getClass());

    public final static Child END_FLAG_CHILD=new Child();

    public UrlChildParser(BlockingQueue<String> urlPool, BlockingQueue<Child> children,CyclicBarrier barrier) {
        this.urlPool = urlPool;
        this.children = children;
        this.barrier=barrier;
    }

    public void run() {
        while (true) {
            String url;
            try {
                url = urlPool.take();
                if (url == END_FLAG_URL) {
                    urlPool.put(END_FLAG_URL);
                    break;
                }
                Child child = UrlUtils.parseUrl2Child(url);
                children.put(child);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            logger.info("线程"+Thread.currentThread().getName()+"解析完成，开始等待");
            barrier.await();
            logger.info("线程"+Thread.currentThread().getName()+"完成等待");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

package com.pengjinfei.thread;

import com.pengjinfei.bean.Child;

import java.util.concurrent.BlockingQueue;

/**
 * Created by pengjinfei
 * DATE: 3/15/16
 * Description:
 */
public class UrlChildParser implements Runnable {

    private final BlockingQueue<String > urlPool;
    private final BlockingQueue<Child> children;

    public UrlChildParser(BlockingQueue<String> urlPool,BlockingQueue<Child> children) {
        this.urlPool=urlPool;
        this.children=children;
    }

    public void run() {

    }
}

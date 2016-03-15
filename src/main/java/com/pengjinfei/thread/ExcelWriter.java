package com.pengjinfei.thread;

import com.pengjinfei.bean.Child;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * Created by pengjinfei
 * DATE: 3/15/16
 * Description:
 */
public class ExcelWriter implements Runnable{

    private final BlockingQueue<Child> children;
    private String file=System.getProperty("user.home")+ File.separator+"children.xlsx";

    public ExcelWriter(BlockingQueue<Child> children) {
        this.children = children;
    }
    public void run() {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        SXSSFSheet sheet = workbook.createSheet("data");
        SXSSFRow row = sheet.createRow(0);

        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("性别");
        row.createCell(3).setCellValue("生日");
        row.createCell(4).setCellValue("身高");
        row.createCell(5).setCellValue("失踪日期");
        row.createCell(6).setCellValue("所在地");
        row.createCell(7).setCellValue("失踪地点");
        row.createCell(8).setCellValue("描述");
        row.createCell(9).setCellValue("图片地址");

        int rownum=1;
        while (true){
            Child child;
            try {
                child = children.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
            if (child == null) {
                break;
            }
            row=sheet.createRow(rownum++);

            row.createCell(0).setCellValue(child.getId());
            row.createCell(1).setCellValue(child.getName());
            row.createCell(2).setCellValue(child.getSex());
            row.createCell(3).setCellValue(child.getBirthday());
            row.createCell(4).setCellValue(child.getHeight());
            row.createCell(5).setCellValue(child.getDipearDay());
            row.createCell(6).setCellValue(child.getLocation());
            row.createCell(7).setCellValue(child.getDispearLocation());
            row.createCell(8).setCellValue(child.getDiscreption());
            row.createCell(9).setCellValue(child.getUrl());

        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

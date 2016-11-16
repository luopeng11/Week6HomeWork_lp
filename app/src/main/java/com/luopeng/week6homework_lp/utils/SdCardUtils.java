package com.luopeng.week6homework_lp.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by my on 2016/11/16.
 */
public class SdCardUtils {
    public static void saveToCache(String path,String fileName, byte[] data) {
        File file=new File(path,fileName);
        FileOutputStream fos=null;
        try {
            fos=new FileOutputStream(file);
            fos.write(data);
            fos.flush();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
     public static byte[] getData(String filePath) {
        FileInputStream fis=null;
        ByteArrayOutputStream baos=null;
        try {
            fis=new FileInputStream(new File(filePath));
            baos=new ByteArrayOutputStream();

            int len=0;
            byte[] buf = new byte[1024 * 8];

            while ((len=fis.read(buf))!=-1){
                baos.write(buf,0,len);
                baos.flush();
            }
            return baos.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //Log.d("flag", "---------->getData: 没有从sd卡中获取到数据" +filePath);
        return null;
    }
}

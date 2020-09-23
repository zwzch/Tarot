package com.zwzch.fool.common.utils;

import com.zwzch.fool.common.exception.CommonExpection;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class FileUtils {
    public static String readFile(String fileName) {
        try {
            File file = readFileReturnFile(fileName);
            if(file==null) {
                throw new Exception("file is not exist, file:" + fileName);
            }

            byte[] data = new byte[10 * 1024 * 1024];
            InputStream in = new FileInputStream(file);
            int n = in.read(data);
            in.close();

            return new String(data, 0, n);
        } catch (Exception e) {
            throw new CommonExpection("read file execption, fileName:" + fileName, e);
        }
    }

    public static File readFileReturnFile(String fileName) {
        if(fileName==null || fileName.length()==0)  {
            throw new CommonExpection("file name is null");
        }

        try {
            URL url = FileUtils.class.getClassLoader().getResource(fileName);
            File file = null;

            if(url!=null) {
                file = new File(url.getFile());
                if(!file.exists()) {
                    file = null;
                }
            }

            if(file==null) {
                file = new File(fileName);
                if(!file.exists()) {
                    file = null;
                }
            }
            return file;

        } catch (Exception e) {
            throw new CommonExpection("read file execption, fileName:" + fileName, e);
        }
    }


}

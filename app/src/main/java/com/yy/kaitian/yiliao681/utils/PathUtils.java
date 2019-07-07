package com.yy.kaitian.yiliao681.utils;


import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PathUtils {

    private static String mStorageDir = "";

    private PathUtils() {
    }

    public static void init() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mStorageDir = Environment.getExternalStorageDirectory().getPath();
        } else {
            Log.e("PathUtils", "SDCard 不可用");
        }
    }

    public static String getAppPathDir() {
        if (TextUtils.isEmpty(mStorageDir)) {
            return null;
        }

        String path = mStorageDir + File.separator;
        File file = new File(path);
        boolean isSuccess;
        isSuccess = file.exists() || file.mkdirs();

        if (isSuccess) {
            return path;
        }
        return null;
    }

    public static String getPathDir(String... names) {
        if (TextUtils.isEmpty(mStorageDir)) {
            return null;
        }

        String path = mStorageDir;
        for (String name : names) {
            path += File.separator + name;
        }
        File file = new File(path);
        boolean isSuccess;
        isSuccess = file.exists() || file.mkdirs();

        if (isSuccess) {
            return path;
        }
        return null;
    }






    public static EFileType getFileType(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Log.e("isPic", "文件路径是null");
            return EFileType.UNKNOWN;
        }
        if (filePath.endsWith(".jpg")
                || filePath.endsWith(".png")
                || filePath.endsWith(".jpeg")
                || filePath.endsWith(".gif")) {
            return EFileType.PIC;
        }
        if (filePath.endsWith(".txt")) {
            return EFileType.TXT;
        }
        if (filePath.endsWith(".doc") || filePath.endsWith(".docx")) {
            return EFileType.DOC;
        }
        if (filePath.endsWith(".ppt")) {
            return EFileType.PPT;
        }
        if (filePath.endsWith(".xlsx")) {
            return EFileType.EXCEL;
        }
        if (filePath.endsWith(".rar")) {
            return EFileType.RAR;
        }
        if (filePath.endsWith(".apk")) {
            return EFileType.APK;
        }

        return EFileType.UNKNOWN;
    }

    public static String getRoot() {
        return mStorageDir;
    }

    public enum EFileType {
        PIC, TXT, DOC, PPT, EXCEL, RAR, APK, UNKNOWN
    }

    public static List<File> selectorImage(File file){
        final List<File> fileList = new ArrayList<>();
        if(file == null){
            return null;
        }
        if(!file.isDirectory()){
            return null;
        }
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                if (fileName.endsWith(".jpg")
                        || fileName.endsWith(".png")
                        || fileName.endsWith(".jpeg")
                        || fileName.endsWith(".gif")) {
                    fileList.add(file);
                }
                return false;
            }
        });

        return fileList;
    }


    /**查询文件夹下 的 文件 文件夹
     * @param currentRoot
     * @return
     */
    public static List<File> getFileChildList(String currentRoot) {
        File file = new File(currentRoot);
        if(!file.isDirectory()){
            return null;
        }
        List<File> fileList = new ArrayList<>();
        File[] files = file.listFiles();
        return fileList = Arrays.asList(files);
    }

    /**查询文件夹下 的 文件 文件夹
     * @param file
     * @return
     */
    public static List<File> getFileChildList(File file) {
        if(!file.isDirectory()){
            return null;
        }
        List<File> fileList = new ArrayList<>();
        File[] files = file.listFiles();
        return fileList = Arrays.asList(files);
    }



}

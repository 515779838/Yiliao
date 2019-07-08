package com.yy.kaitian.yl.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/5/22.
 * 路径管理的工具类，一些常用的路径放到这里
 */
public class CommonPath {



    private static String mSDCardDir;
    private static final String GLOBAL_PACKAGE_NAME = "";

    protected static void initExtStorageDir() {
        String[] sfiles = {
                "/sdcard",
                "/storage/sdcard",
                "/storage/sdcard0",
                "/storage/sdcard1",
                "/storage/sdcard2",
                "/storage/sdcard-ext",
                "/storage/external_sd",
                "/storage/flash",
                "/storage/internal",
                "/storage/external",
                "/mnt/sdcard",
                "/mnt/sdcard0",
                "/mnt/sdcard1",
                "/mnt/sdcard2",
                "/mnt/sdcard-ext",
                "/mnt/external_sd",
                "/mnt/flash",
                "/mnt/internal",
                "/mnt/external"};

        for (String sfile : sfiles) {
            final File file = new File(sfile);
            if (file.isDirectory() && file.exists() && file.canWrite()) {
                File subFile = new File(sfile + "/app");
                if (subFile.exists()) {
                    if (subFile.canWrite()) {
                        mSDCardDir = sfile;
                        return;
                    }
                } else {
                    boolean ret = subFile.mkdirs();
                    if (ret) {
                        mSDCardDir = sfile;
                        return;
                    }
                }
            }
        }

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/app/");
            if (path.exists() && path.isDirectory()) {
                mSDCardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                return;
            } else {
                boolean ret = path.mkdirs();
                if (ret) {
                    mSDCardDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                    return;
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append(Environment.getExternalStorageDirectory().getPath()).append("/Android/data/").append(GLOBAL_PACKAGE_NAME);

            File path1 = new File(sb.toString() + "/app/");
            if (path1.exists() && path1.isDirectory()) {
                mSDCardDir = sb.toString();
            } else {
                boolean ret = path.mkdirs();
                if (ret) {
                    mSDCardDir = sb.toString();
                }
            }
        }
    }

    public static String getDirLog(String dateTime) {
        String dir = getDirSDCard();

        dir += "log";
        dir += "/";

        if (dateTime != null && !TextUtils.isEmpty(dateTime)) {
            dir += dateTime;
            dir += "/";
        }

        File f = new File(dir);
        if (!f.exists()) {
            boolean ret = f.mkdirs();
            if (!ret) {
                return null;
            }
        }

        return dir;
    }


    public static String getDirSDCard() {
        if (TextUtils.isEmpty(mSDCardDir)) {
            initExtStorageDir();
            if (TextUtils.isEmpty(mSDCardDir)) {
                mSDCardDir = Environment.getExternalStorageDirectory().toString() + "/data/"+GLOBAL_PACKAGE_NAME;
            }
        }

        String dir = mSDCardDir + "/app/";
        File f = new File(dir);
        if (!f.exists()) {
            boolean ret = f.mkdirs();
            AppLog.instance().i("makeDir ret=" + ret + dir);
        }

        return dir;
    }

    private static String makeDirPath(String folder, String userUuid) {
        String dir = getDirSDCard();

        if (userUuid != null && !TextUtils.isEmpty(userUuid)) {
            dir += userUuid;
            dir += "/";
        }

        dir += folder;
        dir += "/";

        File f = new File(dir);
        if (!f.exists()) {
            boolean ret = f.mkdirs();
            AppLog.instance().i("makeDir ret=" + ret + dir);
        }

        return dir;
    }

    public static String getDirApp() {

        String dir = Environment.getDataDirectory().toString() + "/data/"+ GLOBAL_PACKAGE_NAME +"/app/";

        File f = new File(dir);
        if (!f.exists()) {
            boolean ret = f.mkdirs();
            AppLog.instance().i("makeDir ret=" + ret + dir);
        }

        return dir;
    }

}

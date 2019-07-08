package com.yy.kaitian.yl.utils;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.util.Date;

//
public final class AppLog {

    private final boolean DEBUG = false; // 是否输出debug

    private boolean mSaveLogToSdcard = false;// true: 输出到SDCard文件中, false: 不输出log到SDCard中
    private String tag = "yiliao";
    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter;
    private String mLogPath;

    private String logFileName;

    private AppLog() {
        if (mSaveLogToSdcard) {
            formatter = DateFormat.getDateInstance(DateFormat.MILLISECOND_FIELD);
            DateFormat dirFormatter = DateFormat.getDateInstance(DateFormat.DATE_FIELD);
            mLogPath = dirFormatter.format(new Date());
            logFileName = DateFormat.getDateInstance(DateFormat.HOUR_OF_DAY0_FIELD).format(new Date());
        }
    }

    /* 此处使用一个内部类来维护单例 */
    private static class SingletonFactory {
        private static AppLog mInstance = new AppLog();
    }

    public static AppLog instance() {
        return SingletonFactory.mInstance;
    }


    /**
     * Get The Current Function Name
     */
    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return "empty";
        }

        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("  [");
            builder.append(Thread.currentThread().getName());
            builder.append(" : ");
            builder.append(st.getFileName());
            builder.append(" : ");
            builder.append(st.getLineNumber());
            builder.append("  ");
            builder.append(st.getMethodName());
            builder.append("]");
            return builder.toString();
        }

        return "empty";
    }

    /**
     * The Log Level:i
     */
    public void i(Object obj) {
        if (!DEBUG) {
            return;
        }

        if (!mSaveLogToSdcard) {
            Log.i(tag, getLogString(obj));
        } else {
            saveLogInfo2File(tag, "info", getLogString(obj));
        }
    }
    /**
     * The Log Level:i
     */
    public void iToSd(Object obj) {
//        saveLogInfo2File(tag, "info", getLogString(obj));
    }

    /**
     * The Log Level:d
     */
    public void d(Object obj) {
        if (!DEBUG) {
            return;
        }
        if (!mSaveLogToSdcard) {
            Log.d(tag, getLogString(obj));
        } else {
            saveLogInfo2File(tag, "debug", getLogString(obj));
        }

    }


    /**
     * The Log Level:V
     */
    public void v(Object str) {
        if (!DEBUG) {
            return;
        }
        String s = getLogString(str);
        if (!mSaveLogToSdcard) {
            Log.v(tag, s);
        } else {
            saveLogInfo2File(tag, "verbose", s);
        }
    }

    /**
     * The Log Level:w
     */
    public void w(Object str) {
        if (!DEBUG) {
            return;
        }

        String s = getLogString(str);
        if (mSaveLogToSdcard) {
            saveLogInfo2File(tag, "warn", s);
        } else {
            Log.w(tag, s);
        }
    }

    /**
     * The Log Level:e
     */
    public void e(Object obj) {
        if (!DEBUG) {
            return;
        }
        String s = getLogString(obj);
        if (mSaveLogToSdcard) {
            saveLogInfo2File(tag, "error", s);
        } else {
            Log.e(tag, s);
        }
    }


    public void e(Exception ex) {
        if (!DEBUG) {
            return;
        }


        if (mSaveLogToSdcard) {
            saveLogInfo2File(tag, "error", getExceptionStr(ex));
        } else {
            Log.e(tag, "error", ex);
        }
    }


    private static String getExceptionStr(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        return writer.toString();
    }

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private void saveLogInfo2File(String tag, String level, String sb) {
        try {
            Date curDate = new Date();
//            String time = formatter.format(curDate);
            String time = MyDateUtil.getTimeStamp(System.currentTimeMillis(),MyDateUtil.YYYY_MM_DD_HH_MM_SS);
            String fileName = "医疗"+tag + logFileName + ".log";
//            String path = CommonPath.getDirLog(mLogPath);
            String path = PathUtils.getPathDir("医疗测试log");
            if (path == null) {
                return;
            }
            File file = new File(path + File.separator + fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append(time);
            sBuilder.append("  ");
            sBuilder.append(level);
            sBuilder.append("  ");
            sBuilder.append(sb);
            sBuilder.append("\r\n");

            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write(sBuilder.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            if (mSaveLogToSdcard) {
                Log.e(tag, "an error while writing file...", e);
            }
        }
    }

    private String getLogString(Object obj) {
        return String.format("%s%s", getFunctionName(), obj.toString());
    }

}

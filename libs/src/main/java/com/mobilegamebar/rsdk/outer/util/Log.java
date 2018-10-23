package com.mobilegamebar.rsdk.outer.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("SimpleDateFormat")
public class Log {

    private static boolean isInit = false;
    public enum LogLevel {
        Verbose,
        Debug,
        Info,
        Warn,
        Error
    }

    // 写log文件策略
    public enum LogFilePolicy {
        NoLogFile,          // 不写文件
        PerDay,             // 一天只产生一个log文件
        PerLaunch           // 每次运行均产生一个log文件
    }

    public static class LogConfig {
        public static final String DEFAULT_FILE_SUFFIX = "log";

        public String filePrefix;           // log文件名前缀
        public String fileSuffix;           // log文件名后缀
        public String fileDir;              // log文件目录，绝对路径
        public String filePath;             // log文件绝对路径，指定这个之后fileDir被忽略
        //public boolean useFileLock;         // log文件被多进程同时写的话，需要设为true
        public LogFilePolicy policy;
        public LogLevel outputLevel;        // 输出级别，大于等于此级别的log才会输出
        public LogLevel fileLevel;          // 输出到文件的级别，大于等于此级别的log才会写入文件
        public int fileFlushCount;          // 每次累计log超过此条数时，会检查是否需要flush log文件
        public int fileFlushInterval;       // 定时每隔一定秒数检查是否需要flush log文件
        public int fileFlushMinInterval;    // 距离上次flush最少需要多少秒
        //public boolean isFileCreator;       // 多进程读写时使用，log文件创建者进程设为true
        public int fileKeepDays;            // Log文件保留天数，启动时检查

        public LogConfig() {
            policy = LogFilePolicy.PerLaunch;
            outputLevel = LogLevel.Verbose;
            fileLevel = LogLevel.Info;
            fileFlushCount = 10;
            fileFlushInterval = 10;
            fileFlushMinInterval = 10;
            //isFileCreator = true;
            fileKeepDays = 3;
            fileSuffix = DEFAULT_FILE_SUFFIX;
        }

        public LogConfig(LogConfig cfg) {
            this.filePrefix = cfg.filePrefix;
            this.fileSuffix = cfg.fileSuffix;
            this.fileDir = cfg.fileDir;
            this.filePath = cfg.filePath;
            //this.useFileLock = cfg.useFileLock;
            this.policy = cfg.policy;
            this.outputLevel = cfg.outputLevel;
            this.fileLevel = cfg.fileLevel;
            this.fileFlushCount = cfg.fileFlushCount;
            this.fileFlushInterval = cfg.fileFlushInterval;
            this.fileFlushMinInterval = cfg.fileFlushMinInterval;
            //this.isFileCreator = cfg.isFileCreator;
            this.fileKeepDays = cfg.fileKeepDays;
        }
    }


    private static LogThread logThread;   // 用于在另一个线程写log文件

    private static LogConfig config = new LogConfig();
    // 写文件线程未准备好的时候，将可以写入文件的log先缓存起来
    private static List<String> logList = Collections.synchronizedList(new ArrayList<String>());

    private static final String LOG_TAG = "Log";
    private static Context context;

    public static void init(Context ctx, LogLevel logLevel, String outputDir) {

        LogConfig cfg = new LogConfig();
        cfg.policy = LogFilePolicy.PerLaunch;
        cfg.outputLevel = logLevel;
        cfg.fileLevel = logLevel;
        cfg.fileDir = outputDir;

        Log.init(ctx, cfg);
    }

    /**
     * 使用Log之前，必须先init
     *
     */
    public static void init(Context ctx, LogConfig cfg) {

        if (isInit) {
            return;
        }

        i(LOG_TAG, "init Log");
        config = new LogConfig(cfg);
        context = ctx;

        if (config.fileDir == null && config.filePath == null) {
        	if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                File SDFile = Environment.getExternalStorageDirectory();
                config.fileDir = SDFile.getAbsolutePath()+"/ttgamesdk/logs" ;
            } 
        }

        if (config.fileDir != null) {
            File logDir = new File(config.fileDir);
            if (!logDir.exists()) {
                Log.i("Log", "create log fileDir: " + logDir.getAbsolutePath());
                logDir.mkdirs();
            }
        }

        if (config.filePath == null && config.fileDir!=null) {
            SimpleDateFormat format;
            if (config.policy == LogFilePolicy.PerLaunch) {
                format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
            } else {
                format = new SimpleDateFormat("yyyy-MM-dd");
            }

            config.filePath = config.fileDir + "/" + (config.filePrefix != null ? config.filePrefix + "_" : "")
                    + format.format(new Date()) + "." + (config.fileSuffix!=null?config.fileSuffix: LogConfig.DEFAULT_FILE_SUFFIX);
            File logFile = new File(config.filePath);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i(LOG_TAG, "log file name: " + config.filePath);

//        if (config.fileDir != null) {
//            i(LOG_TAG, "log fileDir = " + config.fileDir);
//        }

        if (config.policy != LogFilePolicy.NoLogFile && logThread == null && config.filePath != null) {
            logThread = new LogThread("LogThread", config);
            logThread.start();
        }

        isInit = true;

    }

    public static LogConfig getConfig() {
        return config;
    }

    public static String getLogDir() {
        return config.fileDir;
    }

    private static String tag(Object tag) {
        return tag instanceof String ? (String) tag
            : tag.getClass().getSimpleName();
    }

    private static boolean isLoggable(LogLevel level) {
        return level.compareTo(config.outputLevel) >= 0;
    }

    private static String levelToString(LogLevel level) {
        String str;
        switch (level) {
            case Debug:
                str = "Debug";
                break;
            case Error:
                str = "Error";
                break;
            case Info:
                str = "Info";
                break;
            case Verbose:
                str = "Verbose";
                break;
            case Warn:
                str = "Warn";
                break;
            default:
                str = "Debug";
                break;
        }
        return str;
    }

    public static String getLogFilePath() {
        return config.filePath;
    }

    private static void logToFile(String tag, LogLevel level, String message, Throwable t) {

        if (config.policy != LogFilePolicy.NoLogFile) {
            if (logThread == null || !logThread.isReady()) {
                if (level.compareTo(config.fileLevel) >= 0) {
                    // 文件线程未准备好，先缓存
                    logList.add(LogThread.getFormattedString(tag, level, message));
                }
            } else {
                logThread.logToFile(tag, level, message, t);
            }
        }
    }

    public static void log(String tag, LogLevel level, String message) {
        if (!Log.isLoggable(level)) {
            return;
        }
        message = formatLogText(message);
        switch (level) {
            case Debug:
                android.util.Log.d(tag, message);
                break;
            case Error:
                android.util.Log.e(tag, message);
                break;
            case Info:
                android.util.Log.i(tag, message);
                break;
            case Verbose:
                android.util.Log.v(tag, message);
                break;
            case Warn:
                android.util.Log.w(tag, message);
                break;
            default:
                android.util.Log.d(tag, message);
                break;
        }
        logToFile(tag, level, message, null);
    }

    private static void logError(String tag, String msg, Throwable tr) {
        if (Log.isLoggable(LogLevel.Error)) {
            msg = formatLogText(msg);
            if (tr == null) {
                android.util.Log.e(tag, msg);
            } else {
                android.util.Log.e(tag, msg, tr);
            }
            logToFile(tag, LogLevel.Error, msg, tr);
        }
    }

//    public static void log(String tag, LogLevel level, String message, Throwable throwable) {
//        switch (level) {
//            case Debug:
//                android.util.Log.d(tag, message, throwable);
//                break;
//            case Error:
//                android.util.Log.e(tag, message, throwable);
//                break;
//            case Info:
//                android.util.Log.i(tag, message, throwable);
//                break;
//            case Verbose:
//                android.util.Log.v(tag, message, throwable);
//                break;
//            case Warn:
//                android.util.Log.v(tag, message, throwable);
//                break;
//            default:
//                android.util.Log.d(tag, message, throwable);
//                break;
//        }
//    }

    private static String formatLogText(String message) {
        if (message == null) {
            message = "null";
        }
//        if (BuildConfig.DEBUG) {
            int line = -1;
            String filename = null;
            if (Thread.currentThread().getStackTrace().length > 5) {
                line = Thread.currentThread().getStackTrace()[5].getLineNumber();
                filename = Thread.currentThread().getStackTrace()[5].getFileName();
            }
            StringBuilder sb = new StringBuilder(message);
//        sb.append("(P:");
//        sb.append(Process.myPid());
//        sb.append(")");
//        sb.append("(T:");
//        sb.append(Thread.currentThread().getId());
//        sb.append(")");
//        sb.append("(C:");
//        sb.append(tag);
//        sb.append(")");
            if (filename != null) {
                sb.append(" (");
                sb.append(filename);
            }
            if (line > 0) {
                sb.append(":");
                sb.append(line);
                sb.append(")");
            }
            return sb.toString();
//        } else {
//            return message;
//        }
    }

    public static void v(String tag, String message) {

        Log.log(tag, LogLevel.Verbose, message);
    }

    public static void d(String tag, String message) {

        Log.log(tag, LogLevel.Debug, message);
    }

    public static void i(String tag, String message) {

        Log.log(tag, LogLevel.Info, message);
    }

    public static void w(String tag, String message) {

        Log.log(tag, LogLevel.Warn, message);
    }

    public static void e(String tag, String message) {

        Log.log(tag, LogLevel.Error, message);
    }

    public static void e(String tag, Throwable throwable) {
        Log.logError(tag, throwable!=null?throwable.getMessage():"", throwable);
    }

    public static void e(String tag, String msg, Throwable t) {
        Log.logError(tag, msg, t);
    }

    public static void v(Object obj, String message) {
        String myTag = tag(obj);
        Log.log(myTag, LogLevel.Verbose, obj+":"+message);
    }

    public static void d(Object obj, String message) {
        String myTag = tag(obj);
        Log.log(myTag, LogLevel.Debug, obj+":"+message);
    }

    public static void i(Object obj,String message) {
        String myTag = tag(obj);
        Log.log(myTag, LogLevel.Info, obj+":"+message);
    }

    public static void w(Object obj, String message) {
        String myTag = tag(obj);
        Log.log(myTag, LogLevel.Warn, obj+":"+message);
    }

    public static void e(Object obj, String message) {
        String myTag = tag(obj);
        Log.log(myTag, LogLevel.Error, obj+":"+message);
    }

    public static void v(Object obj, String msgFormat, Object... args) {
        String msg = String.format(msgFormat,args);
        //v(obj,msg);
        //为保持调用栈层级一致，直接调用log函数而不是上层封装
        String myTag = tag(obj);
        Log.log(myTag, LogLevel.Verbose, obj+":"+msg);
    }

    public static void d(Object obj, String msgFormat, Object... args) {
        String msg = String.format(msgFormat,args);
        //d(obj,msg);
        String myTag = tag(obj);
        Log.log(myTag, LogLevel.Debug, obj+":"+msg);
    }

    public static void i(Object obj, String msgFormat, Object... args) {
        String msg = String.format(msgFormat,args);
        //i(obj,msg);
        String myTag = tag(obj);
        Log.log(myTag, LogLevel.Info, obj+":"+msg);
    }

    public static void w(Object obj, String msgFormat, Object... args) {
        String msg = String.format(msgFormat,args);
        //w(obj,msg);
        String myTag = tag(obj);
        Log.log(myTag, LogLevel.Warn, obj+":"+msg);
    }

    public static void e(Object obj, String msgFormat, Object... args) {
        String msg = String.format(msgFormat,args);
        //e(obj,msg);
        String myTag = tag(obj);
        Log.log(myTag, LogLevel.Error, obj+":"+msg);
    }

    public static void forceFlush() {
        if (logThread != null) {
            logThread.forceFlush();
        }
    }

    public static void syncFlush() {
        if (logThread != null) {
            logThread.sendFlush(true);
            try {
                synchronized (logThread) {
                    logThread.wait(3000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logThread.forceFlush();
        }
    }

    /**
     * 用于写log文件的线程
     *
     * @author daixiang
     */
    private static class LogThread extends Thread {

        private static final int LogMessageType = 0;
        private static final int TimerMessageType = 1;
        private static final int LogThrowableType = 2;
        private static final int FlushLog = 3;
        private static final int SyncFlushLog = 4;
        private static final int CheckOldLogFiles = 5;
        private static final int CheckLogFileSize = 6;

        private LogThreadHandler handler;  // 使用此handler将log消息发到此线程处理
        private LogConfig config;
        //private String filePath;
        private boolean isReady = false;

        public LogThread(String name, LogConfig cfg) {
            super(name);
            config = cfg;
        }

        public boolean isReady() {
            return isReady;
        }

        private static String getFormattedString(String tag, LogLevel level, String msg) {

            String thread = (Looper.getMainLooper() == Looper.myLooper()) ? "["+Process.myPid()+":Main]"
                : ("["+Process.myPid()+":"+Thread.currentThread().getId()+"]");
            String strLevel = "[" + Log.levelToString(level) + "]";
            return thread + "[" + tag + "]" + strLevel + " " + msg;
        }

        public void logToFile(String tag, LogLevel level, String msg, Throwable t) {
            if ((config.policy != LogFilePolicy.NoLogFile)
                && (level.compareTo(config.fileLevel) >= 0)
                && (handler != null)) {

                String logMsg = getFormattedString(tag, level, msg);

                Message threadMessage;
                if (t == null) {
                    threadMessage = handler.obtainMessage(LogMessageType);
                    threadMessage.obj = logMsg;
                } else {
                    threadMessage = handler.obtainMessage(LogThrowableType);
                    threadMessage.obj = logMsg;
                    Bundle b = new Bundle();
                    b.putSerializable("throwable", t);
                    threadMessage.setData(b);
                }

                if (threadMessage != null) {
                    handler.sendMessage(threadMessage);
                }
            }
        }

        public void sendFlush(boolean sync) {
            if (handler != null) {
                if (sync) {
                    handler.sendEmptyMessage(SyncFlushLog);
                } else {
                    handler.sendEmptyMessage(FlushLog);
                }
            }
        }

        public void forceFlush () {
            try {
                handler.flush(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void logToFile(String tag, LogLevel level, String msg) {
            logToFile(tag, level, msg, null);
        }

//        public String getFilePath() {
//            return filePath;
//        }

        public void run() {

            Looper.prepare();

            handler = new LogThreadHandler(config);
            isReady = true;

            // 将之前缓存的log先写入文件
            List<String> list = new ArrayList<String>(logList);
            try {
                if (list.size() > 0) {
                    Log.d(LOG_TAG, "write logs before Log thread ready to file: " + list.size());
                    for (String s : list) {
                        handler.writeLine(s);
                    }
                    handler.flush(true);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            logList.clear();
            list.clear();
            list = null;
            handler.sendEmptyMessage(CheckOldLogFiles);
            long time = 30 * 1000;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage(CheckLogFileSize);
                    handler.sendMessage(msg);
                }
            }, time, time);

            Looper.loop();
        }

        private class LogThreadHandler extends Handler {

            private SimpleDateFormat dateFormat;
            private BufferedWriter writer;
            //private FileChannel fileChannel;
            //private LogThread logThread;
            private LogConfig config;
            private int logCounter;
            private long lastFlushTime;

            private synchronized void writeLine(String formattedStr) throws IOException {
                if (writer != null) {
                    FileLock lock = null;
//                    if (config.useFileLock) {
//                        lock = fileChannel.lock();
//                    }
                    writer.write(dateFormat.format(new Date()) + " " + formattedStr);
                    writer.newLine();
//                    if (config.useFileLock) {
//                        lock.release();
//                    }
                }
            }

            private synchronized void newLine() throws IOException {
                if (writer != null) {
                    FileLock lock = null;
//                    if (config.useFileLock) {
//                        lock = fileChannel.lock();
//                    }
                    writer.newLine();
//                    if (config.useFileLock) {
//                        lock.release();
//                    }
                }
            }

            public synchronized void flush(boolean force) throws IOException {
                if (writer != null) {

                    long now = System.currentTimeMillis();
                    // 不要太频繁flush，最低间隔
                    if (force || ((now - lastFlushTime) > (config.fileFlushMinInterval * 1000))) {
//                        FileLock lock = null;
//                        if (config.useFileLock) {
//                            lock = fileChannel.lock();
//                        }
                        writer.flush();
//                        if (config.useFileLock) {
//                            lock.release();
//                        }
                        lastFlushTime = System.currentTimeMillis();
                        logCounter = 0;
                    } else {
                        logCounter++;
                    }
                }

            }

            public LogThreadHandler(LogConfig config) {
                //logThread = thread;
                super();
                this.config = config;
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                boolean append;
                append = config.policy != LogFilePolicy.PerLaunch;

                try {

                    File file = new File(config.filePath);
//                    boolean isFileCreator = !file.exists();
                    FileOutputStream fos = new FileOutputStream(config.filePath, append);
                    OutputStreamWriter osw = new OutputStreamWriter(fos);
//                    fileChannel = fos.getChannel();
                    writer = new BufferedWriter(osw);

//                    if (isFileCreator) {
                        if (config.policy == LogFilePolicy.PerDay) {
                            newLine();
                        }
                        // 在文件开头加入一个易于识别的行
                        writeLine(getFormattedString(LOG_TAG, config.fileLevel, "--------------------- Log Begin ---------------------"));
                    writeAppInfo();
                        flush(true);
//                    }
                } catch (Exception e) {
                    writer = null;
                    e.printStackTrace();
                }

                if (writer != null && config.fileFlushInterval > 0) {
                    long time = config.fileFlushInterval * 1000;
                    new Timer().schedule(new TimerTask() {

                        @Override
                        public void run() {
                            Message msg = obtainMessage(TimerMessageType);
                            sendMessage(msg);
                        }
                    }, time, time);
                }
            }

            private void writeAppInfo() throws IOException {
                writeLine("manufacturer: " + Build.MANUFACTURER);
                writeLine("product: " + Build.PRODUCT);
                writeLine("model: " + Build.MODEL);
                writeLine("sdk: " + Build.VERSION.SDK);
                writeLine("release: " + Build.VERSION.RELEASE);

                PackageManager mgr = context.getPackageManager();
                if (mgr != null) {
                    try {
                        PackageInfo info = mgr.getPackageInfo(context.getPackageName(), 0);
                        writeLine("version: " + info.versionName + ", code: " + info.versionCode);
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(LOG_TAG, e);
                    }
                }
            }

            public void flushIfNeeded() throws IOException {
                if (logCounter > config.fileFlushCount) {
                    flush(false);
                } else {
                    logCounter++;
                }
            }

            @Override
            public void handleMessage(Message msg) {

                if (writer == null) {
                    return;
                }

                try {
                    switch (msg.what) {

                        case LogMessageType: {
                            writeLine((String) msg.obj);
                            flushIfNeeded();
                            break;
                        }
                        case LogThrowableType: {
                            writeLine((String) msg.obj);
                            Bundle data = msg.getData();
                            if (data != null) {
                                Throwable t = (Throwable) data
                                    .getSerializable("throwable");
                                if (t != null) {
                                    synchronized (this) {
                                        PrintWriter pw = new PrintWriter(writer);
                                        t.printStackTrace(pw);
                                    }
                                    //pw.close();   // 不能close，否则内部的bufferedWriter也会被close！
                                    newLine();
                                    flush(true);  // 异常，立刻flush
                                } else {
                                    flushIfNeeded();
                                }
                            } else {
                                flushIfNeeded();
                            }
                            break;
                        }
                        case TimerMessageType:
                            flush(false);
                            break;
                        case FlushLog:
                            flush(true);
                            break;
                        case SyncFlushLog:
                            flush(true);
                            synchronized (Log.LogThread.this) {
                                Log.LogThread.this.notifyAll();
                            }
                            break;
                        case CheckOldLogFiles:
                            checkAndDeleteOldLogFiles();
                            break;
                        case CheckLogFileSize:
                            checkIfFileTooLarge();
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private synchronized void checkIfFileTooLarge() throws IOException {
                if (writer == null) {
                    return;
                }
                File file = new File(config.filePath);
                //文件太大了
                if (file.length() > (2 * 1024 * 1024)) {
                    flush(true);
                    writer.close();
                    String fileName = config.filePath;
                    String suffix = config.fileSuffix != null ? config.fileSuffix : LogConfig.DEFAULT_FILE_SUFFIX;
                    if (config.fileSuffix != null) {
                        fileName = fileName.substring(0, fileName.lastIndexOf("." + suffix));
                    }
                    for (int i = 1; i < 1000000; i++) {
                        String newFileName = fileName + "(" + i + ")" + "." + suffix;
                        File newFile = new File(newFileName);
                        if (!newFile.exists()) {
                            file.renameTo(newFile);
                            //仍然保持log文件名不变
                            FileOutputStream fos = new FileOutputStream(config.filePath, false);
                            OutputStreamWriter osw = new OutputStreamWriter(fos);

                            writer = new BufferedWriter(osw);
                            writeLine(getFormattedString(LOG_TAG, config.fileLevel, "--------------------- Log continue begin ---------------------"));
                            writeLine(getFormattedString(LOG_TAG, config.fileLevel, "new Log file due to old file too large: " + newFileName));
                            flush(true);
                            break;
                        }
                    }
                }
            }

            private void checkAndDeleteOldLogFiles() {
                if (config.fileKeepDays > 0 && config.fileDir != null) {

                    File dir = new File(config.fileDir);
                    File[] logFiles = dir.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            return filename.endsWith("." + config.fileSuffix);
                        }
                    });

                    if (logFiles != null) {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        for (File f : logFiles) {
                            Date date = new Date(f.lastModified());
                            Date now = new Date();
                            //Log.v(LOG_TAG, "file " + f.getAbsolutePath() + " created " + formatter.format(date) + " now " + now.getTime() + " last " + f.lastModified());
                            //Log.v(LOG_TAG, "days " + (((now.getTime()-f.lastModified())/1000f)/(24*60*60f)));
                            if ((((now.getTime()-f.lastModified())/1000f)/(24*60*60f)) - config.fileKeepDays > 0) {
                                f.delete();
                            }

                        }
                    }
                }
            }
        }
    }
}

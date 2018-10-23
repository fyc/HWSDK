package com.yiyou.gamesdk.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chenshuide on 15/6/8.
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtils {

    public static final long DAY = 86400000;  //一天的毫秒
    public static final int HOUR = 3600000;  //一小时的毫秒
    public static final int MIN = 60000;    //一分钟的毫秒

    /**
     * 格式化当前时间为 yyyy-mm-dd hh:mm:ss
     *
     * @return timeStr
     */
    public static String formatNowTime() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sf.format(new Date());
        return sf.format(new Date());
    }


    public static String format(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH-mm");//华为手机 文件名字是不能带冒号的
        String format = sf.format(date);
        return format;
    }

    public static String times(String time) {
        try {
            SimpleDateFormat sdr = new SimpleDateFormat("yyyy.MM.dd");
            @SuppressWarnings("unused")
            long lcc = Long.valueOf(time);
            String times = sdr.format(new Date(lcc));
            return times;
        } catch (Exception e) {
            return "";
        }
    }

    public static String fromDayStr(String timeStamp) {
        return String.valueOf(1 + (new Date().getTime() - Long.valueOf(timeStamp)) / DAY);
    }

    public static int fromDay(String timeStamp) {
        Date startDate = new Date(Long.valueOf(timeStamp));
        Date endDate = new Date();
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24));

    }

    public static String formatTimeYMD(long seconds) {
        long ms = seconds * 1000;
        Date date = new Date(ms);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    /**
     * @return ??天??小时
     */
    public static String getFriendlyTimeDifference(long timeDiffMillis) {
        int day = (int) (timeDiffMillis / DAY);
        int hour = Math.round((timeDiffMillis % DAY) / HOUR);
        if (day > 0) {
            if (hour == 0) return String.format(Locale.CHINA, "%d天", day);
            return String.format(Locale.CHINA, "%d天%d小时", day, hour);
        } else {
            if (hour > 0) {
                return String.format(Locale.CHINA, "%d小时", hour);
            } else {
                int min = Math.round(((timeDiffMillis % DAY) % HOUR / MIN));
                if (min == 0) return "不足1分钟";
                return String.format(Locale.CHINA, "%d分钟", min);
            }
        }
    }




}

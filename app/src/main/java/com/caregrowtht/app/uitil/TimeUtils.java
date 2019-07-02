package com.caregrowtht.app.uitil;

import com.android.library.utils.DateUtil;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by zhoujie on 2018/3/27.
 */

public class TimeUtils {
    //获取当前时间戳
    public static long getCurTimeLong() {
        long time = System.currentTimeMillis();
        return time;
    }

    //将时间戳转换成规定格式
    public static String getDateToString(long milSecond, String pattern) {
        if (milSecond < 100000000000L) {
            milSecond = milSecond * 1000;
        }
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    //将时间戳转换成规定格式
    public static String getCurTimeLong(String pattern) {
        Date date = new Date(getCurTimeLong());
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }


    public static String GetMonth(long time) {
        SimpleDateFormat format = new SimpleDateFormat("MM");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
//        Log.e("msg", t1);

        return t1;
    }


    public static String GetDay(long time) {
        SimpleDateFormat format = new SimpleDateFormat("dd");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
//        Log.e("msg", t1);

        return t1;
    }


    public static String GetFriendlyTime(String time) {
        if (time == null) {
            return "";
        }

        long milSecond = Long.parseLong(time);
        if (milSecond < 100000000000L) {
            milSecond = milSecond * 1000;
        }

        Date date = new Date(milSecond);

        return GetFriendlyTime(date);
    }

    public static String GetFriendlyTime(Date time) {
        //获取time距离当前的秒数
        int ct = (int) ((System.currentTimeMillis() - time.getTime()) / 1000);

        if (ct == 0) {
            return "刚刚";
        }

        if (ct > 0 && ct < 60) {
            return ct + "秒前";
        }

        if (ct >= 60 && ct < 3600) {
            return Math.max(ct / 60, 1) + "分钟前";
        }
        if (ct >= 3600 && ct < 86400)
            return ct / 3600 + "小时前";


        return TimeUtils.getDateToString(TimeUtils.dateToTimestamp(time), "yyyy-MM-dd");
    }

    //    3.2 Date -> Timestamp
    //  父类不能直接向子类转化，可借助中间的String~~~~
    public static long dateToTimestamp(Date date) {
        Timestamp ts = new Timestamp(date.getTime());
//        System.out.println(ts); // 2017-01-15 21:33:32.203
        return ts.getTime();
    }


    /**
     * 获取日期 昨天-1 今天0 明天1 的日期
     *
     * @return
     */
    public static String dateTiem(String d, int day, String type) {
        SimpleDateFormat formatter = new SimpleDateFormat(type);
        Date date = null;
        try {
            date = formatter.parse(d);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE, day);//1 把日期往后增加一天.整数往后推,负数往前移动
            date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatter.format(date);
    }

    /**
     * 获取n个月后的日期
     *
     * @param date  传入的日期
     * @param month 前一个月-1 这个月0 下个月1 的日期
     * @param type  传入的类型 例:yyyy-MM-dd
     * @return
     */
    public static String getMonthAgo(Date date, int month, String type) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        String monthAgo = simpleDateFormat.format(calendar.getTime());
        return monthAgo;
    }

    /**
     * Get what day of week.
     * 得到星期几
     *
     * @param date 时间 yyyy-MM-dd
     * @return
     */
    private int getDayOfWeek(String date) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal.get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7 : cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * Get what day of week.
     *
     * @param timeStamp 时间戳 1520000000
     * @return
     */
    public static int getDayOfWeek(Long timeStamp) {

        String date = DateUtil.getDate(timeStamp, "yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cal.get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7 : cal.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * Get what day of the week is the selected day.
     *
     * @param type  时间格式
     * @param which 1开始 which-1
     * @return 一周中的哪一天是被选中的一天 时间
     */
    public static String getDayOfWeek(String type, int which, String today) {
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int d;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            d = -6;
        } else {
            d = 2 - cal.get(Calendar.DAY_OF_WEEK);
        }
        cal.add(Calendar.DAY_OF_WEEK, d);
        cal.add(Calendar.DAY_OF_WEEK, which - 1);
        return new SimpleDateFormat(type).format(cal.getTime());
    }

    /**
     * <pre>
     * 根据指定的日期字符串获取星期几
     * </pre>
     *
     * @param strDate 指定的日期字符串(yyyy-MM-dd 或 yyyy/MM/dd)
     * @return week
     * 星期几(MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY)
     */
    public static String getWeekByDateStr(String strDate) {
        int year = Integer.parseInt(strDate.substring(0, 4));
        int month = Integer.parseInt(strDate.substring(5, 7));
        int day = Integer.parseInt(strDate.substring(8, 10));

        Calendar c = Calendar.getInstance();

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);

        String week = "";
        int weekIndex = c.get(Calendar.DAY_OF_WEEK);

        switch (weekIndex) {
            case 1:
                week = "星期日";
                break;
            case 2:
                week = "星期一";
                break;
            case 3:
                week = "星期二";
                break;
            case 4:
                week = "星期三";
                break;
            case 5:
                week = "星期四";
                break;
            case 6:
                week = "星期五";
                break;
            case 7:
                week = "星期六";
                break;
        }
        return week;
    }

    /**
     * 获取开始-结束时间范围内的时间戳
     */
    public static class GetStartEndTime {
        private long withinDay;
        private long yesTerday;

        public long getWithinDay() {
            return withinDay;
        }

        public long getYesTerday() {
            return yesTerday;
        }

        public GetStartEndTime invoke(int start, int end) {
            long startTime = DateUtil.getStringToDate(TimeUtils.getCurTimeLong("yyyy-MM-dd 00:00"), "yyyy-MM-dd HH:mm");
            long endTime = DateUtil.getStringToDate(TimeUtils.getCurTimeLong("yyyy-MM-dd 24:00"), "yyyy-MM-dd HH:mm");
            withinDay = DateUtil.getStringToDate(TimeUtils.dateTiem(
                    DateUtil.getDate(startTime, "yyyy-MM-dd HH:mm")
                    , start, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
            yesTerday = DateUtil.getStringToDate(TimeUtils.dateTiem(
                    DateUtil.getDate(endTime, "yyyy-MM-dd HH:mm")
                    , end, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm");
            return this;
        }
    }

    /**
     * 判断时间在时间段以内如（7:00-22:00）
     *
     * @param s
     * @param e
     * @return
     * @throws ParseException
     */
    public static boolean isTimeRange(String startHour, String s, String e) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date now = df.parse(startHour);
        Date begin = df.parse(String.format("%s:00", s));
        Date end = df.parse(String.format("%s:00", e));
        Calendar nowTime = Calendar.getInstance();
        nowTime.setTime(now);
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(begin);
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(end);
        return nowTime.before(endTime) && nowTime.after(beginTime);
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(String day) {
        try {
            Calendar pre = Calendar.getInstance();
            Date predate = new Date(System.currentTimeMillis());
            pre.setTime(predate);
            Calendar cal = Calendar.getInstance();
            Date date = getDateFormat().parse(day);
            cal.setTime(date);
            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                        - pre.get(Calendar.DAY_OF_YEAR);
                if (diffDay == 0) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否为昨天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsYesterday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<>();

}

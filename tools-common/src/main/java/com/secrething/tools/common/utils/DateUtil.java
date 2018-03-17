package com.secrething.tools.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wenguo.chen on 2016/2/3.
 */
public final class DateUtil {

    protected static Logger logger = LoggerFactory.getLogger(DateUtil.class);
    private static final SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat formatYYYY = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat formatLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat formatWithTime = new SimpleDateFormat("yyyyMMdd HH:mm");
    private static final SimpleDateFormat formatWithTime_one = new SimpleDateFormat("yyyyMMddHH:mm");
    private static final SimpleDateFormat formatWithTimeUTC = new SimpleDateFormat("yyyyMMddHHmm");
    private static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("UTC");
    public static final SimpleDateFormat formatShort = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat formatShortTime = new SimpleDateFormat("HH:mm");
    private static final SimpleDateFormat formatYYYYMMDDSSS = new SimpleDateFormat("yyMMddHHmmssSSS");
    public static final SimpleDateFormat formatYYYYMMDDmm = new SimpleDateFormat("yyyyMMddHHmm");
    private static final SimpleDateFormat formatYYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat formatZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final SimpleDateFormat formatZoneTimeZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final SimpleDateFormat formatToSecond = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static final String SEPERATED_DASH = "-";

    public static Map<String, String> mapMonth = new HashMap<>();

    static {
        //宽松模式
        formatYYYYMMDDSSS.setLenient(false);
        formatYYYY.setLenient(false);
        formatYYYYMMDDHHMMSS.setLenient(false);
        formatYYYYMMDD.setLenient(false);
        formatWithTime.setLenient(false);
        formatWithTime_one.setLenient(false);
        formatTime.setLenient(false);
        formatShort.setLenient(false);
        formatLong.setLenient(false);
        formatWithTimeUTC.setTimeZone(GMT_TIMEZONE);
        mapMonth.put("01", "JAN");
        mapMonth.put("02", "FEB");
        mapMonth.put("03", "MAR");
        mapMonth.put("04", "APR");
        mapMonth.put("05", "MAY");
        mapMonth.put("06", "JUN");
        mapMonth.put("07", "JUL");
        mapMonth.put("08", "AUG");
        mapMonth.put("09", "SEP");
        mapMonth.put("10", "OCT");
        mapMonth.put("11", "NOV");
        mapMonth.put("12", "DEC");
    }

    private DateUtil() {
    }

    public static StringBuffer monthMap(Long date) {
        String dateStr = formatYYYYMMDD.format(new Date(date));
        String[] dates = dateStr.split("-");
        StringBuffer sb = new StringBuffer(dates[2]);
        sb.append(mapMonth.get(dates[1]));
        return sb;
    }

    public static String getYear(Long date) {
        String dateStr = formatYYYYMMDD.format(new Date(date));
        String[] dates = dateStr.split("-");
        String year = dates[0].substring(2, 4);
        return year;
    }

    public static String getCurrentDate(){
        return formatShort.format(new Date());
    }
    public static String getCurrentDate(String format){
        return getSpecificFormatTime(new Date(),format);
    }

    public static String getTomorrowCurrentDate(){
        Date date = addDay(new Date(), 1);
        return formatShort.format(date);
    }

    public static String getFullYear(Long date){
        String dateStr = formatYYYYMMDD.format(new Date(date));
        String[] dates = dateStr.split("-");
        String year = dates[0].substring(0,4);
        return year;
    }
    public static String formatCurTime() {
        synchronized (formatYYYYMMDDSSS) {
            return formatYYYYMMDDSSS.format(Calendar.getInstance().getTime());
        }
    }

    public static String formatCurTimeLong() {
        synchronized (formatYYYYMMDDHHMMSS) {
            return formatYYYYMMDDHHMMSS.format(Calendar.getInstance().getTime());
        }
    }

    public static String formatYYYYMMDDHHMMSS(Date date) {
        synchronized (formatYYYYMMDDHHMMSS) {
            return formatYYYYMMDDHHMMSS.format(date);
        }
    }

    public static String formatYYYY(Date date) {
        synchronized (formatYYYY) {
            return formatYYYY.format(date);
        }
    }

    public static String formatYYYYMMDDWithZone(Date date, TimeZone timeZone) {
        synchronized (formatZoneTimeZone) {
            formatZoneTimeZone.setTimeZone(timeZone);
            return formatZoneTimeZone.format(date);
        }
    }


    public static Date convertYYYYMMDD(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0)
            return null;

        Date date = null;
        try {
            synchronized (formatYYYYMMDD) {
                date = formatYYYYMMDD.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertYYYYMMDD error: date=" + strDate, e);
            return null;
        }

        return date;
    }

    public static Date convertYYYY(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0)
            return null;

        Date date = null;
        try {
            synchronized (formatYYYY) {
                date = formatYYYY.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertYYYY error: date=" + strDate, e);
            return null;
        }

        return date;
    }

    //formatShortTime
    public static Date convertShortTime(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0)
            return null;

        Date date = null;
        try {
            synchronized (formatShortTime) {
                date = formatShortTime.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertShortTime error: date=" + strDate, e);
            return null;
        }

        return date;
    }


    /**
     * 将字符串转换为带时区的时间.
     *
     * @param strDate
     * @return Date
     */
    public static Date convertZone(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0)
            return null;

        Date date = null;
        try {
            synchronized (formatZone) {
                date = formatZone.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("formatZone error: date=" + strDate, e);
            return null;
        }

        return date;
    }


    /**
     * 将字符串转换为带时区的时间.
     *
     * @param strDate
     * @return Date
     */
    public static Date convertToSecondWithT(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0)
            return null;

        Date date = null;
        try {
            synchronized (formatToSecond) {
                date = formatToSecond.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("formatToSecond error: date=" + strDate, e);
            return null;
        }

        return date;
    }


    public static Date convertWithTimeForUTCTimeZone(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0) {
            logger.error("convertWithTime error: date=" + strDate);
            return null;
        }

        Date date = null;
        try {
            synchronized (formatWithTimeUTC) {
                date = formatWithTimeUTC.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertWithTime error: date=" + strDate, e);
            return null;
        }

        return date;
    }


    public static String formateWithTimeForUTCTimeZone(Date date) {
        synchronized (formatWithTimeUTC) {
            return formatWithTimeUTC.format(date);
        }
    }


    public static Date convertWithTime(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0) {
            logger.error("convertWithTime error: date=" + strDate);
            return null;
        }

        Date date = null;
        try {
            synchronized (formatWithTime) {
                date = formatWithTime.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertWithTime error: date=" + strDate, e);
            return null;
        }

        return date;
    }

    public static Date convertWithTime_one(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0) {
            logger.error("convertWithTime_one error: date=" + strDate);
            return null;
        }

        Date date = null;
        try {
            synchronized (formatWithTime_one) {
                date = formatWithTime_one.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertWithTime_one error: date=" + strDate, e);
            return null;
        }

        return date;
    }

    public static Date convertShort(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0)
            return null;

        Date date = null;
        try {
            synchronized (formatShort) {
                date = formatShort.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertShort error: date=" + strDate, e);
            return null;
        }

        return date;
    }


    public static Date convert(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0)
            return null;

        Date date = null;
        try {
            synchronized (format) {
                date = format.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertLong error: date=" + strDate, e);
            return null;
        }

        return date;
    }

    public static String formatYYYYMMDD(Date date) {
        synchronized (formatYYYYMMDD) {
            return formatYYYYMMDD.format(date);
        }
    }

    public static String formatTime(Date date) {
        synchronized (formatTime) {
            return formatTime.format(date);
        }
    }

    public static String formatShortTime(Date date) {
        synchronized (formatShortTime) {
            return formatShortTime.format(date);
        }
    }

    public static String formatShort(Date date) {
        synchronized (formatShort) {
            return formatShort.format(date);
        }
    }

    public static Date convertFormatLong(String strDate) {
        Date date = null;
        try {
            synchronized (formatLong) {
                date = formatLong.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertLong error: date=" + strDate, e);
            return null;
        }

        return date;
    }

    public static String formatLong(Date date) {
        synchronized (formatLong) {
            return formatLong.format(date);
        }
    }

    public static String formatScalLong(Date date) {
        String longDate = formatLong(date);
        return longDate.replace('-', '/');
    }

    public static String format(Date date) {
        synchronized (format) {
            return format.format(date);
        }
    }

    public static String formatYYYYMMDDmm(Date date) {
        synchronized (formatYYYYMMDDmm) {
            return formatYYYYMMDDmm.format(date);
        }
    }
    public static Date convertYYYYMMDDmm(String strDate) {
        Date date = null;
        try {
            synchronized (formatYYYYMMDDmm) {
                date = formatYYYYMMDDmm.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertLong error: date=" + strDate, e);
            return null;
        }
        return date;
    }

    public static Date convertWithTimeWithOutUTCTimeZone(String strDate) {
        if (strDate == null || strDate.indexOf("null") >= 0) {
            logger.error("convertWithTime error: date="+strDate);
            return null;
        }

        Date date = null;
        try {
            synchronized (formatWithTimeUTC) {
                date = formatWithTimeUTC.parse(strDate);
            }
        } catch (Exception e) {
            logger.error("convertWithTime error: date="+strDate, e);
            return null;
        }

        return date;
    }

    public static String formatDDMMMYY(Date date) {
        return String.format(Locale.US, "%1$td%1$tb%1$ty", date);
    }

    public static String convertYYYYMMDDToDDMMMYY(String date) {
        Date d = convertYYYYMMDD(date);
        return null == d ? "" : formatDDMMMYY(d);
    }

    public static String formatTodyDate(String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(new Date());
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static Date parse(String date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            logger.error("pase error: date=" + date + ", pattern=" + pattern, e);
            return null;
        }
    }

    /**
     * 取得两个日期的时间间隔,相差的天数
     *
     * @param d1
     * @param d2
     * @return
     */
    public static int getDayBetween(Date d1, Date d2) {
        Calendar before = Calendar.getInstance();
        Calendar after = Calendar.getInstance();
        if (d1.before(d2)) {
            before.setTime(d1);
            after.setTime(d2);
        } else {
            before.setTime(d2);
            after.setTime(d1);
        }
        int days = 0;

        int startDay = before.get(Calendar.DAY_OF_YEAR);
        int endDay = after.get(Calendar.DAY_OF_YEAR);

        int startYear = before.get(Calendar.YEAR);
        int endYear = after.get(Calendar.YEAR);
        before.clear();
        before.set(startYear, 0, 1);

        while (startYear != endYear) {
            before.set(startYear++, Calendar.DECEMBER, 31);
            days += before.get(Calendar.DAY_OF_YEAR);
        }
        return days + endDay - startDay;
    }

    /**
     * 获取两个日期之间相差的天数，
     * @param beginTime
     * @param endTime
     * @return
     */
    public static int getDayBetween(String beginTime,String endTime){
        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        begin.setTime(convertYYYYMMDDmm(beginTime));
        end.setTime(convertYYYYMMDDmm(endTime));

        double days = (double)(end.getTimeInMillis() - begin.getTimeInMillis())/(1000 * 60 * 60 * 24);
        return (int)Math.ceil(days);
    }

    /**
     * d1 - d2的小时数. d1 > d2.
     *
     * @param d1
     * @param d2
     * @return d1 - d2的小时数.
     */
    public static int getHourBetween(Date d1, Date d2) {
        int days = getDayBetween(d1, d2);
        Calendar before = Calendar.getInstance();
        Calendar after = Calendar.getInstance();
        before.setTime(d1);
        after.setTime(d2);
        int d1Hour = before.get(Calendar.HOUR_OF_DAY);
        int d2Hour = after.get(Calendar.HOUR_OF_DAY);
        int distance = d1Hour - d2Hour;
//        if (distance < 0) {
//            distance = 24 * (days - 1) + distance;
//        } else {
//            distance = 24 * days + distance;
//        }
        distance = 24 * days + distance;
        return distance;
    }

    /**
     * 获取固定日期与当前日期之差
     * @param date
     * @return
     */
    public static int reduceDays(String date){
        Date stayDate = convertShort(date);
        return getDayBetween(new Date(),stayDate);
    }

    //传入日期获得小时
    public static String getHour(Date date) {
        Calendar before = Calendar.getInstance();
        before.setTime(date);
        int hour = before.get(Calendar.HOUR_OF_DAY);
        if (hour < 10) {
            return "0" + hour;
        }
        return hour + "";
    }

    public static int getHourInt(Date date) {
        Calendar before = Calendar.getInstance();
        before.setTime(date);
        int hour = before.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    /**
     * 取得时间间隔,相差的时间，XX小时XX分钟
     *
     * @param time1
     * @param time2
     * @return 不会超过24小时
     */
    public static String getTimeBetween(String time1, String time2) {
        String[] t1 = time1.split(":");
        String[] t2 = time2.split(":");

        int minute = Integer.parseInt(t2[1]) - Integer.parseInt(t1[1]);
        int hour = Integer.parseInt(t2[0]) - Integer.parseInt(t1[0]);

        if (minute < 0) {
            minute += 60;
            hour -= 1;
        }
        if (hour < 0) {
            hour += 24;
        }

        if (hour == 0) {
            return minute + "分钟";
        }
        if (minute == 0) {
            return hour + "小时";
        }
        return hour + "小时" + minute + "分钟";
    }


    public static Date addHourForBigDecimal(Date myDate, BigDecimal amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        BigDecimal multiply = amount.multiply(new BigDecimal(3600000));
        int i = multiply.intValue();
        cal.add(Calendar.MILLISECOND, i);
        return cal.getTime();
    }
    public static Date addHour(Date myDate, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.HOUR_OF_DAY, amount);
        return cal.getTime();
    }

    public static Date addDay(Date myDate, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DAY_OF_MONTH, amount);
        return cal.getTime();
    }

    public static Date addYear(Date myDate, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.DAY_OF_YEAR, amount);
        return cal.getTime();
    }

    public static Date addMinute(Date myDate, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.MINUTE, amount);
        return cal.getTime();
    }

    public static Date addSecond(Date myDate, int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(myDate);
        cal.add(Calendar.SECOND, amount);
        return cal.getTime();
    }

    public static String dateFormatStr(String dateByyyyyMMddStr) {
        if (dateByyyyyMMddStr != null && dateByyyyyMMddStr.length() == 8) {
            String year = dateByyyyyMMddStr.substring(0, 4);
            String month = dateByyyyyMMddStr.substring(4, 6);
            String day = dateByyyyyMMddStr.substring(6, 8);
            return year + "-" + month + "-" + day;
        } else {
            return "";
        }
    }

    public static String long2DateStr(long msec, String pattern) {
        Date date = new Date();
        date.setTime(msec);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 格式化日期.YYYY-MM-DD.
     *
     * @param date YYYYMMDD
     * @return YYYY-MM-DD
     */
    public static String formatYYYYMMDD(String date) {
        if (StringUtils.isNotBlank(date) && date.length() == 8) {
            StringBuilder bir = new StringBuilder(10);
            bir.append(date.substring(0, 4)).append(SEPERATED_DASH).append(date.substring(4, 6)).append(SEPERATED_DASH)
                    .append(date.substring(6, 8));
            return bir.toString();
        }
        return "";
    }

    /**
     * 格式化日期
     *
     * @param date      YYYYMMDD, 例如：20090101,
     * @param separator 分隔符, 例如：-
     * @return YYYY-MM-DD
     */
    public static String formatYYYYMMDD(String date, String separator) {
        if (StringUtils.isNotBlank(date) && date.length() == 8) {
            return date.substring(0, 4) + separator + date.substring(4, 6) + separator + date.substring(6, 8);
        }
        return "";
    }

    /**
     * 格式化日期
     *
     * @param date      yyyy-MM-dd, 例如：2009-01-01,
     * @param separator 分隔符, 例如：空 /
     * @return 20090101 2009/01/01
     */
    public static String formatFromYYYYMMDD(String date, String separator) {
        if (StringUtils.isNotBlank(date) && date.length() == 10) {
            return date.substring(0, 4) + separator + date.substring(5, 7) + separator + date.substring(8);
        }
        return "";
    }

    /**
     * 根据出发时间(HH:mm)、到达时间(HH:mm)获取到达时间(yyyy-MM-dd HH:mm).
     *
     * @param date    yyyyMMdd
     * @param depTime 出发时间(HH:mm)
     * @param arrTime 到达时间(HH:mm)
     * @return yyyy-MM-dd HH:mm
     */
    public static Date transArrDateByDepArrTime(String date, String depTime, String arrTime) {
        Date depDate = DateUtil.convertWithTime(date + " " + depTime);
        Date arrDate = DateUtil.convertWithTime(date + " " + arrTime);
        if (depDate.after(arrDate)) {
            arrDate = DateUtil.addDay(arrDate, 1);
        }
        return arrDate;
    }

    //获得当天指定时刻的long值
    public static Long getPreciseTimeLong(int hour, int minute, int second) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, hour);
        todayStart.set(Calendar.MINUTE, minute);
        todayStart.set(Calendar.SECOND, second);
        todayStart.set(Calendar.MILLISECOND, 0);
        Long df = todayStart.getTime().getTime();
        return df;
    }

    /**
     * date 返回指定时间隔几个月的时间
     * next 为正表示下几个月。为负表示上几个月
     * next= -1表示上一个月的这个时刻
     */
    public static Date parseNextMonthTime(Date date, int next) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, next);
        return cal.getTime();
    }

    /**
     * 获得当前月的几号
     * 传入的参数必须两位 03、04、12
     * 例如传入15.返回当月15号的日期
     */

    public static Date thisMonthAppointDate(String date) {
        try {
            Calendar localTime = Calendar.getInstance();
            int year = localTime.get(Calendar.YEAR);
            String monthStr = "";
            int month = localTime.get(Calendar.MONTH) + 1;
            if (month < 10) {
                monthStr = "0" + month;
            } else {
                monthStr = "" + month;
            }
            StringBuffer sb = new StringBuffer("" + year);
            sb.append(monthStr).append(date);
            Date d = DateUtil.convertShort(sb.toString());
            return d;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 传入开始和结束时间，返回两个时间之间天的集合， 例如 [2013-04-01, 2013-04-02, 2013-04-03]
     *
     * @param start
     * @param end
     * @return
     */
    public static List<String> getDaysList(String start, String end) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.parse(start));
        int dayBetween = DateUtil.daysBetween(DateUtil.parse(start), DateUtil.parse(end));
        List<String> list = new ArrayList<String>(30);
        for (int i = 0; i <= dayBetween; i++) {
            list.add(DateUtil.getSpecificFormatTime(calendar.getTime(), "yyyy-MM-dd"));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return list;
    }

    public static Date parse(String dateString) {
        try {
            if (dateString == null || dateString.length() == 0) {
                return null;
            } else if (dateString.length() <= 6) {
                return new SimpleDateFormat("yyyyMM").parse(dateString);
            } else if (dateString.indexOf("-") < 0) {
                return new SimpleDateFormat("yyyyMMdd").parse(dateString);
            } else if (dateString.length() == 7 && dateString.indexOf("-") > 0) {
                return new SimpleDateFormat("yyyy-MM").parse(dateString);

            } else {
                return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 格式化日期
     * @param date
     * @return
     */
    public static String formatFromYYYYMMDDHHMM(String date,String sepDate,String sepTime){
        if (StringUtils.isNotBlank(date) && date.length()==12){
            return date.substring(0,4) + sepDate +date.substring(4,6) +sepDate +date.substring(6,8) + " " +date.substring(8,10) + sepTime +date.substring(10,12);
        }
        return date;
    }

    /** 求两个日期之间的相隔天数 */
    public static int daysBetween(Date data1, Date data2) {
        Calendar cNow = Calendar.getInstance();
        Calendar cReturnDate = Calendar.getInstance();
        cNow.getTimeInMillis();
        if (data1.after(data2)) {
            cNow.setTime(data1);
            cReturnDate.setTime(data2);
        } else {
            cNow.setTime(data2);
            cReturnDate.setTime(data1);
        }

        setTimeToMidnight(cNow);
        setTimeToMidnight(cReturnDate);
        long todayMs = cNow.getTimeInMillis();
        long returnMs = cReturnDate.getTimeInMillis();
        long intervalMs = todayMs - returnMs;
        return millisecondsToDays(intervalMs);
    }

    private static int millisecondsToDays(long intervalMs) {
        return (int) (intervalMs / (1000 * 86400));
    }

    private static void setTimeToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }

    /**
     * 返回指定格式的时间
     */
    public static String getSpecificFormatTime(Date date, String format) {
        return FastDateFormat.getInstance(format).format(date);
    }

    public static long getUTCTimeformatTime(String date) throws Exception {
        SimpleDateFormat utcFormater = new SimpleDateFormat("yyyyMMdd");
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(date);
        } catch (ParseException e) {
            logger.error("parse date error", e);
        }
        if (gpsUTCDate != null) {
            return gpsUTCDate.getTime() / 1000;
        }
        throw new Exception("parse date error");

    }



   public static int getBetweenHour(Date start,Date end){
       long time = end.getTime()-start.getTime();
       long hour=time/(1000*60*60);
       return (int) hour;
    }


    /**
     *
     * @param number
     * @return 返回n分钟之前
     */
    public static Date getCurrentTime(int number){
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, -number);// n分钟之前的时间
        Date beforeD = beforeTime.getTime();
        return beforeD;
    }

    /**
     *
     * @param date
     * @return  返回某天零点时刻
     */
    public static Date getStartTimeOfDay(Date date){
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.HOUR_OF_DAY,0);
        day.set(Calendar.MINUTE,0);
        day.set(Calendar.SECOND,0);
        day.set(Calendar.MILLISECOND,0);

        return day.getTime();
    }

    /**
     *
     * @param date
     * @return  返回某天末点时刻
     */
    public static Date getEndTimeOfDay(Date date){
        Calendar day = Calendar.getInstance();
        day.setTime(date);
        day.set(Calendar.HOUR_OF_DAY,23);
        day.set(Calendar.MINUTE,59);
        day.set(Calendar.SECOND,59);
        day.set(Calendar.MILLISECOND,999);
        return day.getTime();
    }

    /**
     *
     * @param format
     * @param lon
     * @return 时间戳转展示字符
     */
    public static  String getFormatForLong(SimpleDateFormat format,Long lon){
        Date date =new Date(lon);
        String res = format.format(date);
        return res;
    }



    /**
     *
     * @param startMonth 开始月
     * @param endMonth 结束月
     * @return  返回中间相差的月份集合
     */
    public static Set<String> getMonthBetween(String startMonth, String endMonth)  {
        Set<String> result = new HashSet<>();
        try {


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
            SimpleDateFormat mm = new SimpleDateFormat("M");//格式化为年月

            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();

            start.setTime(sdf.parse(startMonth));
            start.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), 1);

            end.setTime(sdf.parse(endMonth));
            end.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), 2);

            Calendar curr = start;
            while (curr.before(end)) {
                result.add(mm.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
            }


        } catch (ParseException e) {
            logger.error("pase error: startMonth=" + startMonth + ", endMonth=" + endMonth, e);
            return null;
        }
        return result;
    }



    /**
     * 获取某年某月第某个星期几的日期 的0点0分0秒
     * @param year   2017对应2017
     * @param month   12对应12月
     * @param number 第几个 ,区间正负5 ;0的时候 day传是多少号 例如 number:0 day:28 表明当月的28号
     * @param day   周几; 1对应周一  7对应周日
     * @return
     */
    public static Date getNumberDayOfWeekForYearMonth(int year, int month,int number,int day) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DATE, number);
        cal.set(Calendar.HOUR_OF_DAY, 2);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if(number==0){
            return cal.getTime();
        }
        if(day==7){
            day=0;
        }
        cal.set(Calendar.DATE, 1); // 设为第一天
        List<Date> list=new ArrayList<>();
        Calendar get_cal;
        while (cal.get(Calendar.DAY_OF_MONTH) != cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        {
            if(cal.get(Calendar.DAY_OF_WEEK)==day+1){
                get_cal = Calendar.getInstance();
                get_cal.set(Calendar.YEAR, year);
                get_cal.set(Calendar.MONTH, month - 1);
                get_cal.set(Calendar.DATE, cal.get(Calendar.DAY_OF_MONTH));
                get_cal.set(Calendar.HOUR_OF_DAY, 2);
                get_cal.set(Calendar.MINUTE, 0);
                get_cal.set(Calendar.SECOND, 0);
                get_cal.set(Calendar.MILLISECOND, 0);
                list.add(get_cal.getTime());
            }
            cal.add(Calendar.DATE, 1);
        }
        if(cal.get(Calendar.DAY_OF_WEEK)==day+1){
            get_cal = Calendar.getInstance();
            get_cal.set(Calendar.YEAR, year);
            get_cal.set(Calendar.MONTH, month - 1);
            get_cal.set(Calendar.DATE, cal.get(Calendar.DAY_OF_MONTH));
            get_cal.set(Calendar.HOUR_OF_DAY, 2);
            get_cal.set(Calendar.MINUTE, 0);
            get_cal.set(Calendar.SECOND, 0);
            get_cal.set(Calendar.MILLISECOND, 0);
            list.add(get_cal.getTime());
        }
        Date return_date=null;
        int size = list.size();
        if(number>0){
            return_date=list.get(number-1);
        }else{
            return_date=list.get(size+number);
        }
        return return_date;
    }

         //获取当天零点时刻
        public static Date getToDayZeroTime() {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        }



        public static int getDateYear(Date date){
            String yyyy = DateUtil.formatYYYY(date);
            Integer integer = Integer.valueOf(yyyy);
            return integer;
        }

    /**
     *
     * @param date1
     * @param date2
     *
     * @return date2 sub date1  区分正负
     */
        public static BigDecimal getSubDateInt(Date date1,Date date2){
            BigDecimal sub = new BigDecimal(date2.getTime()-date1.getTime());
            BigDecimal hour = new BigDecimal(3600000);
            BigDecimal divide = sub.divide(hour,2, RoundingMode.HALF_UP);
            return divide;
        }


    /** for test only
     * @throws ParseException */
    public static void main(String[] args) throws ParseException {
        /*Date toDayZeroTime = DateUtil.getNumberDayOfWeekForYearMonth(2018,9,-1,6);
        System.out.println(toDayZeroTime.getTime());*/
        System.out.println(formateWithTimeForUTCTimeZone(new Date(1518503700000L)));
    }

}






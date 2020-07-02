package cn.cstn.algorithm.commons.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

/**
 * DateTimeUtil
 *
 * @author zhaohuiqiang
 * @date 2020/7/1 15:00
 */
@Slf4j
@SuppressWarnings("unused")
public class DateTimeUtil {

    public static Date[][] getDateRange(Date[] nowRange, Date[] curRange, Date[] lastRange, Function<Date, Date[]> f) {
        if (DateUtils.isSameDay(nowRange[0], curRange[0])) {
            Date[] lastLastRange = f.apply(lastRange[0]);
            return new Date[][]{lastRange, lastLastRange};
        }

        return new Date[][]{curRange, lastRange};
    }

    public static Date[] getLastLastQuarterRange() {
        return getLastLastQuarterRange(new Date());
    }

    public static Date[] getLastLastQuarterRange(Date date) {
        return getLastQuarterRange(getLastQuarterRange(date)[0]);
    }

    public static Date[] getLastQuarterRange() {
        return getLastQuarterRange(new Date());
    }

    public static Date[] getLastQuarterRange(Date date) {
        return getQuarterRange(date, -1);
    }

    public static Date[] getCurQuarterRange() {
        return getCurQuarterRange(new Date());
    }

    public static Date[] getCurQuarterRange(Date date) {
        return getQuarterRange(date, 1);
    }

    public static Date[] getQuarterRange(Date date, int amount) {
        Calendar calendar = getCalendarFromStart(date);
        int curMonth = calendar.get(Calendar.MONTH);
        if (curMonth < 3)
            calendar.set(Calendar.MONTH, 0);
        else if (curMonth < 6)
            calendar.set(Calendar.MONTH, 3);
        else if (curMonth < 9)
            calendar.set(Calendar.MONTH, 6);
        else if (curMonth < 12)
            calendar.set(Calendar.MONTH, 9);
        calendar.set(Calendar.DATE, 1);

        Date cur = calendar.getTime();
        calendar.add(Calendar.MONTH, amount * 3);
        if (amount < 0) return new Date[]{calendar.getTime(), cur};

        return new Date[]{cur, calendar.getTime()};
    }

    public static Date[] getLastLastMonthRange() {
        return getLastLastMonthRange(new Date());
    }

    public static Date[] getLastLastMonthRange(Date date) {
        return getLastMonthRange(getLastMonthRange(date)[0]);
    }

    public static Date[] getLastMonthRange() {
        return getLastMonthRange(new Date());
    }

    public static Date[] getLastMonthRange(Date date) {
        return getMonthRange(date, -1);
    }

    public static Date[] getCurMonthRange() {
        return getCurMonthRange(new Date());
    }

    public static Date[] getCurMonthRange(Date date) {
        return getMonthRange(date, 1);
    }

    public static Date[] getMonthRange(Date date, int amount) {
        Calendar calendar = getCalendarFromStart(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date cur = calendar.getTime();
        calendar.add(Calendar.MONTH, amount);
        if (amount < 0) return new Date[]{calendar.getTime(), cur};

        return new Date[]{cur, calendar.getTime()};
    }

    public static Date[] getLastLastWeekRange() {
        return getLastLastWeekRange(new Date());
    }

    public static Date[] getLastLastWeekRange(Date date) {
        return getLastWeekRange(getLastWeekRange(date)[0]);
    }

    public static Date[] getLastWeekRange() {
        return getLastWeekRange(new Date());
    }

    public static Date[] getLastWeekRange(Date date) {
        return getWeekRange(date, -1);
    }

    public static Date[] getCurWeekRange() {
        return getCurWeekRange(new Date());
    }

    public static Date[] getCurWeekRange(Date date) {
        return getWeekRange(date, 1);
    }

    public static Date[] getWeekRange(Date date, int amount) {
        Calendar calendar = getCalendarFromStart(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date cur = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_MONTH, amount);

        if (amount < 0) return new Date[]{calendar.getTime(), cur};

        return new Date[]{cur, calendar.getTime()};
    }

    public static Date[] getLastLastDayRange() {
        return getLastLastDayRange(new Date());
    }

    public static Date[] getLastLastDayRange(Date date) {
        return getLastDayRange(getLastDayRange(date)[0]);
    }

    public static Date[] getLastDayRange() {
        return getLastDayRange(new Date());
    }

    public static Date[] getLastDayRange(Date date) {
        return getDayRange(date, -1);
    }

    public static Date[] getCurDayRange() {
        return getCurDayRange(new Date());
    }

    public static Date[] getCurDayRange(Date date) {
        return getDayRange(date, 1);
    }

    public static Date[] getDayRange(Date date, int amount) {
        Calendar calendar = getCalendarFromStart(date);
        Date cur = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, amount);

        if (amount < 0) return new Date[]{calendar.getTime(), cur};

        return new Date[]{cur, calendar.getTime()};
    }

    public static Calendar getCalendarFromStart() {
        return getCalendarFromStart(null);
    }

    public static Calendar getCalendarFromStart(Date date) {
        Calendar calendar = getCalendar(date);

        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        return calendar;
    }

    public static Calendar getCalendar() {
        return getCalendar(null);
    }

    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) calendar.setTime(date);
        else calendar.setTime(new Date());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar;
    }

}

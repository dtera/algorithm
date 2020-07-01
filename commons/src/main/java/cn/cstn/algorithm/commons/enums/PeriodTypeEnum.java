package cn.cstn.algorithm.commons.enums;

import cn.cstn.algorithm.commons.util.DateTimeUtil;
import com.alibaba.fastjson.annotation.JSONType;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

/**
 * PeriodTypeEnum
 *
 * @author zhaohuiqiang
 * @date 2020/7/1 15:28
 */
@JSONType(serializeEnumAsJavaBean = true)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@RequiredArgsConstructor
@Getter
@SuppressWarnings("unused")
public enum PeriodTypeEnum {

    DAY("D", "日", 30) {
        @Override
        public Date[][] getLastAndLastLastDateRange(Date date) {
            return new Date[][]{DateTimeUtil.getLastDayRange(date), DateTimeUtil.getLastLastDayRange(date)};
        }

        @Override
        public Date[] getCurDateRange(Date date) {
            return DateTimeUtil.getCurDayRange(date);
        }

        @Override
        public Date[] getLatestDateRange(Date date) {
            return DateTimeUtil.getMonthRange(date, -getAmount());
        }

    },
    WEEK("W", "周", 12) {
        @Override
        public Date[][] getLastAndLastLastDateRange(Date date) {
            return new Date[][]{DateTimeUtil.getLastWeekRange(date), DateTimeUtil.getLastLastWeekRange(date)};
        }

        @Override
        public Date[] getCurDateRange(Date date) {
            return DateTimeUtil.getCurWeekRange(date);
        }

        @Override
        public Date[] getLatestDateRange(Date date) {
            return DateTimeUtil.getWeekRange(date, -getAmount());
        }

    },
    MONTH("M", "月", 12) {
        @Override
        public Date[][] getLastAndLastLastDateRange(Date date) {
            return new Date[][]{DateTimeUtil.getLastMonthRange(date), DateTimeUtil.getLastLastMonthRange(date)};
        }

        @Override
        public Date[] getCurDateRange(Date date) {
            return DateTimeUtil.getCurMonthRange(date);
        }

        @Override
        public Date[] getLatestDateRange(Date date) {
            return DateTimeUtil.getMonthRange(date, -getAmount());
        }

    },
    QUARTER("Q", "季度", 4) {
        @Override
        public Date[][] getLastAndLastLastDateRange(Date date) {
            return new Date[][]{DateTimeUtil.getLastQuarterRange(date), DateTimeUtil.getLastLastQuarterRange(date)};
        }

        @Override
        public Date[] getCurDateRange(Date date) {
            return DateTimeUtil.getCurQuarterRange(date);
        }

        @Override
        public Date[] getLatestDateRange(Date date) {
            return DateTimeUtil.getQuarterRange(date, -getAmount());
        }

    };

    private final String type;
    private final String desc;
    private final int amount;

    public abstract Date[][] getLastAndLastLastDateRange(Date date);

    public abstract Date[] getCurDateRange(Date date);

    public abstract Date[] getLatestDateRange(Date date);

    public Date getNowStartDate() {
        return getCurDateRange(new Date())[0];
    }

    public boolean isInNowRange(Date date) {
        return getCurDateRange(new Date())[0].equals(getCurDateRange(date)[0]);
    }

    public int getIndex() {
        return this.ordinal();
    }

    public static PeriodTypeEnum of(Integer index) {
        for (PeriodTypeEnum item : PeriodTypeEnum.values()) {
            if (item.getIndex() == index) {
                return item;
            }
        }
        return null;
    }

    public static PeriodTypeEnum of(String type) {
        for (PeriodTypeEnum item : PeriodTypeEnum.values()) {
            if (item.getType().equalsIgnoreCase(type)) {
                return item;
            }
        }
        return DAY;
    }

    public static boolean contains(String code) {
        for (PeriodTypeEnum item : PeriodTypeEnum.values()) {
            if (item.getType().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "{'index': " + getIndex() + ", 'code': '" + type + ", 'desc': '" + desc + "'}";
    }

}

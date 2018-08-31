package cn.cstn.algorithm.commons.util;

import java.lang.reflect.Field;

/**
 * description :        class description
 * @author :            zhaohq
 * date :               2018/8/31 0031 21:43
 */
public class BeanUtil {

    public static void setFieldValue(Object o, String name, Object value) {
        try {
            Field f = o.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(o, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

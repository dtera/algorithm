package cn.cstn.algorithm.commons.util;

import java.lang.reflect.Field;

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

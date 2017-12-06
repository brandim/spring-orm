package indi.gavin.orm;

import java.lang.reflect.Method;
import java.util.UUID;

import org.springframework.beans.BeanUtils;


public final class BizUtils implements BizStatus {
    
    private BizUtils() {
    }

    /**
     * 生成随机、唯一的uuid，以字符串的形式返回
     */
    public static String uuid(String domain) {
        if (domain == null) {
            return UUID.randomUUID().toString();
        } else {
            return domain + "-" + UUID.randomUUID().toString();
        }
    }

    /**
     * 设置对象的指定属性的值
     * 
     */
    public static void setProperty(Object obj, String propertyName, Object value) {
        try {
            Method method = BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName).getWriteMethod();
            method.invoke(obj, value);
        } catch (Exception e) {
            throw new BizException(S_COMMON_SET_POPERTY_FAILED_2, propertyName, value);
        }
    }

    /**
     * 取得对象的指定属性的值
      */
    @SuppressWarnings("unchecked")
    public static <T> T getProperty(Object obj, String propertyName, Class<T> valueClass) {
        T r = null;
        try {
            Method method = BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName).getReadMethod();
            r = (T) method.invoke(obj);
        } catch (Exception e) {
            throw new BizException(S_COMMON_GET_POPERTY_FAILED_1, propertyName);
        }

        return r;
    }

    /**
     * 取得对象的指定属性的值
      */
    public static Object getProperty(Object obj, String propertyName) {
        try {
            Method method = BeanUtils.getPropertyDescriptor(obj.getClass(), propertyName).getReadMethod();
            Object r = method.invoke(obj);
            return r;
        } catch (Exception e) {
            throw new BizException(S_COMMON_GET_POPERTY_FAILED_1, propertyName);
        }
    }


}

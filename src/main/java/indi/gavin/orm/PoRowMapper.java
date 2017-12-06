package indi.gavin.orm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

/**
 * 通过类反射机制将jdbcTemplate查询结果转换为数据对象(PO)。
 * 目前支持的数据类型：int、string、date、double、float。
 * 
 * @author Gavin
 *
 * @param <T>
 */
public final class PoRowMapper<T> implements RowMapper<T> {

    private static Logger logger =  LoggerFactory.getLogger(PoRowMapper.class);

    /**
     * 实例缓存
     */
    private static Map<Class<?>, RowMapper<?>> instances = new HashMap<Class<?>, RowMapper<?>>();

    @SuppressWarnings("unchecked")
    public static <W> RowMapper<W> instance(Class<W> clazz) {
        RowMapper<W> m = (RowMapper<W>) instances.get(clazz);
        if (m == null) {
            m = new PoRowMapper<W>(clazz);
            instances.put(clazz, m);
        }
        return m;
    }

    private Class<T> instClass;
    private Map<String, Method> setters = new HashMap<String, Method>();

    private PoRowMapper(Class<T> clazz) {
        if (clazz == null)
            throw new NullPointerException("Null Class to instantiate " + getClass().getName());

        instClass = clazz;
    }

    @Override
    public T mapRow(ResultSet rs, int index) throws SQLException {
        try {
            T obj = instClass.newInstance();
            Object invokeObj = null;
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            String columnName;
            String fieldName;
            Method setter;
            Class<?> paramClass;

            for (int i = 1; i <= columnCount; i++) {
                //首先，取得设置对象属性的;setter方法
                columnName = md.getColumnName(i);
                String[] alias = columnName.split("\\$");
                if (alias.length > 1) {
                    //嵌套对象
                    fieldName = PoUtils.columnName2FieldName(alias[0]);
                    invokeObj = BizUtils.getProperty(obj, fieldName);
                    if (null == invokeObj) {
                        logger.error(" null instance for embeded property: {}, class is:  {}", fieldName, obj
                                .getClass().getName());
                        continue;
                    }
                    setter = getSetterOfColumnName(invokeObj.getClass(), fieldName, alias[1]);
                } else {
                    //非嵌套对象
                    setter = getSetterOfColumnName(instClass, "", columnName);
                    invokeObj = obj;
                }
                if (setter == null) {
                    logger.error("Can't find setter for column: {}", columnName);
                    continue;
                }

                //然后，根据setter方法参数类型，从rs中取得相应值，并设置到对象中
                paramClass = setter.getParameterTypes()[0];
                try {
                    if (String.class.equals(paramClass)) {
                        //字符串
                        String sValue = rs.getString(i);
                        setter.invoke(invokeObj, sValue);
                    } else if (int.class.equals(paramClass)) {
                        //整数
                        Integer iValue = rs.getInt(i);
                        setter.invoke(invokeObj, iValue);
                    } else if (Integer.class.equals(paramClass)) {
                        //整数(Integer)                        
                        Integer iValue = rs.getInt(i);
                        setter.invoke(invokeObj, iValue);
                    } else if (Long.class.equals(paramClass)) {
                        //长整型 (Long)                    
                        Long lValue = rs.getLong(i);
                        setter.invoke(invokeObj, lValue);
                    } else if (long.class.equals(paramClass)) {
                        //长整型                     
                        Long lValue = rs.getLong(i);
                        setter.invoke(invokeObj, lValue);
                    } else if (Date.class.equals(paramClass)) {
                        //日期
                        Date dValue = rs.getTimestamp(i);
                        setter.invoke(invokeObj, dValue);
                    } else if (Double.class.equals(paramClass)) {
                        //double
                        Double dValue = rs.getDouble(i);
                        setter.invoke(invokeObj, dValue);
                    } else if (Float.class.equals(paramClass)) {
                        //float
                        Float fValue = rs.getFloat(i);
                        setter.invoke(invokeObj, fValue);
                    } else {
                        logger.warn("Unsupported field type: {}", paramClass.getName());
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn(e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    logger.warn(e.getMessage(), e);
                }
            }

            return obj;
        } catch (InstantiationException e) {
            throw new SQLException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new SQLException(e.getMessage(), e);
        }
    }

    /**
     * 根据jdbc resultset中的字段名取得对应数据对象的setter方法名
     * 
     * @param dbColumnName resulset中的字段名
     * @return 与columnName对应的pojo属性的set方法
     */
    protected Method getSetterOfColumnName(Class<?> clazz, String prefix, String dbColumnName) {
        String fullKey = prefix + "_" + dbColumnName;
        if (setters.containsKey(fullKey)) {
            return setters.get(fullKey);
        }

        String fieldName = PoUtils.columnName2FieldName(dbColumnName);
        String setterName;
        Method setter = null;
        Field field;

        setterName = "set" + StringUtils.capitalize(fieldName);
        while ((clazz != null) && (!Object.class.equals(clazz))) {
            try {
                field = clazz.getDeclaredField(fieldName);
                setter = clazz.getDeclaredMethod(setterName, field.getType());
                break;
            } catch (Exception e) {
                Type t = clazz.getGenericSuperclass();
                if (t != null && t instanceof Class<?>) {
                    clazz = (Class<?>) t;
                }
            }
        }
        if (setter == null) {
            logger.warn("{} hasn't setter for column: {}.", instClass.getName(), dbColumnName);
        }

        setters.put(fullKey, setter);
        return setter;
    }

}

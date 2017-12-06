/**
 * <p>Copyright &copy; 2017 。</p>
 */
package indi.gavin.orm.helper;

import indi.gavin.orm.BizException;
import indi.gavin.orm.BizStatus;
import indi.gavin.orm.PoDefinition;
import indi.gavin.orm.PoUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <b>Application name:</b><br>
 * <b>Application describing:</b> <br>
 * <b>Copyright:</b>Copyright &copy; 2017<br>
 * <b>Date:</b><br>
 * @author gavin
 * @version $Revision: 1.0 $
 */
public class GenericDaoHelper {
    private static Logger logger = LoggerFactory.getLogger(GenericDaoHelper.class);

    protected JdbcTemplate db;
    protected Class<?> poClass;

    public GenericDaoHelper(JdbcTemplate db, Class<?> poClass) {
        Assert.notNull(db);
        Assert.notNull(poClass);

        PoDefinition poDef = PoDefinition.instance(poClass);
        if (poDef.getTableCount() != 1) {
            logger.error("试图同时操作多个表进行修改操作(insert/update/delete), poClass is: {}", poClass.getName());
            throw new BizException(BizStatus.S_BIZ_P_ERROR_UPDATE_MULTI_TABLE);
        }

        this.db = db;
        this.poClass = poClass;
    }

    /**
     * 创建一条新记录。
     * 
     * @param entity 实体对象
     * @return 受影响的记录行数（正常情况下应该为“1”）
     */
    public int create(Object entity) {
        Assert.notNull(entity);

        PoDefinition poDef = PoDefinition.instance(poClass);
        String tableName = poDef.getTableName(0);
        StringBuilder sbFields = new StringBuilder();
        StringBuilder sbValues = new StringBuilder();
        List<Object> listValues = new ArrayList<Object>();
        String getterName;
        Method getter;
        Object value;

        for (String fieldName : poDef.getFields()) {
            if (poDef.isFieldReadOnly(fieldName)) {
                continue;
            }
            getterName = "get" + StringUtils.capitalize(fieldName);
            value = null;
            try {
                getter = getMethod(poClass, getterName);
                value = getter.invoke(entity);
            } catch (Exception e) {
                logger.error("解析PO对象出错 {}", e.getMessage());
                throw new BizException(BizStatus.S_BIZ_P_ERROR_INVALID_PO_OBJECT, e);
            }

            if (value != null) {
                listValues.add(value);
                sbFields.append(',').append(poDef.getFieldColumn(fieldName));
                sbValues.append(",?");
            }
        }

        //拼接并执行SQL语句
        StringBuilder sbSQL = new StringBuilder();

        sbSQL.append("insert into ").append(tableName);
        sbSQL.append('(').append(sbFields.substring(1)).append(')');
        sbSQL.append(" values (").append(sbValues.substring(1)).append(')');

        String sql = sbSQL.toString();
        if (logger.isInfoEnabled()) {
            logger.info("SQL: {} with {}", sql, listValues.toString());
        }
        int result = db.update(sql, listValues.toArray());

        return result;
    }
    
    private Method getMethod(Class<?> clazz, String getterName) {
        if(clazz.equals(Object.class)) {
            throw new BizException(BizStatus.S_BIZ_P_ERROR_INVALID_PO_OBJECT);
        }
        try {
           return clazz.getDeclaredMethod(getterName);
        } catch (NoSuchMethodException e) {
         return  this.getMethod(clazz.getSuperclass(), getterName);
        }
       
    }

    public int update(Object entity, String includeFields, String excludeFields, String filter, Object... filterArgs) {
        Assert.notNull(entity);

        PoDefinition poDef = PoDefinition.instance(poClass);
        String tableName = poDef.getTableName(0);
        StringBuilder sbSQL = new StringBuilder();
        List<Object> listValues = new ArrayList<Object>();
        String getterName;
        Method getter;
        Object value;
        String[] saIncluded;
        String[] saExcluded;

        if (StringUtils.isEmpty(includeFields)) {
            saIncluded = poDef.getFields();
            if (StringUtils.isEmpty(excludeFields)) {
                saExcluded = new String[0];
            } else {
                saExcluded = excludeFields.split(",");
                Arrays.sort(saExcluded);
            }
        } else {
            saIncluded = includeFields.split(",");
            saExcluded = new String[0];
        }

        sbSQL.append("update ").append(tableName).append(" set ");
        for (String fieldName : saIncluded) {
            //去掉空格
            fieldName = fieldName.trim();
            if (Arrays.binarySearch(saExcluded, fieldName) >= 0) {
                continue;
            }
            if (poDef.isFieldReadOnly(fieldName)) {
                continue;
            }
            getterName = "get" + StringUtils.capitalize(fieldName);
            value = null;
            try {
                getter = this.getMethod(poClass, getterName);
                value = getter.invoke(entity);
            } catch (Exception e) {
                logger.error("解析PO对象出错: {}", e.getMessage());
                throw new BizException(BizStatus.S_BIZ_P_ERROR_INVALID_PO_OBJECT, e);
            }

            listValues.add(value);
            sbSQL.append(poDef.getFieldColumn(fieldName)).append("=?,");
        }
        sbSQL.deleteCharAt(sbSQL.length() - 1);

        if (!StringUtils.isEmpty(filter)) {
            sbSQL.append(" where ").append(PoUtils.parseFilter(poDef, filter, null));
            //拼接listValues&filterArgs
            if (filterArgs != null) {
                for (Object o : filterArgs) {
                    listValues.add(o);
                }
            }
        }

        //执行SQL
        String sql = sbSQL.toString();
        if (logger.isInfoEnabled()) {
            logger.info("SQL: {} with {}", sql, listValues.toString());
        }
        int result = db.update(sql, listValues.toArray());

        return result;
    }

    /**
     * 删除操作
     * 
     * @param filter 只删除符合此条件的记录，可以为null
     * @param filterArgs
     * @return 受影响的记录数
     */
    public int delete(String filter, Object... filterArgs) {
        PoDefinition poDef = PoDefinition.instance(poClass);
        StringBuilder sbSQL = new StringBuilder();

        sbSQL.append("delete from ").append(poDef.getTableName(0));
        if (!StringUtils.isEmpty(filter)) {
            sbSQL.append(" where ").append(PoUtils.parseFilter(poDef, filter, null));
        }

        //执行SQL
        String sql = sbSQL.toString();
        if (logger.isInfoEnabled()) {
            logger.info("SQL: {} with {}", sql, StringUtils.arrayToCommaDelimitedString(filterArgs));
        }
        int result = db.update(sql, filterArgs);

        return result;
    }

}

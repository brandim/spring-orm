package indi.gavin.orm;

import indi.gavin.orm.helper.GenericDaoHelper;

/**
 * 进行PO增加、修改、删除的通用实现
 */
public interface GenericDao<T> extends JdbcWired, ParameterizedPoHandler<T> {
    /**
     * 创建一条新记录。
     * 
     * @param entity 实体对象
     * @return 受影响的记录行数（正常情况下应该为“1”）
     */
    default int create(T entity) {
        GenericDaoHelper helper = GenericDaoHelperFactory.getHelper(getJdbcTemplate(), getPoClass());

        return helper.create(entity);
    }

    /**
     * 修改PO记录。
     * 
     * @param entity 包含PO记录新属性的PO实例
     * @param includeFields 只更新哪些属性，属性之间用“,”分隔，可以为null。当includeFields和excludeField都为null时，更新PO的所有属性.
     * @param excludeFields 不更新哪些属性，属性之间用“,”分隔,（仅当includeFields为null时有效）
     * @param filter 只更新符合此条件的记录，可以为null
     * @param filterArgs
     * @return 受影响的记录数
     */
    default int update(T entity, String includeFields, String excludeFields, String filter, Object... filterArgs) {
        GenericDaoHelper helper = GenericDaoHelperFactory.getHelper(getJdbcTemplate(), getPoClass());

        return helper.update(entity, includeFields, excludeFields, filter, filterArgs);
    }

    /**
     * 删除操作
     * 
     * @param filter 只删除符合此条件的记录，可以为null
     * @param filterArgs
     * @return 受影响的记录数
     */
    default int delete(String filter, Object... filterArgs) {
        GenericDaoHelper helper = GenericDaoHelperFactory.getHelper(getJdbcTemplate(), getPoClass());

        return helper.delete(filter, filterArgs);
    }

}

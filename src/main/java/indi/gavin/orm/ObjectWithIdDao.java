package indi.gavin.orm;

import java.util.List;

import org.springframework.util.StringUtils;

/**
 * Just for demo.
 * 
 * @author Gavin
 *
 * @param <T>
 */
public interface ObjectWithIdDao<T> extends GenericDao<T>, GenericQuery<T> {

    /**
     * 通过id查询相应记录
     */
    default T get(String id) {
        return queryForObject(null, null, "{#id}=?", id);
    }

    default <R> R getAs(Class<R> clazz, String id) {
        return queryForObjectAs(clazz, null, null, "{#id}=?", id);
    }

    /**
     * 取得数据对象清单。
     * 
     * @param start
     * @param count
     * @param filter
     * @param filterArgs
     * @return
     */
    default List<T> list(int start, int count, String orderBy, String filter, Object... filterArgs) {
        return queryForList(null, null, start, count, orderBy, null, filter, filterArgs);
    }

    default <R> List<R> listAs(Class<R> clazz, int start, int count, String orderBy, String filter,
            Object... filterArgs) {
        return queryForListAs(clazz, null, null, start, count, orderBy, null, filter, filterArgs);
    }

    /**
     * 查询符合条件的记录数
     * 
     * @param filter
     * @param filterArgs
     * @return
     */
    default int count(String filter, Object... filterArgs) {
        int result = queryForValue(Integer.class, "count(*)", filter, filterArgs);

        return result;
    }

    /**
     * 修改指定id的记录
     */
    default int update(T po, String id) {
        return update(po, null, "id", "{#id}=?", id);
    }

    /**
     * 根据条件修改数据记录（ID除外）
     */
    default int updateByFilter(T po, String filter, Object... filterArgs) {
        return update(po, null, "id", filter, filterArgs);
    }

    /**
     * 根据ID修改数据记录的指定属性
     */
    default int updateFields(T po, String fields, String id) {
        if (StringUtils.isEmpty(fields)) {
            throw new BizException(BizStatus.S_BIZ_P_ERROR_NULL_PO_FIELDS);
        }
        return update(po, fields, null, "{#id}=?", id);
    }

    /**
     * 根据条件修改数据记录的指定属性
     */
    default int updateFields(T po, String fields, String filter, Object... filterArgs) {
        return update(po, fields, null, filter, filterArgs);
    }

    /**
     * 删除指定id对应的记录
     * 
     * @param id
     * @return
     */
    default int delete(String id) {
        return delete("{#id}=?", id);
    }
}

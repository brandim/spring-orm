package indi.gavin.orm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;


/**
 * 进行PO查询的通用实现
 */
public interface GenericQuery<T> extends JdbcWired, ParameterizedPoHandler<T> {
    static Logger LOG =  LoggerFactory.getLogger(GenericQuery.class);

    default <R> RowMapper<R> getRowMapper(Class<R> clazz) {
        return PoRowMapper.instance(clazz);
    }

    /**
     * PO查询
     * 
     * @param includeFields 查询结果中只包括哪些字段，可以为空
     * @param excludeFields 查询结果中不包括哪些字段，可以为空
     * @param start 从第几条记录开始（0-based）
     * @param count 最多返回多少条记录
     * @param orderByQL 排序方式
     * @param groupByQL 分组方式
     * @param filterQL 查询条件
     * @param filterArgs 查询条件的参数（预编译的SQL参数）
     * @return PO列表
     */
    default List<T> queryForList(String includeFields, String excludeFields, int start, int count, String orderByQL,
            String groupByQL, String filterQL, Object... filterArgs) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());
        String sql =
                PoUtils.getQuerySQL(poDef, includeFields, excludeFields, start, count, orderByQL, groupByQL, filterQL,
                        null);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} with {}", sql, StringUtils.arrayToCommaDelimitedString(filterArgs));
        }

        RowMapper<T> rowMapper = getRowMapper(getPoClass());
        if (rowMapper == null) {
            throw new BizException(BizStatus.S_PERSISTENCE_NOT_ROWMAPPER_FOUND);
        }

        List<T> result;
        try {
            result = getJdbcTemplate().query(sql, rowMapper, filterArgs);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * 与上一个方法修改，区别是通过第一个参数（clazz）指定返回的对象类型
     */
    default <R> List<R> queryForListAs(Class<R> clazz, String includeFields, String excludeFields, int start,
            int count, String orderByQL, String groupByQL, String filterQL, Object... filterArgs) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());
        String sql =
                PoUtils.getQuerySQL(poDef, includeFields, excludeFields, start, count, orderByQL, groupByQL, filterQL,
                        null);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} with {}", sql, StringUtils.arrayToCommaDelimitedString(filterArgs));
        }

        RowMapper<R> rowMapper = getRowMapper(clazz);
        if (rowMapper == null) {
            throw new BizException(BizStatus.S_PERSISTENCE_NOT_ROWMAPPER_FOUND);
        }

        List<R> result;
        try {
            result = getJdbcTemplate().query(sql, rowMapper, filterArgs);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * 与上一个方法修改，区别是通过第一个参数（rowMapper）指定对查询结果的映射器
     */
    default <R> List<R> queryForListWith(RowMapper<R> rowMapper, String includeFields, String excludeFields, int start,
            int count, String orderByQL, String groupByQL, String filterQL, Object... filterArgs) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());
        String sql =
                PoUtils.getQuerySQL(poDef, includeFields, excludeFields, start, count, orderByQL, groupByQL, filterQL,
                        null);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} with {}", sql, StringUtils.arrayToCommaDelimitedString(filterArgs));
        }

        List<R> result;
        try {
            result = getJdbcTemplate().query(sql, rowMapper, filterArgs);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * PO查询
     * 
     * @param includeFields 查询结果中只包括哪些字段，可以为空
     * @param excludeFields 查询结果中不包括哪些字段，可以为空
     * @param start 从第几条记录开始（0-based）
     * @param count 最多返回多少条记录
     * @param orderByQL 排序方式
     * @param groupByQL 分组方式
     * @param filterQL 查询条件
     * @param filterPo 查询条件的参数（预编译的SQL参数）
     * @return PO列表
     */
    default List<T> queryForList(String includeFields, String excludeFields, int start, int count, String orderByQL,
            String groupByQL, String filterQL, T filterPo) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());

        String sql =
                PoUtils.getQuerySQL(poDef, includeFields, excludeFields, start, count, orderByQL, groupByQL, filterQL,
                        filterPo);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} ", sql);
        }

        RowMapper<T> rowMapper = getRowMapper(getPoClass());
        if (rowMapper == null) {
            throw new BizException(BizStatus.S_PERSISTENCE_NOT_ROWMAPPER_FOUND);
        }

        List<T> result;
        try {
            result = getJdbcTemplate().query(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * 与上一个方法修改，区别是通过第一个参数（clazz）指定返回的对象类型
     */
    default <R> List<R> queryForListAs(Class<R> clazz, String includeFields, String excludeFields, int start,
            int count, String orderByQL, String groupByQL, String filterQL, T filterPo) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());

        String sql =
                PoUtils.getQuerySQL(poDef, includeFields, excludeFields, start, count, orderByQL, groupByQL, filterQL,
                        filterPo);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} ", sql);
        }

        RowMapper<R> rowMapper = getRowMapper(clazz);
        if (rowMapper == null) {
            throw new BizException(BizStatus.S_PERSISTENCE_NOT_ROWMAPPER_FOUND);
        }
        List<R> result;
        try {
            result = getJdbcTemplate().query(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * 与上一个方法修改，区别是通过第一个参数（rowMapper）指定对查询结果的映射器
     */
    default <R> List<R> queryForListWith(RowMapper<R> rowMapper, String includeFields, String excludeFields, int start,
            int count, String orderByQL, String groupByQL, String filterQL, T filterPo) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());

        String sql =
                PoUtils.getQuerySQL(poDef, includeFields, excludeFields, start, count, orderByQL, groupByQL, filterQL,
                        filterPo);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} ", sql);
        }

        List<R> result;
        try {
            result = getJdbcTemplate().query(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * PO查询，最多返回一条记录
     * 
     * @param includeFields 查询结果中只包括哪些字段，可以为空
     * @param excludeFields 查询结果中不包括哪些字段，可以为空
     * @param filterQL 查询条件
     * @param filterArgs 查询条件的参数（预编译的SQL参数）
     * 
     * @return PO实例，null表示没有符合条件的记录
     */
    default T queryForObject(String includeFields, String excludeFields, String filterQL, Object... filterArgs) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());
        String sql = PoUtils.getQuerySQL(poDef, includeFields, excludeFields, 0, 0, null, null, filterQL, null);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} with {}", sql, StringUtils.arrayToCommaDelimitedString(filterArgs));
        }

        RowMapper<T> rowMapper = getRowMapper(getPoClass());
        if (rowMapper == null) {
            throw new BizException(BizStatus.S_PERSISTENCE_NOT_ROWMAPPER_FOUND);
        }
        T result;
        try {
            result = getJdbcTemplate().queryForObject(sql, rowMapper, filterArgs);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * 与上一个方法修改，区别是通过第一个参数（clazz）指定返回的对象类型
     */
    default <R> R queryForObjectAs(Class<R> clazz, String includeFields, String excludeFields, String filterQL,
            Object... filterArgs) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());
        String sql = PoUtils.getQuerySQL(poDef, includeFields, excludeFields, 0, 0, null, null, filterQL, null);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} with {}", sql, StringUtils.arrayToCommaDelimitedString(filterArgs));
        }

        RowMapper<R> rowMapper = getRowMapper(clazz);
        if (rowMapper == null) {
            throw new BizException(BizStatus.S_PERSISTENCE_NOT_ROWMAPPER_FOUND);
        }

        R result;
        try {
            result = getJdbcTemplate().queryForObject(sql, rowMapper, filterArgs);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }
        return result;
    }

    /**
     * 与上一个方法修改，区别是通过第一个参数（rowMapper）指定对查询结果的映射器
     */
    default <R> R queryForObjectWith(RowMapper<R> rowMapper, String includeFields, String excludeFields,
            String filterQL, Object... filterArgs) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());
        String sql = PoUtils.getQuerySQL(poDef, includeFields, excludeFields, 0, 0, null, null, filterQL, null);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} with {}", sql, StringUtils.arrayToCommaDelimitedString(filterArgs));
        }

        R result;
        try {
            result = getJdbcTemplate().queryForObject(sql, rowMapper, filterArgs);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * PO查询，最多返回一条记录
     * 
     * @param includeFields 查询结果中只包括哪些字段，可以为空
     * @param excludeFields 查询结果中不包括哪些字段，可以为空
     * @param filterQL 查询条件
     * @param filterPo 查询条件的参数（预编译的SQL参数）
     * 
     * @return PO实例，null表示没有符合条件的记录
     */
    default T queryForObject(String includeFields, String excludeFields, String filterQL, T filterPo) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());

        String sql = PoUtils.getQuerySQL(poDef, includeFields, excludeFields, 0, 0, null, null, filterQL, filterPo);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} ", sql);
        }

        RowMapper<T> rowMapper = getRowMapper(getPoClass());
        if (rowMapper == null) {
            throw new BizException(BizStatus.S_PERSISTENCE_NOT_ROWMAPPER_FOUND);
        }

        T result;
        try {
            result = getJdbcTemplate().queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * 与上一个方法修改，区别是通过第一个参数（clazz）指定返回的对象类型
     */
    default <R> R queryForObjectAs(Class<R> clazz, String includeFields, String excludeFields, String filterQL,
            T filterPo) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());

        String sql = PoUtils.getQuerySQL(poDef, includeFields, excludeFields, 0, 0, null, null, filterQL, filterPo);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} ", sql);
        }

        RowMapper<R> rowMapper = getRowMapper(clazz);
        if (rowMapper == null) {
            throw new BizException(BizStatus.S_PERSISTENCE_NOT_ROWMAPPER_FOUND);
        }

        R result;
        try {
            result = getJdbcTemplate().queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * 与上一个方法修改，区别是通过第一个参数（rowMapper）指定对查询结果的映射器
     */
    default <R> R queryForObjectWith(RowMapper<R> rowMapper, String includeFields, String excludeFields,
            String filterQL, T filterPo) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());

        String sql = PoUtils.getQuerySQL(poDef, includeFields, excludeFields, 0, 0, null, null, filterQL, filterPo);

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} ", sql);
        }

        R result;
        try {
            result = getJdbcTemplate().queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        }

        return result;
    }

    /**
     * 从PO表中查询，返回一个值，如：进行count统计。
     * 
     * @param valueClass 返回值的数据类型
     * @param expression 查询内容的表达式，如：count(*)
     * @param filterQL 查询条件
     * @param filterPo 查询条件的参数（预编译的SQL参数）
     * 
     * @return
     */
    default <R> R queryForValue(Class<R> valueClass, String expression, String filterQL, T filterPo) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());
        StringBuilder sb = new StringBuilder();

        sb.append("select ").append(PoUtils.parseFilter(poDef, expression, null));
        sb.append(" from ").append(PoUtils.getQueryFrom(poDef));
        if (!StringUtils.isEmpty(filterQL)) {
            sb.append(" where ").append(PoUtils.parseFilter(poDef, filterQL, filterPo));
        }

        String sql = sb.toString();

        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {}", sql);
        }
        R value = getJdbcTemplate().queryForObject(sql, valueClass);

        return value;
    }

    /**
     * 从PO表中查询，返回一个值，如：进行count统计。
     * 
     * @param valueClass 返回值的数据类型
     * @param expression 查询内容的表达式，如：count(*)
     * @param filterQL 查询条件
     * @param filterArgs 查询条件的参数（预编译的SQL参数）
     * 
     * @return
     */
    default <R> R queryForValue(Class<R> valueClass, String expression, String filterQL, Object... filterArgs) {
        PoDefinition poDef = PoDefinition.instance(getPoClass());
        StringBuilder sb = new StringBuilder();

        sb.append("select ").append(PoUtils.parseFilter(poDef, expression, null));
        sb.append(" from ").append(PoUtils.getQueryFrom(poDef));
        String join = poDef.getTableJoin();
        if (StringUtils.isEmpty(join) && !StringUtils.isEmpty(filterQL)) {
            sb.append(" where ").append(PoUtils.parseFilter(poDef, filterQL, null));
        } else if(!StringUtils.isEmpty(join) && !StringUtils.isEmpty(filterQL)) {
            sb.append(" where (").append(PoUtils.parseFilter(poDef, filterQL, null)).append(") and ").append(join);
        } else if (!StringUtils.isEmpty(join) && StringUtils.isEmpty(filterQL)) {
            sb.append(" where ").append(join);
        }
       

        String sql = sb.toString();
        if (LOG.isInfoEnabled()) {
            LOG.info("SQL: {} with {}", sql, StringUtils.arrayToCommaDelimitedString(filterArgs));
        }
        R value = getJdbcTemplate().queryForObject(sql, valueClass, filterArgs);

        return value;
    }
}

package indi.gavin.orm;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;
import org.springframework.expression.ParserContext;

public final class PoUtils implements BizStatus {
    private PoUtils() {
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // for query
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    public static String getQuerySQL(PoDefinition poDef, String includeFields, String excludeFields, int start,
            int count, String orderBy, String groupBy, String filter, Object filterPo) {
        StringBuilder sb = new StringBuilder();

        sb.append("select ").append(PoUtils.getQuerySelect(poDef, includeFields, excludeFields));
        sb.append(" from ").append(PoUtils.getQueryFrom(poDef));
        if (!StringUtils.isEmpty(filter)) {
            if (StringUtils.isEmpty(poDef.getTableJoin())) {
                sb.append(" where ").append(PoUtils.parseFilter(poDef, filter, filterPo));
            } else {
                sb.append(" where (").append(poDef.getTableJoin()).append(") and (");
                sb.append(PoUtils.parseFilter(poDef, filter, filterPo)).append(')');
            }
        } else if (!StringUtils.isEmpty(poDef.getTableJoin())) {
            sb.append(" where (").append(poDef.getTableJoin()).append(')');
        }

        if (!StringUtils.isEmpty(StringUtils.trimAllWhitespace(orderBy))) {
            sb.append(" order by ").append(PoUtils.parseFilter(poDef, orderBy, null));
        }
        if (!StringUtils.isEmpty(StringUtils.trimAllWhitespace(groupBy))) {
            sb.append(" group by ").append(PoUtils.parseFilter(poDef, groupBy, null));
        }

        if (start < 0) {
            start = 0;
        }
        if (count > 0) {
            sb.append(" limit ").append(start).append(',').append(count);
        }

        return sb.toString();
    }

    public static String getQueryFrom(PoDefinition poDef) {
        if (StringUtils.isEmpty(poDef.getTableFrom())) {
            StringBuilder sb = new StringBuilder();
            String[] tables = poDef.getTableAliases();

            sb.append(poDef.getTableName(tables[0])).append(" AS ").append(tables[0]);
            for (int i = 1; i < tables.length; i++) {
                sb.append(',').append(poDef.getTableName(tables[i])).append(" AS ").append(tables[i]);
            }
            return sb.toString();
        } else {
            return poDef.getTableFrom();
        }
    }

    protected static String getQuerySelect(PoDefinition poDef, String includeFields, String excludeFields) {
        if (!StringUtils.isEmpty(includeFields)) {
            return getQuerySelectByInclude(poDef, includeFields);
        } else if (!StringUtils.isEmpty(excludeFields)) {
            return getQuerySelectByExclude(poDef, excludeFields);
        } else {
            return getQuerySelectByDefault(poDef);
        }
    }

    protected static String getQuerySelectByDefault(PoDefinition poDef) {
        StringBuilder sb = new StringBuilder();
        String columnName;

        for (String field : poDef.getFields()) {
            columnName = fieldName2ColumnName(field);
            sb.append(',');
            sb.append(poDef.getFieldColumn(field)).append(" as ").append(columnName);
        }

        return sb.substring(1);
    }

    protected static String getQuerySelectByInclude(PoDefinition poDef, String includeFields) {
        StringBuilder sb = new StringBuilder();
        String columnName;

        for (String field : includeFields.split(",")) {
            columnName = fieldName2ColumnName(field);
            sb.append(',');
            sb.append(poDef.getFieldColumn(field)).append(" as ").append(columnName);
        }

        return sb.substring(1);
    }

    protected static String getQuerySelectByExclude(PoDefinition poDef, String excludeFields) {
        StringBuilder sb = new StringBuilder();
        String columnName;

        excludeFields = "," + excludeFields + ",";
        for (String field : poDef.getFields()) {
            if (excludeFields.indexOf(',' + field + ',') < 0) {
                columnName = fieldName2ColumnName(field);
                sb.append(',');
                sb.append(poDef.getFieldColumn(field)).append(" as ").append(columnName);
            }
        }

        return sb.substring(1);
    }

    public static String parseFilter(PoDefinition poDef, String filter, Object filterPo) {
        ExpressionParser filterParser = new SpelExpressionParser();
        StandardEvaluationContext ctx;
        if (filterPo == null) {
            ctx = new StandardEvaluationContext();
        } else {
            ctx = new StandardEvaluationContext(filterPo);
        }

        for (String fieldName : poDef.getFields()) {
            ctx.setVariable(fieldName, poDef.getFieldColumn(fieldName));
        }

        String result = filterParser.parseExpression(filter, FilterParseContext.instance()).getValue(ctx, String.class);

        return result;
    }

    public static String columnName2FieldName(String dbColumnName) {
        String[] columnWords = dbColumnName.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        sb.append(columnWords[0]);
        for (int i = 1; i < columnWords.length; i++) {
            sb.append(StringUtils.capitalize(columnWords[i]));
        }
        return sb.toString();
    }

    public static String class2TableName(Class<?> poClass) {
        String className = poClass.getSimpleName();

        if (className.length() > 2) {
            String last2 = className.substring(className.length() - 2, className.length()).toUpperCase();
            if ("PO".equals(last2) || "BO".equals(last2) || "VO".equals(last2)) {
                className = className.substring(0, className.length() - 2);
            }
        }

        return fieldName2ColumnName(className);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    // for dao
    /////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 把实体的属性名变成字段名
     * 
     * @param poFieldName
     * @return
     */
    public static String fieldName2ColumnName(String poFieldName) {

        if (StringUtils.isEmpty(poFieldName)) {
            return "";
        }
        char[] chars = poFieldName.toCharArray();
        StringBuffer sb = new StringBuffer();
        char c;

        for (int i = 0; i < chars.length; i++) {
            c = chars[i];
            if (i > 0 && c >= 'A' && c <= 'Z') {
                sb.append("_");
            }
            sb.append(c);
        }
        String columnName = sb.toString();
        if (StringUtils.startsWithIgnoreCase(columnName, "-")) {
            columnName = columnName.substring(1, columnName.length());
        }
        return columnName.toUpperCase();
    }

    /**
     * 获取传进对象的所有对象名和值，并把对象名转成字段名
     * 
     * @param entity
     * @return
     * @throws Exception
     */
    public static Map<String, Object> po2Map(Object entity) throws Exception {
        Field[] field = entity.getClass().getDeclaredFields();
        Map<String, Object> map = new LinkedHashMap<String, Object>();

        for (Field f : field) {
            //把私有变量变成公有
            f.setAccessible(true);
            String fieldName = fieldName2ColumnName(f.getName());
            if ("id".equals(fieldName) && null == f.get(entity)) {
                String tableName = fieldName2ColumnName(entity.getClass().getSimpleName());
                map.put(fieldName, BizUtils.uuid(tableName));
            } else {
                map.put(fieldName, f.get(entity));
            }
        }
        return map;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static Map<Class<?>, Class<?>> class2PoClass = new HashMap<Class<?>, Class<?>>();

    /**
     * 
     * @param daoClass
     * @param parameterizedPoType (暂未使用）
     * @return
     */
    public static synchronized Class<?> getParameterizedPoClass(Class<?> rootClass, Class<?> parameterizedPoType) {
        Class<?> poClass = class2PoClass.get(rootClass);
        if (poClass != null) {
            return poClass;
        }

        Type type;
        Type[] types;
        Class<?> clazz;
        Class<?>[] classes;
        ParameterizedType ptype;

        //检查父类
        type = rootClass.getGenericSuperclass();
        if (type != null && type instanceof ParameterizedType) {
            ptype = (ParameterizedType) type;
            clazz = ptype.getRawType().getClass();
            if (clazz.equals(parameterizedPoType)) {
                poClass = (Class<?>) ptype.getActualTypeArguments()[0];
                class2PoClass.put(rootClass, poClass);
                return poClass;
            }
        }

        //检查当前类实现的接口        
        types = rootClass.getGenericInterfaces();
        for (Type t : types) {
            if (t instanceof ParameterizedType) {
                ptype = (ParameterizedType) t;
                poClass = (Class<?>) ptype.getActualTypeArguments()[0];
                class2PoClass.put(rootClass, poClass);
                return poClass;
            }
        }

        //检查上一级接口
        classes = (Class<?>[]) rootClass.getInterfaces();
        for (Class<?> c : classes) {
            poClass = getParameterizedPoClass(c, parameterizedPoType);
            if (poClass != null) {
                return poClass;
            }
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final class FilterParseContext implements ParserContext {

        private static FilterParseContext inst = new FilterParseContext();

        public static FilterParseContext instance() {
            return inst;
        }

        /**
         * 禁止被实例化
         */
        private FilterParseContext() {
        }

        @Override
        public String getExpressionPrefix() {
            return "{";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }

        @Override
        public boolean isTemplate() {
            return true;
        }
    }
}

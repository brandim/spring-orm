package indi.gavin.orm;

import indi.gavin.orm.annotations.PoColumn;
import indi.gavin.orm.annotations.PoTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

public final class PoDefinition {

    private static Map<Class<?>, PoDefinition> instances = new HashMap<Class<?>, PoDefinition>();

    public static synchronized PoDefinition instance(Class<?> poClass) {
        PoDefinition poDef = instances.get(poClass);
        if (poDef == null) {
            poDef = new PoDefinition(poClass);
            instances.put(poClass, poDef);
        }
        return poDef;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Class<?> poClass;

    //PO对应的表的信息：key->table_alias，value->table_name
    private Map<String, String> poTables = new HashMap<String, String>();

    private String poTableFrom = null;

    private String poTableJoin = null;

    private String poTableTrace = null;

    //PO属性对应的字段的信息：key->po_fieldname，value->table_alias.column_name
    private Map<String, String> poFields = new HashMap<String, String>();

    private Map<String, Boolean> poFieldReadOnly = new HashMap<String, Boolean>();

    private PoDefinition(Class<?> poClass) {
        this.poClass = poClass;
        init();
    }

    public int getTableCount() {
        return poTables.size();
    }

    public String[] getTableAliases() {
        return poTables.keySet().toArray(new String[0]);
    }

    public String getTableName(String tableAlias) {
        return poTables.get(tableAlias);
    }

    public String getTableJoin() {
        return poTableJoin;
    }

    public String getTableFrom() {
        return poTableFrom;
    }

    public String getTableTrace() {
        return poTableTrace;
    }

    public String getTableName(int index) {
        return poTables.get(getTableAliases()[index]);
    }

    public String[] getFields() {
        return poFields.keySet().toArray(new String[0]);
    }

    public String getFieldColumn(String fieldName) {
        return poFields.get(fieldName);
    }

    public boolean isFieldReadOnly(String fieldName) {
        return poFieldReadOnly.get(fieldName);
    }

    private void init() {
        String tableNames;

        //分析PO的表定义
        PoTable annTable = poClass.getAnnotation(PoTable.class);
        if (annTable == null) {
            tableNames = getDefaultDBName(poClass.getSimpleName());
            poTableJoin = null;
            poTableFrom = null;
            poTableTrace = null;
        } else {
            tableNames = annTable.value();
            poTableJoin = annTable.join();
            poTableFrom = annTable.from();
            poTableTrace = annTable.trace();
        }

        for (String s : StringUtils.trimAllWhitespace(tableNames).split(",")) {
            if (StringUtils.isEmpty(s))
                continue;

            String[] sa = s.split("=");
            if (sa.length > 1) {
                poTables.put(sa[0], sa[1]);
            } else {
                poTables.put(s, s);
            }
        }

        //处理PO属性与表字段的定义关系
        getPoFileds(poClass);

    }

    private void getPoFileds(Class<?> clazz) {
        PoColumn annColumn;
        String field;
        String columnValue;
        boolean readOnly;

        for (Field f : clazz.getDeclaredFields()) {
            field = f.getName();
            if ("serialVersionUID".equals(field)) {
                continue;
            }
            if (!f.getType().isPrimitive() && !f.getType().getPackage().getName().startsWith("java")) {
                getPoFileds(f.getType(), field);
                continue;
            }
            if (!StringUtils.isEmpty(poFields.get(field))) {
                //子类重新定义了该属性，优先保留子类定义
                continue;
            }

            annColumn = f.getAnnotation(PoColumn.class);
            if (annColumn == null) {
                columnValue = null;
                readOnly = false;
            } else {
                columnValue = annColumn.value();
                readOnly = annColumn.readOnly();
            }
            if (StringUtils.isEmpty(columnValue)) {
                columnValue = getDefaultDBName(field);
            }

            if (!"-".equals(columnValue)) {
                poFields.put(field, columnValue);
                poFieldReadOnly.put(field, readOnly);
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        if (null != superClass && !Object.class.equals(superClass)) {
            getPoFileds(superClass);
        }
    }

    private void getPoFileds(Class<?> clazz, String suffix) {
        PoColumn annColumn;
        String field;
        String columnValue;
        boolean readOnly;

        for (Field f : clazz.getDeclaredFields()) {
            field = f.getName();
            if ("serialVersionUID".equals(field)) {
                continue;
            }
            if (!f.getType().isPrimitive() && !f.getType().getPackage().getName().startsWith("java")) {
                getPoFileds(f.getType(), suffix);
            }
            if (!StringUtils.isEmpty(poFields.get(suffix + "_" + field))) {
                //子类重新定义了该属性，优先保留子类定义
                continue;
            }

            annColumn = f.getAnnotation(PoColumn.class);
            if (annColumn == null) {
                columnValue = null;
                readOnly = false;
            } else {
                columnValue = annColumn.value();
                readOnly = annColumn.readOnly();
            }
            if (StringUtils.isEmpty(columnValue)) {
                columnValue = suffix + "." + getDefaultDBName(field);
            }
            if (!"-".equals(columnValue)) {
                poFields.put(suffix + "$" + field, columnValue);
                poFieldReadOnly.put(suffix + "." + field, readOnly);
            }
        }

    }

    protected String getDefaultDBName(String poName) {
        if (poName.endsWith("Po")) {
            poName = poName.substring(0, poName.length() - 2);
        }
        StringBuilder sb = new StringBuilder();
        int delta = 'a' - 'A';

        char[] buf = new char[poName.length()];
        poName.getChars(0, poName.length(), buf, 0);
        for (char c : buf) {
            if (c >= 'A' && c <= 'Z') {
                if (sb.length() > 0) {
                    sb.append('_');
                }
                sb.append((char) (c + delta));
            } else {
                sb.append(c);
            }
        }

        return sb.toString().toUpperCase();
    }
}

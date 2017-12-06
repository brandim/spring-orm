package indi.gavin.orm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PoTable {
    /**
     * 对应的表名， 格式："<tablename>" 或 ”<tablealias>=<tablename>“。
     * 允许指定多个表名，中间用逗号分隔。
     * 例如：
     * 1.    @PoTable("mytalbename")
     * 2.    @PoTable("tbl=mytalbename")
     * 3.    @PoTable("mytable1,tbl2=mytable2")
     */
    String value() default "";

    /**
     * 在多表联合时指定表之间的联合条件，格式：“<sql_condition>”。
     * 例如：
     *     @PoTable(value="U=TM_SEC_USER,R=TM_SEC_ROLE",join="U.ROLE_ID=R.ID")
     */
    String join() default "";

    /**
     * 指定在执行SELECT查询时，“FROM”语法后的内容。
     * 如果未指定，则根据value中的定义自动生成。建议内连接查询使用value方式进行设置。
     * 
     * 例如：
     *     @Potable(from="TM_SEC_USER as U left join TM_SEC_ROLE as R on U.ROLE_ID=R.ID")
     */
    String from() default "";

    /**
     * 将该表的变更历史记录在哪里。
     * 
     * 要求：
     * 1.  原表必须ID字段
      *2.  记录表在原表结构的基础上追加字段“TRACE_ID”和“TRACE_ACTION”、并以TRACE_ID为主键
     */
    String trace() default "";
}

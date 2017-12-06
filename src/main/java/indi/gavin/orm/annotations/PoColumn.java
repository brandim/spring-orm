package indi.gavin.orm.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PoColumn {
    /**
     * 对应的数据库字段名或SQL表达式， 格式："<columndefinition>" ，"-"表示该属性与数据库无关。
     * 1.    @PoColumn("db_column_name")
     * 2.    @PoColumn("column1+column2")
     * 3.    @PoColumn("-")
     */
    String value() default "";

    /**
     * 该字段是否只读，true表示是只读数据，在进行修改操作时将忽略该字段。
     * 
     * 应用将所有通过SQL表达式计算的属性设置为只读。
     */
    boolean readOnly() default false;
}

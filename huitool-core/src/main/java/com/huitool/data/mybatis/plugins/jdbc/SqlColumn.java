package com.huitool.data.mybatis.plugins.jdbc;

import com.huitool.data.mybatis.plugins.jdbc.expression.BetweenExpression;
import com.huitool.data.mybatis.plugins.jdbc.expression.Expressions;
import com.huitool.data.mybatis.plugins.jdbc.expression.InExpression;
import com.huitool.data.mybatis.plugins.jdbc.expression.NotInExpression;
import com.huitool.data.mybatis.plugins.jdbc.expression.NotNullExpression;
import com.huitool.data.mybatis.plugins.jdbc.expression.NullExpression;
import com.huitool.data.mybatis.plugins.jdbc.expression.SimpleExpression;
import com.huitool.util.Functions;
import com.huitool.util.LambdaUtils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.util.Collection;

/**
 * <描述信息>
 */
@Data
@Accessors(fluent = true)
public class SqlColumn<T> {
    private String name;
    private Class<T> domainType;
    private Field property;
    private JDBCType jdbcType;
    private String alias;
    private Boolean descending;

    public SqlColumn(Class<T> domainType, Field property) {
        this.domainType = domainType;
        this.property = property;

        if(property.isAnnotationPresent(Column.class)){
            this.name = property.getAnnotation(Column.class).value();
        }else{
            this.name = property.getName();
        }
    }

    public static <T> SqlColumn of(Functions.SerializableFunction<T, ?> fn){
        return new SqlColumn(LambdaUtils.getImplClass(fn), getProperty(fn));
    }

    public boolean isPersistable(){
        return !(property.isAnnotationPresent(Transient.class) || property.isAnnotationPresent(java.beans.Transient.class));
    }

    @Override
    public String toString() {
        return String.format("%s as %s", this.name, StringUtils.hasText(this.alias) ? this.alias : this.name);
//        return StringUtils.hasText(this.alias) ? String.format("%s as %s", this.name, this.alias) : this.name;
    }

    public SimpleExpression eq(Object value){
        return Expressions.eq(this, value);
    }

    public SimpleExpression ne(Object value){
        return Expressions.ne(this, value);
    }

    public SimpleExpression like(Object value){
        return Expressions.like(this, value);
    }

    public SimpleExpression notLike(Object value){
        return Expressions.notLike(this, value);
    }

    public SimpleExpression gt(Object value){
        return Expressions.gt(this, value);
    }

    public SimpleExpression lt(Object value){
        return Expressions.lt(this, value);
    }

    public SimpleExpression le(Object value){
        return Expressions.le(this, value);
    }

    public SimpleExpression ge(Object value){
        return Expressions.ge(this, value);
    }

    public BetweenExpression between(Object low, Object high){
        return Expressions.between(this, low, high);
    }

    public InExpression in(Object... values){
        return Expressions.in(this, values);
    }

    public InExpression in(Collection values){
        return Expressions.in(this, values);
    }

    public NotInExpression notIn(Object... values){
        return Expressions.notIn(this, values);
    }

    public NotInExpression notIn(Collection values){
        return Expressions.notIn(this, values);
    }

    public NullExpression isNull(){
        return Expressions.isNull(this);
    }

    public NotNullExpression isNotNull(){
        return Expressions.isNotNull(this);
    }

    private static Field getProperty(Functions.SerializableFunction fn){
        String methodName = LambdaUtils.getImplMethodName(fn);
        String prefix = null;

        if(methodName.startsWith("get")){
            prefix = "get";
        }else if(methodName.startsWith("is")){
            prefix = "is";
        }

        if(prefix == null){
            throw new RuntimeException("无效的getter方法: "+methodName);
        }

        String fieldName = methodName.substring(prefix.length());
        return ReflectionUtils.findField(LambdaUtils.getImplClass(fn), Introspector.decapitalize(fieldName));
    }
}

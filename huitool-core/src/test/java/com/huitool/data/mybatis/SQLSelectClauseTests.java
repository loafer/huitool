package com.huitool.data.mybatis;

import com.huitool.data.City;
import com.huitool.data.mybatis.plugins.jdbc.SqlColumn;
import com.huitool.data.mybatis.plugins.jdbc.dml.SQLSelectClause;
import org.junit.Test;

/**
 * <描述信息>
 */
public class SQLSelectClauseTests {
    @Test
    public void testSelect(){
        SQLSelectClause selectClause = new SQLSelectClause();
        System.out.println(selectClause.from(City.class).getSQL());
    }

    @Test
    public void testSelectAliases(){
        final SqlColumn<City> NAME = SqlColumn.of(City::getName);
        System.out.println(
            new SQLSelectClause()
            .from(City.class)
            .where(SqlColumn.of(City::getId).eq(1L))
            .where(NAME.eq("Shandong"))
            .getSQL()
        );
    }
}

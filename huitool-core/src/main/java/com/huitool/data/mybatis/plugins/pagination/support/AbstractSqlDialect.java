package com.huitool.data.mybatis.plugins.pagination.support;

import com.huitool.data.mybatis.plugins.pagination.SqlDialect;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <描述信息>
 */
public abstract class AbstractSqlDialect implements SqlDialect {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSqlDialect.class);
    private String ORDER_BY_KEYWORD = "order\\s+by";
    private String ORDER_BY_ELEMENT_REGEX = "order\\s+by[\\w|\\W|\\s|\\S]*";
    private String OVER_ORDER_BY_REGEX = "over\\s+\\([\\w|\\W|\\s|\\S]*order\\s+by[\\w|\\W|\\s|\\S]*\\)";

    public int getCount(MappedStatement ms, Connection connection, Object parameterObject) throws SQLException {
        BoundSql boundSql = ms.getBoundSql(parameterObject);

        String sql = boundSql.getSql().trim();
        String countSql = getCountString(sql);

        logger.debug("Count SQL [{}]", countSql);
        logger.debug("Parameters: {} ", parameterObject);

        try(PreparedStatement stmt = connection.prepareStatement(countSql)){
            DefaultParameterHandler handler = new DefaultParameterHandler(ms, parameterObject, boundSql);
            handler.setParameters(stmt);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        }
    }

    protected String getCountString(String sql) {
        String newsql;
        if (containOrder(sql) && !containOver(sql)) {
            newsql = removeOrders(sql);
        } else {
            newsql = sql;
        }

        return "select count(1) from (" + newsql.trim() + ") tmp_count";
    }

    protected String getLineSql(String sql) {
        return sql.replaceAll("[\r\n]", " ").replaceAll("\\s{2,}", " ");
    }

    private boolean containOrder(String sql) {
        return containRegex(sql, ORDER_BY_KEYWORD);
    }

    private boolean containOver(String sql) {
        return containRegex(sql, OVER_ORDER_BY_REGEX);
    }

    private boolean containRegex(String sql, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        return matcher.find();
    }

    private String removeOrders(String sql) {
        Pattern p = Pattern.compile(ORDER_BY_ELEMENT_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }
}

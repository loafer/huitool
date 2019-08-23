package com.huitool.data.mybatis.plugins.pagination.support;

/**
 * <描述信息>
 */
public class PostgreSQLDialect extends AbstractSqlDialect {
    @Override
    public String getName() {
        return "postgresql";
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return this.getLimitString(sql, offset, Integer.toString(offset), Integer.toString(limit));
    }

    private String getLimitString(String sql, int offset, String offsetPlaceholder, String limitPlaceholder) {
        StringBuilder sb = new StringBuilder(this.getLineSql(sql));
        sb.append(" limit ");

        if (offset > 0) {
            sb.append(limitPlaceholder).append(" offset ").append(offsetPlaceholder);
        } else {
            sb.append(limitPlaceholder);
        }

        return sb.toString();
    }
}

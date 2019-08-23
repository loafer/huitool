package com.huitool.data.mybatis.plugins.pagination.support;

/**
 * <描述信息>
 */
public class OracleDialect extends AbstractSqlDialect {
    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return null;
    }

    private String getLimitString(final String sql, final int offset,
                                  final String offsetPlaceholder, final String limitPlaceholder) {
        String _sql = getLineSql(sql);
        StringBuilder pagingSelect = new StringBuilder();
        if (offset >= 0) {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(_sql);
        if (offset >= 0) {
            String endString = offsetPlaceholder + "+" + limitPlaceholder;
            pagingSelect.append(" ) row_ ) where rownum_ <= ")
                .append(endString).append(" and rownum_ > ").append(offsetPlaceholder);
        } else {
            pagingSelect.append(" ) where rownum <= ").append(limitPlaceholder);
        }

        return pagingSelect.toString();
    }
}

package com.huitool.data.mybatis.plugins.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * <描述信息>
 */
@Getter
@ToString
@AllArgsConstructor
public class DataPage<T>{
    private final long total;
    private final List<T> rows;
}

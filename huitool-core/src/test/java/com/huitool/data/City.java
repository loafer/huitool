package com.huitool.data;

import com.huitool.data.mybatis.plugins.jdbc.Table;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

/**
 * <描述信息>
 */
@Data
@Table("city")
public class City {
    @Id
    private Long id;
    private String name;
    private String state;
    private String country;
    @Transient
    private String remark;
}

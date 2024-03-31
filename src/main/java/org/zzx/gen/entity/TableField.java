package org.zzx.gen.entity;

import lombok.Data;

/**
 * 表字段
 */
@Data
public class TableField {
    /**
     * 字段名
     */
    private String columnName;

    /**
     * 字段类型
     */
    private String columnType;

    /**
     * 是否主键
     */
    private Boolean isPrimaryKey;

    /**
     * 是否自增id
     */
    private Boolean isAutoIncrement;

    /**
     * 字段注释
     */
    private MetaInfo metaInfo;




}

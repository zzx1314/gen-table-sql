package org.zzx.gen.entity;

import lombok.Data;

import java.util.List;

@Data
public class TableInfo {
    /**
     * 表名
     */
    private String tableName;

    /**
     * 字段
     */
    private List<TableField> tableFields;

    /**
     * 表注释
     */
    private String tableComment;
}

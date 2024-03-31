package org.zzx.gen.entity;

import lombok.Data;

/**
 * 表字段元数据
 */
@Data
public class MetaInfo {
    /**
     * 长度
     */
    private int length;

    /**
     * 是否为空
     */
    private Boolean nullable;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 默认值
     */
    private Object defaultValue;

}

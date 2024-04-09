package org.zzx.gen.entity;

import lombok.Data;

/**
 * 数据库信息
 */
@Data
public class DbInfo {
    private static final long serialVersionUID = 1L;
    private String driver;
    private String url;
    private String userName;
    private String password;
    private String dbName;
}

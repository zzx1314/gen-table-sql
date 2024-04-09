DROP TABLE IF EXISTS `${tableName}`;
CREATE TABLE `${tableName}`
(
<#list tableFields as field>
    `${field.columnName}` ${field.columnType}(${field.metaInfo.length}) <#if field.metaInfo.nullable> NULL<#else> NOT NULL</#if> <#if field.isAutoIncrement> AUTO_INCREMENT</#if><#if field.isPrimaryKey> PRIMARY KEY</#if> COMMENT '${field.metaInfo.remarks}'<#if field_has_next>,</#if>
</#list>
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '${tableComment}' ROW_FORMAT = Dynamic;
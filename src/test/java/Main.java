import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.zzx.gen.entity.DbInfo;
import org.zzx.gen.entity.MetaInfo;
import org.zzx.gen.entity.TableField;
import org.zzx.gen.entity.TableInfo;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws Exception {
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName("sys_user");
        tableInfo.setTableComment("用户表");

        TableField tableField = new TableField();
        tableField.setColumnName("id");
        tableField.setColumnType("int");
        tableField.setIsPrimaryKey(true);
        tableField.setIsAutoIncrement(true);

        MetaInfo metaInfo = new MetaInfo();
        metaInfo.setLength(11);
        metaInfo.setNullable(false);
        metaInfo.setRemarks("id");
        metaInfo.setDefaultValue(null);
        tableField.setMetaInfo(metaInfo);


        List<TableField> tableFields = new ArrayList<>();
        tableFields.add(tableField);

        tableInfo.setTableFields(tableFields);
        gen(tableInfo, "table.sql.ftl");
    }

    /**
     * @param tableInfo        传递到模板中的数据
     * @param useTemplateName 模版名称
     */
    private static void gen(TableInfo tableInfo, String useTemplateName) throws Exception {
        final String ENCODING = "UTF-8";

        // 指定模板存放的路径
        TemplateLoader loader = new FileTemplateLoader(new File(getTemplatePath(useTemplateName)));
        // 指定配置
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(loader);
        cfg.setEncoding(Locale.getDefault(), ENCODING);

        // 指定使用的模板
        Template template = cfg.getTemplate(useTemplateName, ENCODING);
        template.setEncoding(ENCODING);

        // 生成 Java 代码
        /*OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(tableInfo.getTableName() + ".sql"), ENCODING);
        template.process(tableInfo, outputStreamWriter);*/
        Writer out = new StringWriter();
        template.process(tableInfo, out);
        out.flush();
        System.out.println(out.toString());

        DbInfo dbInfo = new DbInfo();
        dbInfo.setDriver("com.mysql.cj.jdbc.Driver");
        dbInfo.setUrl("jdbc:mysql://127.0.0.1:3306/th_test?characterEncoding=utf8&useUnicode=true&useSSL=false&serverTimezone=GMT%2B8");
        dbInfo.setUserName("root");
        dbInfo.setPassword("123456");

        Class.forName(dbInfo.getDriver()).newInstance();
        Connection conn = DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUserName(), dbInfo.getPassword());
        Statement statement = conn.createStatement();

        List<String> sqlList= Arrays.asList(out.toString().split(";\\n"));
        for (String sql : sqlList){
            int i = statement.executeUpdate(sql);
        }
        statement.close();
        conn.close();

    }

    /**
     * 获取模版路径
     * @param fileName 文件名称
     * @return
     */
    private static String getTemplatePath(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceUrl = classLoader.getResource("templates/"+ fileName);
        String filePath = resourceUrl.getPath().substring(0, resourceUrl.getPath().lastIndexOf("/"));
        System.out.println(filePath);
        return filePath;
    }
}
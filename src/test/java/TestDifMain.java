import cn.hutool.core.io.FileUtil;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

import java.util.Arrays;
import java.util.List;

public class TestDifMain {
    public static void main(String[] args) {
        testOne();
    }

    public static void testOne(){
        List<String> one = FileUtil.readLines("D:\\171\\cs_integration.sql", "UTF-8");
        List<String> two = FileUtil.readLines("D:\\171\\cs_integration1.sql", "UTF-8");
        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(true)
                .oldTag(f -> "~")
                .newTag(f -> "**")
                .build();
        List<DiffRow> rows = generator.generateDiffRows(
                one,
                two);

        System.out.println("|original|new|");
        System.out.println("|--------|---|");
        for (DiffRow row : rows) {
            System.out.println("|" + row.getOldLine() + "|" + row.getNewLine() + "|");
        }
    }
}

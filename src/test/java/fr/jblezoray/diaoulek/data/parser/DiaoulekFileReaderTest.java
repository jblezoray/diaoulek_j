package fr.jblezoray.diaoulek.data.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.List;

public class DiaoulekFileReaderTest {

    private static Charset UTF8 = Charset.forName("UTF-8");

    @Test
    public void nominalTest() throws DataException {
        // having
        String lines = "A\nB\nC\nA\nB";

        // when
        DiaoulekFileReader dfr = new DiaoulekFileReader(lines.getBytes(), UTF8);
        List<String> group1 = dfr.readLinesUntilNextline(line -> "A".equals(line));
        List<String> group2 = dfr.readLinesUntilNextline(line -> "A".equals(line));
        List<String> group3 = dfr.readLinesUntilNextline(line -> "A".equals(line));

        // then
        Assertions.assertEquals("ABC", coalesce(group1));
        Assertions.assertEquals("AB", coalesce(group2));
        Assertions.assertEquals("", coalesce(group3));
    }

    private static String coalesce(List<String> list) {
        return list.stream().reduce("", (a,b) -> a+(b==null?"":b));
    }

    @Test
    public void ignoreLines() throws DataException {
        // having
        String lines = ".\n.\n.\n-\nA\n.\nB\n.\n-\n.\n.\nB\n.\n.\n.\n-\nC";

        // when
        DiaoulekFileReader dfr = new DiaoulekFileReader(lines.getBytes(), UTF8);
        dfr.setIgnore(line -> ".".equals(line));
        List<String> group1 = dfr.readLinesUntilNextline(line -> "-".equals(line));
        List<String> group2 = dfr.readLinesUntilNextline(line -> "-".equals(line));
        List<String> group3 = dfr.readLinesUntilNextline(line -> "-".equals(line));

        // then
        Assertions.assertEquals("-AB", coalesce(group1));
        Assertions.assertEquals("-B", coalesce(group2));
        Assertions.assertEquals("-C", coalesce(group3));
    }

    @Test
    public void mergeLines() throws DataException {
        // having
        String lines = "A\n B\n C\nD\n-\nA\n B\n-\n A\n B\n";

        // when
        DiaoulekFileReader dfr = new DiaoulekFileReader(lines.getBytes(), UTF8);
        dfr.setMergeLineWithPreviousIf(line -> line.startsWith(" "));
        List<String> group1 = dfr.readLinesUntilNextline(line -> line.startsWith("-"));
        List<String> group2 = dfr.readLinesUntilNextline(line -> line.startsWith("-"));
        List<String> group3 = dfr.readLinesUntilNextline(line -> line.startsWith("-"));

        // then
        Assertions.assertEquals("A B C", group1.get(0));
        Assertions.assertEquals("D", group1.get(1));
        Assertions.assertEquals("-", group2.get(0));
        Assertions.assertEquals("A B", group2.get(1));
        Assertions.assertEquals("- A B", group3.get(0));
    }

}

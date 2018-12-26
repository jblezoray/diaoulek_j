package fr.jblezoray.diaoulek.data.parser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Predicate;

/**
 * A parser that :
 * - ignores comment lines that starts by a '!'
 * - reads the file alias in the first line
 * - ignores empty lines
 * - can read line by line
 * - can read multiple of lines until a line matches a condition.
 */
public class DiaoulekFileReader implements Closeable  {

    private final BufferedReader br;
    private Queue<String> foreSeenLines;
    private final static Predicate<String> HEADER_LINE = l -> l.startsWith("!#");

    private Predicate<String>[] linesToIgnore = null;
    private Predicate<String>[] linesToMerge = null;


    public DiaoulekFileReader(byte[] fileContent, Charset charset) throws DataException {
        String fileContentString = new String(fileContent, charset);
        this.br = new BufferedReader(new StringReader(fileContentString));
        this.foreSeenLines = new ArrayDeque<>();
    }

    @Override
    public void close() throws IOException {
        this.br.close();
    }


    public void setIgnore(Predicate<String>... linesToIgnore) {
        this.linesToIgnore = linesToIgnore;
    }

    public void setMergeLineWithPreviousIf(Predicate<String>... linesToMerge) {
        this.linesToMerge = linesToMerge;
    }

    public String readFileAlias() throws DataException{
        // DOC: La première ligne de la leçon doit commencer par les
        // signes « !# » suivis d'un espace et de l'alias de la leçon.
        String fileAlias;
        try {
            String line = br.readLine();
            if (!HEADER_LINE.test(line))
                throw new DataException(line);
            fileAlias = line.substring(2).trim();
        } catch (IOException e) {
            throw new DataException("Cannot read header", e);
        }
        return fileAlias;
    }

    public String readNextLine(Predicate<String> predicate) throws DataException {
        String line;
        do {
            line = readNextLine();
        } while (line!=null && !predicate.test(line));
        return line;
    }


    public String readNextLine() throws DataException {
        String line ;
        try {
            line = this.foreSeenLines.peek()==null ?
                    br.readLine()
                    : this.foreSeenLines.poll();

            // does this line must be ignored?
            if (mustBeIgnored(line))
                line = readNextLine();

            // does the next line must be merged with this one ?
            ensureAtLeast1lineInTheForeseenLines();
            while (mustBeMerged(this.foreSeenLines.peek())) {
                line = line + this.foreSeenLines.poll();
                ensureAtLeast1lineInTheForeseenLines();
            }
        } catch (IOException e) {
            throw new DataException("Cannot read lines", e);
        }
        return line;
    }


    private boolean mustBeIgnored(String line) {
        boolean mustBeIgnored = false;
        if (line != null && this.linesToIgnore!=null) {
            for (Predicate<String> lineCondition : this.linesToIgnore) {
                if (lineCondition.test(line)) {
                    mustBeIgnored = true;
                    break;
                }
            }
        }
        return mustBeIgnored;
    }

    private boolean mustBeMerged(String line) {
        boolean mustBeMerged = false;
        if (line!= null && this.linesToMerge!=null) {
            for (Predicate<String> lineCondition : this.linesToMerge) {
                if (lineCondition.test(line)) {
                    mustBeMerged = true;
                    break;
                }
            }
        }
        return mustBeMerged;
    }


    private void ensureAtLeast1lineInTheForeseenLines() throws IOException {
        if (this.foreSeenLines.size()==0) {
            String line;
            do {
                line = br.readLine();
            } while (mustBeIgnored(line));
            if (line!=null)
                this.foreSeenLines.add(line);
        }
    }


    public List<String> readLinesUntilNextline(Predicate<String> nextLineCondition)
            throws DataException {
        List<String> lines = new ArrayList<>();
        boolean nextLineConditionReached = false;
        lines.add(readNextLine());
        do {
            try {
                ensureAtLeast1lineInTheForeseenLines();
            } catch (IOException e) {
                throw new DataException("Cannot read lines", e);
            }

            String nextLine = this.foreSeenLines.peek();
            if (nextLine==null || nextLineCondition.test(nextLine)) {
                nextLineConditionReached=true;
            } else {
                lines.add(readNextLine());
            }
        } while (!nextLineConditionReached);
        return lines;
    }


}

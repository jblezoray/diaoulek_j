package fr.jblezoray.diaoulek.data.parser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
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
    private String foreSeenLine = null;
    public final static Predicate<String> EMPTY_LINE = l -> l.trim().length()==0;
    public final static Predicate<String> COMMENT_LINE = l -> l.startsWith("!");
    public final static Predicate<String> HEADER_LINE = l -> l.startsWith("!#");

    public DiaoulekFileReader(byte[] fileContent, Charset charset) throws DataException {
        String fileContentString = new String(fileContent, charset);
        this.br = new BufferedReader(new StringReader(fileContentString));
    }

    @Override
    public void close() throws IOException {
        this.br.close();
    }

    public String readFileAlias() throws DataException{
        // DOC: La première ligne de la leçon doit commencer par les
        // signes « !# » suivis d'un espace et de l'alias de la leçon.
        String fileAlias;
        try {
            String line = getNextLine();
            if (!HEADER_LINE.test(line))
                throw new DataException(line);
            fileAlias = line.substring(2).trim();
        } catch (IOException e) {
            throw new DataException("Cannot read header", e);
        }
        return fileAlias;
    }

    private String getNextLine() throws IOException {
        String line ;
        if (this.foreSeenLine==null) {
            line = br.readLine();
        } else {
            line = this.foreSeenLine;
            this.foreSeenLine = null;
        }
        return line;
    }


    public List<String> readLinesUntil(Predicate<String> continuationCondition)
            throws DataException {
        List<String> lines = new ArrayList<>();
        try {
            String line;
            while ((line = getNextLine()) != null && continuationCondition.test(line))
                lines.add(line);
        } catch (IOException e) {
            throw new DataException("Cannot read lines", e);
        }
        return lines;
    }

    public List<String> readLinesUntilNextline(Predicate<String> nextLineCondition)
            throws DataException {
        List<String> lines = new ArrayList<>();
        try {
            String readline;
            while ((readline = getNextLine()) != null) {
                if (nextLineCondition.test(readline)) {
                    this.foreSeenLine = readline;
                    break;
                } else {
                    lines.add(readline);
                }
            }
        } catch (IOException e) {
            throw new DataException("Cannot read lines", e);
        }
        return lines;
    }

    public String readNextLine(Predicate<String>... lineConditions)
            throws DataException {
        String line;
        try {
            while ((line=getNextLine())!= null) {
                boolean lineMatches = true;
                for (Predicate<String> lineCondition : lineConditions) {
                    lineMatches = lineMatches && lineCondition.test(line);
                }
                if (lineMatches) break;
            }
        } catch (IOException e) {
            throw new DataException("Cannot read lines", e);
        }
        return line;
    }


}

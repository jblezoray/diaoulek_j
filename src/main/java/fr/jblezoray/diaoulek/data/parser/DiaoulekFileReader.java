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

    public DiaoulekFileReader(byte[] fileContent, Charset charset) throws DataException {
        String fileContentString = new String(fileContent, charset);
        this.br =  new BufferedReader(new StringReader(fileContentString));
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
            String line = br.readLine();
            if (!line.startsWith("!#"))
                throw new DataException(line);
            fileAlias = line.substring(2).trim();
        } catch (IOException e) {
            throw new DataException("Cannot read header", e);
        }
        return fileAlias;
    }

    public String readLine() throws DataException {
        return skipToLine(l ->
                // DOC: Les lignes commençant par le signe « ! » sont des
                // commentaires et sont ignorées.
                !l.startsWith("!")
                // Empty line to ignore.
                && !(l.trim().length()==0));
    }


    public List<String> readLinesUntil(Predicate<String> continuationCondition)
            throws DataException {
        List<String> lines = new ArrayList<>();
        try {
            String line;
            while ((line = br.readLine()) != null && continuationCondition.test(line))
                lines.add(line);
        } catch (IOException e) {
            throw new DataException("Cannot read lines", e);
        }
        return lines;
    }

    public String skipToLine(Predicate<String> lineCondition)
            throws DataException {
        String line;
        try {
            while ((line = br.readLine()) != null && !lineCondition.test(line));
        } catch (IOException e) {
            throw new DataException("Cannot read lines", e);
        }
        return line;
    }


}

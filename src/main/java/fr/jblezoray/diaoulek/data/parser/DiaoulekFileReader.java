package fr.jblezoray.diaoulek.data.parser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DiaoulekFileReader implements Closeable  {

    private final BufferedReader br;
    private final String fileAlias;

    public DiaoulekFileReader(byte[] fileContent, Charset charset) throws DataException {
        String fileContentString = new String(fileContent, charset);
        this.br =  new BufferedReader(new StringReader(fileContentString));

        // DOC: La première ligne de la leçon doit commencer par les
        // signes « !# » suivis d'un espace et de l'alias de la leçon.
        try {
            String line = br.readLine();
            if (!line.startsWith("!#")) throw new DataException(line);
            this.fileAlias = line.substring(2).trim();
        } catch (IOException e) {
            throw new DataException("Cannot read header", e);
        }
    }

    @Override
    public void close() throws IOException {
        this.br.close();
    }

    public String getFileAlias() {
        return this.fileAlias;
    }

    public String readLine() throws DataException {
        String line = null;
        try {
            line = br.readLine();
        } catch (IOException e) {
            throw new DataException("Cannot read line", e);
        }

        // EOF
        if (line == null) return null;

        // DOC: Les lignes commençant par le signe « ! » sont des
        // commentaires et sont ignorées.
        if (line.startsWith("!")) return readLine();

        // Empty line to ignore.
        if (line.trim().length()==0) return readLine();

        return line;
    }


    public List<String> readLinesUntil(Predicate<String> continuationCondition)
            throws DataException {
        List<String> lines = new ArrayList<>();
        String line;
        try {
            while ((line = br.readLine()) != null && continuationCondition.test(line))
                lines.add(line);
        } catch (IOException e) {
            throw new DataException("Cannot read lines", e);
        }
        return lines;
    }


}

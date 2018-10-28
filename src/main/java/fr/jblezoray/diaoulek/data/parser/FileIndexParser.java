package fr.jblezoray.diaoulek.data.parser;

import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileIndexParser implements IParser<List<FileIndexEntry>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileIndexParser.class);

    private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy";

    private final Charset charset;

    public FileIndexParser(Charset charset) {
        this.charset = charset;
    }

    @Override
    public boolean seemsParseable(FileIndexEntry fileIndexEntry) {
        return true;
    }

    @Override
    public List<FileIndexEntry> parse(byte[] fileContent, FileIndexEntry unused) throws DataException {

        String fileContentString = new String(fileContent, charset);

        List<FileIndexEntry> entries = new ArrayList<>();
        try (BufferedReader br =  new BufferedReader(new StringReader(fileContentString))) {

            String line;
            while ((line = br.readLine()) != null) {
                // find a line that starts by '#'
                if (!line.startsWith("#")) continue;

                // read a filename;
                String filename = line.replaceFirst("#", "");

                // read filetime on next line.
                line = br.readLine();
                if (!line.startsWith("filetime=")) throw new DataException(line);
                String dateStr = line.replaceFirst("filetime=", "");
                Date date = parseDate(dateStr);

                // read file size
                line = br.readLine();
                if (!line.startsWith("filesize=")) throw new DataException(line);
                Integer sizeStr = parseInteger(line);

                // read MD5 value.
                line = br.readLine();
                if (!line.startsWith("MD5")) throw new DataException(line);
                String[] split = line.split("=");
                if (split.length != 2) throw new DataException(line);
                String md5 = split[1].trim();

                // the entry is completed.
                FileIndexEntry entry = new FileIndexEntry();
                entry.setFilename(filename);
                entry.setFiletimeDate(date);
                entry.setFilesize(sizeStr);
                entry.setMd5(md5);
                entries.add(entry);
            }

        } catch (IOException e) {
            throw new DataException("Cannot read file.", e);
        }
        return entries;
    }


    private Integer parseInteger(String stringWithAnInteger) throws DataException {
        String integerString = stringWithAnInteger.replaceAll("[^\\d.]", "");
        if (integerString.length()==0)
            throw new DataException(integerString);

        try {
            return Integer.parseInt(integerString);
        } catch (NumberFormatException nfe) {
            throw new DataException(stringWithAnInteger);
        }
    }


    private Date parseDate(String filetime) throws DataException {
        Date date = null;

        DateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        try {
            date = df.parse(filetime);
        } catch (ParseException e) {
            throw new DataException(filetime);
        }

        return date;
    }
}

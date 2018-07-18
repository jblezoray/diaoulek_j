package fr.jblezoray.diaoulek.online;

import fr.jblezoray.diaoulek.online.model.OnlineUpdateEntry;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnlineUpdate {

  private static final Logger LOGGER = Logger.getLogger(OnlineUpdate.class.getName());

  private static final Pattern PATTERN  = Pattern.compile(
          "#(?<filename>[\\p{Alnum}\\.\\+\\-_]*)\n" +
          "filetime=(?<filetime>[A-Za-z0-9 :]*)\n" +
          "filesize= (?<filesize>[0-9]*) bytes\n" +
          "MD5 \\([\\p{Alnum}\\.\\+\\-_]*\\) = (?<md5>[A-Za-z0-9]*)\n"
  );

  private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy";


  public List<OnlineUpdateEntry> readIndex(URI indexUri) throws IOException {
    String fileContent = Request.Get(indexUri).execute().returnContent().asString() ;
    return parseIndex(fileContent);
  }

  List<OnlineUpdateEntry> parseIndex(String fileContent) {
    Matcher matcher = PATTERN.matcher(fileContent);
    List<OnlineUpdateEntry> entries = new ArrayList<>();

    while (matcher.find()) {
      String filename = matcher.group("filename");
      String filetime = matcher.group("filetime");
      String filesize = matcher.group("filesize");
      String md5 = matcher.group("md5");
      Date filetimeDate;
      try {
        filetimeDate = parseDate(filetime);
      } catch (ParseException pe) {
        System.err.println("Cannot parse date '"+filetime+"', ignoring entry.");
        pe.printStackTrace();
//        LOGGER.warning("Cannot parse date '{}', ignoring entry. ", filetime);
        continue;
      }
      entries.add(new OnlineUpdateEntry(filename, filesize, md5, filetimeDate));
    }

    return entries;
  }


  private Date parseDate(String filetime) throws ParseException {
    DateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    return df.parse(filetime);
  }

}

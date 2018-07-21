package fr.jblezoray.diaoulek.online;

import fr.jblezoray.diaoulek.Config;
import fr.jblezoray.diaoulek.online.model.Language;
import fr.jblezoray.diaoulek.online.model.Lesson;
import fr.jblezoray.diaoulek.online.model.OnlineUpdateEntry;
import org.apache.http.client.fluent.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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

  public Lesson readLesson(OnlineUpdateEntry onlineUpdateEntry) throws IOException {
    String uri = Config.URL_UPDATE+onlineUpdateEntry.getFilename();
    String fileContent = Request.Get(uri).execute().returnContent().asString() ;
    return parseLesson(fileContent, onlineUpdateEntry);
  }

  Lesson parseLesson(String fileContent, OnlineUpdateEntry onlineUpdateEntry) throws IOException {
    Lesson l = new Lesson(onlineUpdateEntry);
    parseLessonMetadata(l, fileContent);
    parseLessonMetadata(l, fileContent);
    return l;
  }

  private Date parseDate(String filetime) throws ParseException {
    DateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
    return df.parse(filetime);
  }

  private void parseLessonMetadata(Lesson l, String fileContent) throws IOException {

    try (BufferedReader br =  new BufferedReader(new StringReader(fileContent))) {
      String line = br.readLine();
      if (line.startsWith("!#")) { // e.g., "!# K17"
        l.setAlias(line.substring(2).trim());

      } else if (line.startsWith("!QR")) { // e.g., "!QR  Q=br  R=fr"
        for (String split : line.split(" ")) {
          String trimed = split.trim();
          Optional<Language> optL = trimed.length()>=2 ?
                  Language.getLanguageFor(trimed.substring(2))
                  : Optional.empty();
          if (!optL.isPresent()) {
            continue;
          }
          if (trimed.startsWith("Q=")) {
            l.setLanguageForQuestions(optL.get());
          } else if (trimed.startsWith("R=")) {
            l.setLanguageForResponses(optL.get());
          }
        }
      }
    }
  }

}

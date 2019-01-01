package fr.jblezoray.diaoulek.userinterface;

import com.google.common.base.Strings;
import fr.jblezoray.diaoulek.core.AnswerAnalyser;
import fr.jblezoray.diaoulek.core.DiaoulekService;
import fr.jblezoray.diaoulek.core.LessonCategoryBuilder;
import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import fr.jblezoray.diaoulek.data.model.LessonCategory;
import fr.jblezoray.diaoulek.data.model.LessonEntry;
import fr.jblezoray.diaoulek.data.model.Part;
import fr.jblezoray.diaoulek.data.model.analysis.AnswerAnalysis;
import fr.jblezoray.diaoulek.data.model.lessonelement.LessonElement;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;
import fr.jblezoray.diaoulek.data.model.lessonelement.Text;
import fr.jblezoray.diaoulek.data.model.lessonelement.WordReference;
import fr.jblezoray.diaoulek.data.model.lessonelement.lesson.LessonTextLine;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.Question;
import fr.jblezoray.diaoulek.data.parser.DataException;
import fr.jblezoray.diaoulek.data.scrapper.FileRetrieverException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandLineUserInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineUserInterface.class);
    private static final int LINE_WIDTH = 120;

    private final DiaoulekService diaoulekService;
    private final SoundPlayer soundPlayer;
    private final Terminal terminal;
    private final PrintWriter pw;


    public CommandLineUserInterface(DiaoulekService diaoulekService,
                                    PrintStream ps,
                                    InputStream is,
                                    SoundPlayer soundPlayer) throws IOException {
        this.diaoulekService = diaoulekService;
        this.soundPlayer = soundPlayer;
        this.terminal = TerminalBuilder.builder()
                .system(true)
                .streams(is, ps)
                .build();
        this.pw = this.terminal.writer();
    }


    public void start() {
        try {
            while(true) {
                printTitle("Lesson categories");
                LessonCategory lc = chooseLessonCategory();
                printTitle("Lessons");
                FileIndexEntry fie = chooseLesson(lc);
                LessonEntry lesson = this.diaoulekService.getLesson(fie);
                for (LessonElement le : lesson.getLessonElements()) {
                    if (le instanceof Text) {
                        printTitle("Lesson");
                        printText((Text) le);

                    } else if (le instanceof WordReference) {
//                        printTitle("Question");
                        // TODO

                    } else if (le instanceof QRCouple) {
                        printTitle("Question");
                        printQuestion((QRCouple)le);
                        String answer = this.read("translate");

                        AnswerAnalyser analyser = new AnswerAnalyser((QRCouple)le);
                        AnswerAnalysis analysis = analyser.analyze(answer);
                        printAnalysis(analysis);
                    }
                }
            }
        } catch (FileRetrieverException e) {
            LOGGER.error("Cannot get file", e);

        } catch (DataException e) {
            LOGGER.error("Bad file", e);

        } catch (IOException e) {
            LOGGER.error("An I/O error occured", e);
        }
    }

    private void printQuestion(QRCouple qr) {
        Question q = qr.getQuestion();
        for (Part p : q.getParts()) {
            String phrase = p.getRawString();
            this.pw.print("                    ");
            this.pw.println(phrase);
        }
    }

    private void printAnalysis(AnswerAnalysis aa) {
        this.pw.println("expected response : " + aa.getExpectedResponse());
        this.pw.println("      (tokenized) : " + aa.getExpectedResponseTokenized());
        this.pw.println("  phrase accuracy : " + aa.getAnswerAccuracy());
        this.pw.println("   words accuracy : " + aa.getInputWordsAccuracy());
        this.pw.println("      (tokenized) : " + aa.getInputWordsTokenized());
        this.pw.println(new AnswerAnalysisRenderer(aa).render());
    }


    private LessonCategory chooseLessonCategory()
            throws DataException, FileRetrieverException, IOException {
        List<FileIndexEntry> files = diaoulekService.streamAllLessons()
                .collect(Collectors.toList());
        LessonCategoryBuilder lcb = new LessonCategoryBuilder(files);
        List<String> keys = new ArrayList<>();
        for (LessonCategory lc  : lcb.getLessonsCategories()) {
            keys.add(lc.getKey());
            this.pw.printf("  - %3s : %-20s %3d lesson%1s   %s",
                    lc.getKey(),
                    lc.getFullName(),
                    lc.getCount(),
                    lc.getCount()==1 ? "" : "s",
                    lc.getDescription());
            this.pw.println();
        }
        String chosenLessonKey = this.read("Choose a lesson category", keys.toArray(new String[0]));
        Optional<LessonCategory> olc = lcb.findFromKey(chosenLessonKey);
        if (! olc.isPresent()) {
            this.pw.println("Unknown lesson category.");
            return chooseLessonCategory();// recursive call.
        }
        return olc.get(); // final choice
    }



    private FileIndexEntry chooseLesson(LessonCategory lc)
            throws DataException, FileRetrieverException, IOException {
        List<FileIndexEntry> fies = this.diaoulekService
                .streamLessonsWithin(lc)
                .collect(Collectors.toList());
        int maxFilenameLength = fies.stream()
                .map(FileIndexEntry::getFilename)
                .map(String::length)
                .max(Integer::compareTo)
                .orElseThrow(DataException::new);
        int spacer = 5;
        int widthPerFile = maxFilenameLength + spacer;
        int terminalWidth = getTerminalWidth();
        int howManyPerLine = (terminalWidth - spacer) / widthPerFile;
        for (int i=0; i<fies.size(); i+=howManyPerLine) {
            for (int j=i; j<fies.size() && j<i+howManyPerLine; j++) {
                String filename = fies.get(j).getFilename();
                this.pw.printf("%"+(widthPerFile)+"s", filename);
            }
            this.pw.println();
        }
        String[] suggestions = fies.stream()
                .map(FileIndexEntry::getFilename)
                .toArray(String[]::new);
        String chosen = this.read("Choose a Lesson", suggestions);
        for (FileIndexEntry fie : fies)
            if (fie.getFilename().equalsIgnoreCase(chosen))
                return fie; // final choice.
        return chooseLesson(lc); // recursive call.
    }

    private int getTerminalWidth() {
        int terminalWidth = this.terminal.getWidth();
        return terminalWidth==0 ? LINE_WIDTH : terminalWidth;
    }


    private String read(String inquerry, String... completions) {
        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new StringsCompleter(completions))
                .build();
        return lineReader
                .readLine(AnsiEscapeColors.BLUE + inquerry + " > " + AnsiEscapeColors.RESET)
                .trim();
    }


    private void printText(Text le) throws IOException, FileRetrieverException {
        int width = (getTerminalWidth() -9) / 2;

        for (LessonTextLine line : le.getText()) {
            if (line.getLine().length() > 0) {
                this.pw.printf("  %-"+width+"s  |  %-"+width+"s  ",
                        Strings.nullToEmpty(line.getLine()),
                        Strings.nullToEmpty(line.getTranslation()));
                this.pw.println();
                this.pw.flush();
            }

            if (line.getSound()!=null) {
                soundPlayer.playSound(line.getSound());
                NonBlockingReader reader = this.terminal.reader();
                // empty the inputstream.
                while (reader.available()!=0) reader.read();
                // wait for either a key pressed or the end of the playing of
                // the sound.
                while (reader.read(100)==-2 && !this.soundPlayer.isDone());
                this.soundPlayer.stop();
                // re-empty the input stream.
                while (reader.available()!=0) reader.read();
            }
        }
    }


    private void printTitle(String title) {

        int len = title.length();
        int fillerLen = ((getTerminalWidth()-len) / 2) - 4;

        StringBuilder fillerBuilder = new StringBuilder();
        for (int i=0; i<fillerLen; i++) fillerBuilder.append("=");
        String filler = fillerBuilder.toString();

        this.pw.print(AnsiEscapeColors.BLUE);
        this.pw.printf("%s  %s  %s", filler , title, filler);
        this.pw.print(AnsiEscapeColors.RESET);
        this.pw.println();
        this.pw.flush();
    }
}



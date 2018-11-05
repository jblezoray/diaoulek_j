package fr.jblezoray.diaoulek.userinterface;

import fr.jblezoray.diaoulek.core.DiaoulekService;
import fr.jblezoray.diaoulek.core.LessonCategoryBuilder;
import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import fr.jblezoray.diaoulek.data.model.LessonCategory;
import fr.jblezoray.diaoulek.data.model.LessonEntry;
import fr.jblezoray.diaoulek.data.model.Part;
import fr.jblezoray.diaoulek.data.model.lessonelement.LessonElement;
import fr.jblezoray.diaoulek.data.model.lessonelement.QRCouple;
import fr.jblezoray.diaoulek.data.model.lessonelement.Text;
import fr.jblezoray.diaoulek.data.model.lessonelement.WordReference;
import fr.jblezoray.diaoulek.data.model.lessonelement.qrcouple.Question;
import fr.jblezoray.diaoulek.data.parser.DataException;
import fr.jblezoray.diaoulek.data.scrapper.FileRetrieverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommandLineUserInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineUserInterface.class);

    private final DiaoulekService diaoulekService;
    private final PrintStream ps;
    private final InputStream is;

    public CommandLineUserInterface(DiaoulekService diaoulekService, PrintStream ps, InputStream is) {
        this.diaoulekService = diaoulekService;
        this.is = is;
        this.ps = ps;
    }


    public void start() {
        try {
            while(true) {
                LessonCategory lc = chooseLessonCategory();
                FileIndexEntry fie = chooseLesson(lc);
                LessonEntry lesson = this.diaoulekService.getLesson(fie);
                for (LessonElement le : lesson.getLessonElements()) {
                    if (le instanceof Text) {
                        printText((Text) le);

                    } else if (le instanceof WordReference) {
                        // TODO

                    } else if (le instanceof QRCouple) {
                        printQuestion((QRCouple)le);
                        this.read("translate : ");
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
        this.ps.println("Word / expression :");
        Question q = qr.getQuestion();
        for (Part p : q.getParts()) {
            String[] phrases = p.getPhrases();
            for (String phrase : phrases) {
                this.ps.print(phrase);
                this.ps.print(" ; ");
            }
            this.ps.println();
        }
    }


    private LessonCategory chooseLessonCategory()
            throws DataException, FileRetrieverException, IOException {
        this.ps.println("Lesson categories");
        List<FileIndexEntry> files = diaoulekService.streamAllLessons()
                .collect(Collectors.toList());
        LessonCategoryBuilder lcb = new LessonCategoryBuilder(files);
        for (LessonCategory lc  : lcb.getLessonsCategories()) {
            String line = String.format("  - %3s : %-20s %3d lesson%1s   %s",
                    lc.getKey(),
                    lc.getFullName(),
                    lc.getCount(),
                    lc.getCount()==1 ? "" : "s",
                    lc.getDescription());
            this.ps.println(line);
        }
        String chosenLessonKey = this.read("Choose a lesson category");
        Optional<LessonCategory> olc = lcb.findFromKey(chosenLessonKey);
        if (! olc.isPresent()) {
            this.ps.println("Unknown lesson category.");
            return chooseLessonCategory();// recursive call.
        }
        return olc.get(); // final choicef
    }



    private FileIndexEntry chooseLesson(LessonCategory lc)
            throws DataException, FileRetrieverException, IOException {
        this.ps.println("Lessons : ");
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
        int howManyPerLine = (80 - spacer) / widthPerFile;
        for (int i=0; i<fies.size(); i+=howManyPerLine) {
            for (int j=i; j<fies.size() && j<i+howManyPerLine; j++) {
                String filename = fies.get(j).getFilename();
                this.ps.print(String.format("%"+(widthPerFile)+"s", filename));
            }
            this.ps.println();
        }
        String chosen = this.read("Choose a Lesson");
        for (FileIndexEntry fie : fies)
            if (fie.getFilename().equalsIgnoreCase(chosen))
                return fie; // final choice.
        return chooseLesson(lc); // recursive call.
    }



    private String read(String inquerry) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(this.is));
        this.ps.print(inquerry);
        this.ps.print("> ");
        return br.readLine().trim();
    }

    private void printText(Text le) {
        this.ps.println(le.getText());
    }

}



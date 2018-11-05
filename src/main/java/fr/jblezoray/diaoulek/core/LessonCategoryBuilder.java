package fr.jblezoray.diaoulek.core;

import com.google.common.collect.ImmutableList;
import fr.jblezoray.diaoulek.data.model.FileIndexEntry;
import fr.jblezoray.diaoulek.data.model.LessonCategory;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class LessonCategoryBuilder {

    /**
     * Known categories.
     * From http://furchhadiaoulek.free.fr/Diaoulek-lessons-bf.html
     */
    private static List<LessonCategory> BASE_LESSON_CATEGORIES = ImmutableList
            .<LessonCategory>builder()
            .add(new LessonCategory(
                    "^ee-\\d+.txt$",
                    "EE",
                    "Eeun-Eeun",
                    "Leçons complètes, texte, explications et vocabulaire"))
            .add(new LessonCategory(
                    "^ke-\\d+.txt$",
                    "KE",
                    "Kentelioù-Eeun",
                    "Mots courants"))
            .add(new LessonCategory(
                    "^k\\d+.txt$",
                    "K",
                    "Kentelioù",
                    "Vocabulaire en breton traditionnel"))
            .add(new LessonCategory(
                    "^kk\\d+-[123].txt$",
                    "KK",
                    "Kentelioù-Kentelioù",
                    "Vocabulaire en breton traditionnel"))
            .build();

    private final List<FileIndexEntry> fileIndexEntry;

    public LessonCategoryBuilder(List<FileIndexEntry> fileIndexEntry) {
        this.fileIndexEntry = fileIndexEntry;
    }

    public List<LessonCategory> getLessonsCategories() {
        return this.fileIndexEntry.stream()
                .map(fie -> {
                    String fn = fie.getFilename();
                    LessonCategory lc = findMatchInBaseLessonsCategories(fn);
                    if (lc == null) lc = buildUnknownCategory(fn);
                    return lc;
                })
                .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
                // Map<LessonCategory, Long>
                .entrySet()
                .stream()
                .peek(e -> e.getKey().setCount(e.getValue()))
                .map(e -> e.getKey())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }


    /**
     * Searches for a match in the base lessons categories.
     * @param filename the filename to search.
     * @return a match, or null.
     */
    private static LessonCategory findMatchInBaseLessonsCategories(String filename) {
        LessonCategory lc = null;
        // exists in the base lesson categories ?
        for (LessonCategory base : BASE_LESSON_CATEGORIES) {
            if (filename.matches(base.getFilenameRegExp())) {
                lc = base;
                break;
            }
        }
        return lc;
    }


    private static LessonCategory buildUnknownCategory(String fn) {
        // regexp
        String regexp = "^"+Pattern.quote(fn)+"$";

        // remove file extension && keep only alphabetic characters
        if (fn.contains(".")) fn = fn.split("\\.")[0];
        fn = fn.replaceAll("[^\\p{Alpha}]", "");

        // build key.
        String key = fn.length()>=3 ? fn.substring(0, 3) : fn;
        key = key.toUpperCase();

        return new LessonCategory(regexp, key, fn, "");
    }


    public Optional<LessonCategory> findFromKey(String lessonKey) {
        return getLessonsCategories().stream()
                .filter(categ -> categ.getKey().equalsIgnoreCase(lessonKey))
                .findFirst();
    }
}

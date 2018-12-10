package fr.jblezoray.diaoulek.core;

import java.util.Arrays;
import java.util.List;

public class Utils {


    public static int maxSize(List<?> a, List<?> b) {
        return a.size()>b.size() ? a.size() : b.size();
    }

    public static int maxLength(String a, String b) {
        int al = a==null ? 0 : a.length();
        int bl = b==null ? 0 : b.length();
        return al>bl ? al : bl;
    }

    public static <E> E getOrNull(List<E> l, int index) {
        return  l.size()>index ? l.get(index) : null;
    }

    public <A> List<A> mkList(A... a) {
        return Arrays.asList(a);
    }


}

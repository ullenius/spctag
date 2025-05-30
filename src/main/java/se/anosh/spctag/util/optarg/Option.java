package se.anosh.spctag.util.optarg;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public record Option(String opt, String longOpt, String description) {

    static SortedSet<Option> of() {
        return new TreeSet<>(Comparator.comparing(Option::opt));
    }

}

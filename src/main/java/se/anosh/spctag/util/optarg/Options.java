package se.anosh.spctag.util.optarg;

import java.util.*;

public class Options {

    private final SortedSet<Option> options = Option.of();

    public Options addOption(String opt, String longOpt, boolean ignored, String description) { // TODO remove ignored
        options.add(new Option(opt, longOpt, description));
        return this;
    }

    public Collection<Option> options() {
        return options;
    }

}

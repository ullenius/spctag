package se.anosh.spctag.util.optarg;

import java.util.LinkedList;
import java.util.List;

public class Options {

    private List<Option> options = new LinkedList<>();

    public Options addOption(String opt, String longOpt, boolean ignored, String description) { // TODO remove ignored
        options.add(new Option(opt, longOpt, description));
        return this;
    }

    public List<Option> options() {
        return options;
    }

}

package se.anosh.spctag.util.optarg;

public final class HelpFormatter {

    public void printHelp(String message, Options options) {
        System.out.printf("usage: %s\n", message);
        for (Option option : options.options()) {
            System.out.printf(" -%s,--%s\t%s\n", option.opt(), option.longOpt(), option.description());
        }

    }
}

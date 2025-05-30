package se.anosh.spctag.util.optarg;

public class HelpFormatter {

    public void printHelp(String message, Options options) {
        System.out.printf("usage: %s\n", message);
        for (Option option : options.options()) {
            System.out.printf(" -%s,--%s\t\t%s\n", option.opt(), option.longOpt(), option.description());
        }
        /*
        usage: spctag <filename>
        -j,--json      output JSON
        -v,--verbose   verbose output
        -V,--version   print version
        -x,--xid6      print xid6 tags
         */

    }
}

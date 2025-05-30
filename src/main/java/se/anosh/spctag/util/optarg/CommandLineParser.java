package se.anosh.spctag.util.optarg;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CommandLineParser {

    public CommandLine parse(Options options, String[] arr) { //throws ParseException;
        List<Option> valid = options.options();
        List<Option> parsed = new LinkedList<>();
        List<String> argv = new LinkedList<>();

        Arrays.stream(arr)
                .distinct()
                .forEach(opt -> {
                    var found = valid.stream()
                            .filter(e -> e.opt().contentEquals(opt) || e.longOpt().contentEquals(opt))
                            .findAny();
                    found.ifPresentOrElse(parsed::add, () -> argv.add(opt));
                    }
                );
        return new CommandLine(parsed, argv);
    }

}

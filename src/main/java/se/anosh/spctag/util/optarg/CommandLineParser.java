package se.anosh.spctag.util.optarg;

import java.util.*;

public class CommandLineParser {

    public CommandLine parse(Options options, String[] arr) throws ParseException {
        Collection<Option> valid = options.options();
        SortedSet<Option> parsed = Option.of();
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
        if (parsed.isEmpty()) {
            throw new ParseException("No options found");
        }
        return new CommandLine(parsed, argv);
    }

}

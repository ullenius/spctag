package se.anosh.spctag.util.optarg;

import org.tinylog.Logger;

import java.util.*;

public class CommandLineParser {

    public CommandLine parse(Options options, String[] arr) throws ParseException {
        Collection<Option> valid = options.options();
        SortedSet<Option> parsed = Option.of();
        List<String> argv = new LinkedList<>();

        Arrays.stream(arr)
                .forEach(opt -> {
                    final int index = index(opt);
                        var found = valid.stream()
                                .filter(e -> e.opt().contentEquals(opt.substring(index)) || e.longOpt().contentEquals(opt.substring(index)))
                                .findAny();
                        if (found.isPresent()) {
                            parsed.add(found.orElseThrow());
                        } else {
                            argv.add(opt);
                        }
                    }
                );
        if (parsed.isEmpty()) {
            throw new ParseException("No options found");
        }
        return new CommandLine(parsed, argv);
    }
    private int index(String opt) {
        int index = 0;
        for (int i = 0; i < opt.length() && i < 2; i++) {
            if (opt.charAt(i) == '-') {
                index++;
            }
        }
        return index;
    }

}

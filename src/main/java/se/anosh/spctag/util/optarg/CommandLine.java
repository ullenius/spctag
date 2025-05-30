package se.anosh.spctag.util.optarg;

import java.util.List;
import java.util.Objects;

public class CommandLine {

    private final List<Option> parsed;
    private final List<String> argv;

    public CommandLine(List<Option> parsed, List<String> argv) {
        this.parsed = Objects.requireNonNull(parsed);
        this.argv = Objects.requireNonNull(argv);
    }

    public boolean hasOption(String opt) {
        return parsed.stream()
                .anyMatch(e -> e.opt().contentEquals(opt) || e.longOpt().contentEquals(opt));
    }

    public List<String> getArgList() {
        return argv;
    }

    public String[] getArgs() {
        return argv.toArray(String[]::new);
    }

}

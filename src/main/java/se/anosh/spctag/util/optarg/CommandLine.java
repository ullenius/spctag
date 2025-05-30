package se.anosh.spctag.util.optarg;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class CommandLine {

    private final Collection<Option> parsed;
    private final List<String> argv;

    public CommandLine(Collection<Option> parsed, List<String> argv) {
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

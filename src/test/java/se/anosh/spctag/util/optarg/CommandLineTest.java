package se.anosh.spctag.util.optarg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineTest {

    private static final List<String> ARGV = List.of("foo", "bar");

    private CommandLine uut;

    @BeforeEach
    void setup() {
        Options options = new Options();
        options.addOption("v", "verbose", false, "verbose output");
        options.addOption("h", "help", false, "print help");
        uut = new CommandLine(options.options(), ARGV);
    }

    @Test
    void hasOptionFound() {
        assertTrue(uut.hasOption("v"));
        assertTrue(uut.hasOption("verbose"));
        assertTrue(uut.hasOption("h"));
        assertTrue(uut.hasOption("help"));
    }

    @Test
    void unknownOptionsNotFound() {
        assertFalse(uut.hasOption("u"));
        assertFalse(uut.hasOption("unknown"));
    }

    @Test
    void getArgList() {
       assertEquals(ARGV, uut.getArgList());
    }

    @Test
    void getArgs() {
        String[] expected = ARGV.toArray(String[]::new);
        assertArrayEquals(expected, uut.getArgs());
    }


}

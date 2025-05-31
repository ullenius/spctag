package se.anosh.spctag.util.optarg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineParserTest {

    private static final String[] EXPECTED_ARGS = { "foo", "bar" };

    private CommandLineParser uut;

    private static final Options OPTIONS = new Options()
        .addOption("v", "verbose", false, "verbose output")
        .addOption("h", "help", false, "print help");

    @BeforeEach
    void setup() {
        uut = new CommandLineParser();
    }

    @Test
    void noOptionsAndArgsThrows() {
        String[] argv = { };
        assertThrows(ParseException.class, () -> uut.parse(OPTIONS, argv));
    }
    @Test
    void noOptionsWorks() {
        String[] argv = EXPECTED_ARGS;
        List<String> argvList = Arrays.stream(argv).toList();
        CommandLine cmd = uut.parse(OPTIONS, argv);
        assertArrayEquals(argv, cmd.getArgs());
        assertEquals(argvList, cmd.getArgList());
    }

    @Test
    void onlyOptionsWorks() {
        String[] argv = { "--help" };
        String[] empty = {};
        CommandLine cmd = uut.parse(OPTIONS, argv);
        assertArrayEquals(empty, cmd.getArgs());
        assertEquals(Collections.emptyList(), cmd.getArgList());
    }

    @ParameterizedTest
    @MethodSource("dataprovider")
    void parseOptions(String[] argv) {
        CommandLine cmd = uut.parse(OPTIONS, argv);
        assertTrue(cmd.hasOption("v"));
        assertTrue(cmd.hasOption("h"));
        assertTrue(cmd.hasOption("verbose"));
        assertTrue(cmd.hasOption("help"));

        assertArrayEquals(EXPECTED_ARGS, cmd.getArgs());
    }

    private static Object[] dataprovider() {
        return new Object[][] {
                { new String[] {"verbose", "foo", "bar", "help"} },
                { new String[] {"--verbose", "foo", "bar", "--help"} },
                { new String[] { "v", "foo", "bar", "h"} },
                { new String[] { "-v", "foo", "bar", "-h"} }
        };
    }


}

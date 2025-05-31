package se.anosh.spctag.util.optarg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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
    void noOptionsThrows() {
        String[] argv = { "foo", "bar" };
        assertThrows(ParseException.class, () -> uut.parse(OPTIONS, argv));
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

package se.anosh.spctag.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class Utf8ValidatorTest {

    private static final String UTF8_TEST_FILE = "src/test/resources/utf8-testfile.txt";

    @Test
    void validAscii() {
        byte[] ascii = {0x68, 0x65, 0x6C, 0x6C, 0x6F}; // hello
        assertTrue(Utf8Validator.validate(ascii));
    }

    @Test
    void validTwoByteCharacter() {
        // ä 0xC3A4
        // expected result
        // 0x0000 0000 1110 0100 - 0x00E4
        byte[] arr = {(byte) 0xC3, (byte) 0xA4};
        assertTrue(Utf8Validator.validate(arr));
    }

    @Test
    void validThreeByteLong() {
        // utf8 €
        // 0xE2 		0x82 		0xAC
        // 1110 0010 	1000 0010	1010 1100
        // U+20 AC
        byte[] arr = {(byte) 0xE2, (byte) 0x82, (byte) 0xAC};
        assertTrue(Utf8Validator.validate(arr));
    }

    @Test
    @DisplayName("Valid 4-byte character")
    void validFourByteCharacter() {
        // 0xF0 		0x9F 		0x94 		0xA5 utf8 🔥
        // 1111 0 000	10 01 11 11	1001 0100	1010 0101
        byte[] arr = {(byte) 0xF0, (byte) 0x9F, (byte) 0x94, (byte) 0xA5};
        assertTrue(Utf8Validator.validate(arr));
    }

    @Test
    void validPrivateUseFourByteCharacter() {
        // U+1096B3
        byte[] arr = {(byte) 0xF4, (byte) 0x89, (byte) 0x9A, (byte) 0xB3};
        assertTrue(Utf8Validator.validate(arr));
    }

    // Sad path-testing
    @Test
    void continuationByteBroken() {
        final byte continuationByte = (byte) 0x80;
        byte[] broken = {(byte) 0x68, 0x65, 0x6C, 0x6C, continuationByte, 0x6F}; // ascii + stray continuation byte
        assertFalse(Utf8Validator.validate(broken), "Too long (ASCII + continuation byte)");
    }

    @Test
    void overlongTwoBytes() {
        byte[] broken = {(byte) 0xC1, (byte) 0xBF}; // invalid two-byte ('Q' encoded as 2-byte)
        // 1101 1111 1011 1111
        assertFalse(Utf8Validator.validate(broken), "Overlong 2-byte");
    }

    @Test
    void overlongThreeBytes() {
        // 1100 0011 1010 0100 - 0xC3A4
        byte[] broken = {(byte) 0xE0, (byte) 0x80, (byte) 0xA4}; // invalid 3-byte encoding ('ä' encoded as 3-bytes)
        assertFalse(Utf8Validator.validate(broken), "Overlong 3-byte");
    }

    @Test
    void tooShort3BytesMissingSecondByte() {
        byte[] broken = {(byte) 0xC0, 0x7C}; // missing second byte;
        assertFalse(Utf8Validator.validate(broken));
    }

    @Test
    void tooShort3BytesMissingThirdByte() {
        byte[] broken = {(byte) 0xC0, (byte) 0x9F, (byte) 0x7C}; // missing third byte
        assertFalse(Utf8Validator.validate(broken));
    }

    @Test
    @DisplayName("5-byte character sequences are disallowed")
    void fiveByteIsTooLong() {
        byte[] broken = {(byte) 0xF0, (byte) 0x9F, (byte) 0x9F, (byte) 0x9F, (byte) 0x9F};
        assertFalse(Utf8Validator.validate(broken));
    }

    @Test
    void utf16SurrogateHalvesLowerBound() { // 3-byte utf8
        byte[] arr = {(byte) 0xED, (byte) 0xA0, (byte) 0x80};
        // surrogate 0xD800  1101 1000 0000 0000
        // and...... 0xDF FF  1101 1111 1111 1111
        /*
        "The definition of UTF-8 prohibits encoding character numbers between
        U+D800 and U+DFFF, which are reserved for use with the UTF-16
        encoding form (as surrogate pairs) and do not directly represent
        characters." - RFC 3629
        */
        assertFalse(Utf8Validator.validate(arr));
    }

    @Test
    void utf16SurrogateHalvesUpperBound() { // 3-byte utf8
        byte[] arr = {(byte) 0xED, (byte) 0xBF, (byte) 0xBF};
        // surrogate 0xDF FF  1101 1111 1111 1111
        assertFalse(Utf8Validator.validate(arr));
    }

    @Test
    void illegalTwoByteSequence() {
        // example from RFC 3629
        byte[] broken = {0x2F, (byte) 0xC0, (byte) 0xAE, 0x2E, 0x2F};
        assertFalse(Utf8Validator.validate(broken));
    }

    // -------------
    // Broken.
    // Testing overlong sequences from Markus Kuhn UTF-8 decoder capability and stress test
    // https://www.cl.cam.ac.uk/~mgk25/ucs/examples/UTF-8-test.txt

    @Test
    void singleUtf16Surrogates() {
        byte[] broken = {(byte) 0xED, (byte) 0xAD, (byte) 0xBF};
        byte[] broken2 = {(byte) 0xED, (byte) 0xAE, (byte) 0x80};
        byte[] broken3 = {(byte) 0xED, (byte) 0xAF, (byte) 0xBF};
        byte[] broken4 = {(byte) 0xED, (byte) 0xB0, (byte) 0x80};
        byte[] broken5 = {(byte) 0xED, (byte) 0xBE, (byte) 0x80};

        assertFalse(Utf8Validator.validate(broken));
        assertFalse(Utf8Validator.validate(broken2));
        assertFalse(Utf8Validator.validate(broken3));
        assertFalse(Utf8Validator.validate(broken4));
        assertFalse(Utf8Validator.validate(broken5));
    }

    @Test
    void pairedUtf16Surrogates() {
        byte[] broken = {(byte) 0xED, (byte) 0xA0, (byte) 0x80, (byte) 0xED, (byte) 0xB0, (byte) 0x80};
        byte[] broken2 = {(byte) 0xED, (byte) 0xA0, (byte) 0x80, (byte) 0xED, (byte) 0xBF, (byte) 0xBF};
        byte[] broken3 = {(byte) 0xED, (byte) 0xAD, (byte) 0xBF, (byte) 0xED, (byte) 0xB0, (byte) 0x80};
        byte[] broken4 = {(byte) 0xED, (byte) 0xAD, (byte) 0xBF, (byte) 0xED, (byte) 0xBF, (byte) 0xBF};
        byte[] broken5 = {(byte) 0xED, (byte) 0xAE, (byte) 0x80, (byte) 0xED, (byte) 0xB0, (byte) 0x80};
        byte[] broken6 = {(byte) 0xED, (byte) 0xAE, (byte) 0x80, (byte) 0xED, (byte) 0xBF, (byte) 0xBF};
        byte[] broken7 = {(byte) 0xED, (byte) 0xAE, (byte) 0xBF, (byte) 0xED, (byte) 0xB0, (byte) 0x80};
        byte[] broken8 = {(byte) 0xED, (byte) 0xAF, (byte) 0xBF, (byte) 0xED, (byte) 0xBF, (byte) 0x8F};

        assertFalse(Utf8Validator.validate(broken));
        assertFalse(Utf8Validator.validate(broken2));
        assertFalse(Utf8Validator.validate(broken3));
        assertFalse(Utf8Validator.validate(broken4));
        assertFalse(Utf8Validator.validate(broken5));
        assertFalse(Utf8Validator.validate(broken6));
        assertFalse(Utf8Validator.validate(broken7));
        assertFalse(Utf8Validator.validate(broken8));
    }

    // Maximum overlong sequences
    @Test
    void overlongSequences() {
        byte[] broken = {(byte) 0xC1, (byte) 0xBF}; // DRY
        byte[] broken2 = {(byte) 0xE0, (byte) 0x9F, (byte) 0xBF};
        byte[] broken3 = {(byte) 0xF0, (byte) 0x80, (byte) 0xBF, (byte) 0xBF};
        byte[] broken4 = {(byte) 0xF8, (byte) 0x87, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF}; // 5 bytes
        byte[] broken5 = {(byte) 0xFC, (byte) 0x83, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF}; // 6 bytes

        assertFalse(Utf8Validator.validate(broken));
        assertFalse(Utf8Validator.validate(broken2));
        assertFalse(Utf8Validator.validate(broken3));
        assertFalse(Utf8Validator.validate(broken4));
        assertFalse(Utf8Validator.validate(broken5));
    }

    @Test
    void overlongAscii() {
        byte[] broken = {(byte) 0xC0, (byte) 0xAF};
        byte[] broken2 = {(byte) 0xE0, (byte) 0x80, (byte) 0xAF};
        byte[] broken3 = {(byte) 0xF0, (byte) 0x80, (byte) 0x80, (byte) 0xAF};
        byte[] broken4 = {(byte) 0xF8, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xAF}; // 5-byte
        byte[] broken5 = {(byte) 0xFC, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0xAF}; // 6-byte
        assertFalse(Utf8Validator.validate(broken));
        assertFalse(Utf8Validator.validate(broken2));
        assertFalse(Utf8Validator.validate(broken3));
        assertFalse(Utf8Validator.validate(broken4));
        assertFalse(Utf8Validator.validate(broken5));
    }

    @Test
    void impossibleBytes() {
        byte[] broken = {(byte) 0x7C, (byte) 0xFE};
        byte[] broken2 = {(byte) 0x7C, (byte) 0xFF};
        byte[] broken3 = {(byte) 0xFE, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF};
        assertFalse(Utf8Validator.validate(broken));
        assertFalse(Utf8Validator.validate(broken2));
        assertFalse(Utf8Validator.validate(broken3));
    }

    @Test
    void validUtf8TestFile() throws IOException {
        Path utf8 = Path.of(UTF8_TEST_FILE);
        byte[] arr = Files.readAllBytes(utf8);
        assertTrue(Utf8Validator.validate(arr));
    }

    @Test
        /*
         * A contiguous range of 32 noncharacters: U+FDD0..U+FDEF in the BMP
         * https://www.unicode.org/faq/private_use.html#noncharacters
         *
         * Tests 32/66 noncharacter
         *
         */
    void disallowContigiousNoncharacters() {
        final byte[][] contigious = {
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x90},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x91},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x92},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x93},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x94},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x95},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x96},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x97},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x98},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x99},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x9A},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x9B},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x9C},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x9D},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x9E},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0x9F},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xA0},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xA1},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xA2},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xA3},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xA4},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xA5},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xA6},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xA7},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xA8},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xA9},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xAA},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xAB},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xAC},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xAD},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xAE},
                {(byte) 0xEF, (byte) 0xB7, (byte) 0xAF}
        };
        assertEquals(32, contigious.length);
        for (int i = 0; i < contigious.length; i++) {
            assertFalse(Utf8Validator.validate(contigious[i]), "Noncharacter at offset[%d] passed validation".formatted(i));
        }
    }

    /*
     * Disallow noncharacters
     *
     * "the last two code points of the BMP, U+FFFE and U+FFFF"
     *
     * "With such internal use of noncharacters, it may be desirable and safer
     * to block those code points in UTF-8 decoders, as they should never
     * occur legitimately in incoming UTF-8 data, and could trigger unsafe
     * behaviour in subsequent processing."
     * - Markus Kuhn, UTF-8 decoder capability and stress test
     *
     * Tests 2/66 noncharacter
     *
     */
    @Test
    void blockLastTwoCodePoints() {
        final byte[][] lastTwoOfBmp = {
                {(byte) 0xEF, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xEF, (byte) 0xBF, (byte) 0xBF}
        };
        assertEquals(2, lastTwoOfBmp.length);
        for (int i = 0; i < lastTwoOfBmp.length; i++) {
            assertFalse(Utf8Validator.validate(lastTwoOfBmp[i]), "Noncharacter at offset[%d] passed validation".formatted(i));
        }
    }

    /*
     * Block noncharacters
     *
     * "the last two code points of each of the 16 supplementary planes:
     *  U+1FFFE, U+1FFFF, U+2FFFE, U+2FFFF, ... U+10FFFE, U+10FFFF"
     *
     *  https://www.unicode.org/faq/private_use.html#noncharacters
     *
     * Tests 32/66 noncharacter
     */
    @Test
    void blockNoncharacters() {
        final byte[][] lastTwoOfSupplementaryPlanes = {
                {(byte) 0xF0, (byte) 0x9F, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF0, (byte) 0x9F, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF0, (byte) 0xAF, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF0, (byte) 0xAF, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF0, (byte) 0xBF, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF0, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF1, (byte) 0x8F, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF1, (byte) 0x8F, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF1, (byte) 0x9F, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF1, (byte) 0x9F, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF1, (byte) 0xAF, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF1, (byte) 0xAF, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF1, (byte) 0xBF, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF1, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF2, (byte) 0x8F, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF2, (byte) 0x8F, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF2, (byte) 0x9F, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF2, (byte) 0x9F, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF2, (byte) 0xAF, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF2, (byte) 0xAF, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF2, (byte) 0xBF, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF2, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF3, (byte) 0x8F, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF3, (byte) 0x8F, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF3, (byte) 0x9F, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF3, (byte) 0x9F, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF3, (byte) 0xAF, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF3, (byte) 0xAF, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF3, (byte) 0xBF, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF3, (byte) 0xBF, (byte) 0xBF, (byte) 0xBF},

                {(byte) 0xF4, (byte) 0x8F, (byte) 0xBF, (byte) 0xBE},
                {(byte) 0xF4, (byte) 0x8F, (byte) 0xBF, (byte) 0xBF},
        };
        assertEquals(lastTwoOfSupplementaryPlanes.length, 32);

        for (int i = 0; i < lastTwoOfSupplementaryPlanes.length; i++) {
            assertFalse(Utf8Validator.validate(lastTwoOfSupplementaryPlanes[i]), "Noncharacter at offset[%d] passed validation".formatted(i));
        }
    }


}

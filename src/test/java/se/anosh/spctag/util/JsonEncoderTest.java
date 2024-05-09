package se.anosh.spctag.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonEncoderTest {

    @Test
	// as per JSON specification
    void specialCharactersAreEscaped() {
		final String key = "Escaped characters";
		final String val = """
				\\ "\b/\f\n\r\t""";
		final String expected = """
				"escapedCharacters" : "\\\\ \\"\\b\\/\\f\\n\\r\\t\"""";
		assertEquals(expected, JsonEncoder.toJson(key, val));
	}

	@Test
	/*
		"All Unicode characters may be placed within the
		quotation marks, except for the characters that must be escaped:
		quotation mark, reverse solidus, and the control characters (U+0000	through U+001F)."

			- RFC 7159 (The JavaScript Object Notation (JSON) Data Interchange Format)
	*/
	@DisplayName("Ascii control chars are escaped as UTF-16")
	void asciiControlCharactersAreEscaped() {
		final String key = "escaped ascii control characters";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= 0x7; i++) {
			sb.append((char) i);
		}
		// skip 0x08 - 0x0D (tested as escaped special characters)
		for (int i = 0x0E; i <= 0x1F; i++) {
			sb.append((char) i);
		}
		final String expected = """
				"escapedAsciiControlCharacters" : "\\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\\u000e\\u000f\\u0010\\u0011\\u0012\\u0013\\u0014\\u0015\\u0016\\u0017\\u0018\\u0019\\u001a\\u001b\\u001c\\u001d\\u001e\\u001f\"""";

		assertEquals(expected, JsonEncoder.toJson(key, sb.toString()));
	}

	@Test
	void integersSupported() {
		final String key = "Track number";
		final int val = 42;
		String expected = """
			"trackNumber" : 42""";
		assertEquals(expected, JsonEncoder.toJson(key, val));
	}

	@Test
	void floatingPointSupported() {
		final String key = "floating point";
		final double val = Math.PI;
		final String expected = """
			"floatingPoint" : 3.141592653589793""";
		assertEquals(expected, JsonEncoder.toJson(key, val));
	}

	@Test
	@DisplayName("Multibyte UTF-8 is not escaped")
	void utf8Works() {
		final String key = "yiddish letter";
		final String val = "א"; // shtumer alef
		final String expected = """
			"yiddishLetter" : "א\"""";
		assertEquals(expected, JsonEncoder.toJson(key, val));
	}


}

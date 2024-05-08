package se.anosh.spctag.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonEncoderTest {

    @Test
    void encodingWorks() {
        final String key = "Song Name";
        final String val = "du gamla du fria	foobar"; // tab character

        final String expected = """
                "songName" : "du gamla du fria\\tfoobar\"""";

        assertEquals(expected, JsonEncoder.toJson(key, val));
		System.out.println(expected);

		// TODO fix
        System.out.println("""
				{
					%s,
					%s,
					%s,
					%s,
					%s,
					%s,
					%s,
					%s,
					%s
				}
				""".formatted(
                JsonEncoder.toJson("song name", "du gamla du fria\tfoobar"),
                JsonEncoder.toJson("artist name", "michael jackson"),
                JsonEncoder.toJson("track no", 42),
                JsonEncoder.toJson("null funkar", null),
                JsonEncoder.toJson("sant funkar", true),
                JsonEncoder.toJson("falskt funkar", false),
                JsonEncoder.toJson("double funkar", Math.PI),

                JsonEncoder.toJson("UTF-16 test", Character.toString( (char) 0x1F )), // ASCII, unit separator control code U+001F
                JsonEncoder.toJson("UTF-8 test", "😐")
						/*
							All Unicode characters may be placed within the
							quotation marks, except for the characters that must be escaped:
							quotation mark, reverse solidus, and the control characters (U+0000	through U+001F).

							- RFC 7159 (The JavaScript Object Notation (JSON) Data Interchange Format)
						*/
        ));



    }




}

package se.anosh.spctag;

import org.junit.Before;
import org.junit.Test;
import se.anosh.spctag.domain.Id666;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class Id666Test {

    private Id666 uut;

    @Before
    public void setup() {
        uut = new Id666();
    }

    @Test
    public void storeDateAsStringWithoutSeparators() {
        final String expected = "19990401";
        var date = LocalDate.of(1999, Month.APRIL, 1);
        uut.setDateDumpWasCreated(date);
        assertEquals(expected, uut.getDateDumpWasCreated());
    }

}

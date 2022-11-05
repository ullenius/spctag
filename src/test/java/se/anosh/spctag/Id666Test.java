package se.anosh.spctag;

import org.junit.Before;
import org.junit.Test;
import se.anosh.spctag.domain.Id666;

import static org.junit.Assert.assertEquals;

public class Id666Test {

    private Id666 uut;

    @Before
    public void setup() {
        uut = new Id666();
    }

    @Test
    public void dumpedDateStrictResolverStyle() {
        final int illegalDateFormat = 19990229;
        uut.setDateDumpWasCreated(illegalDateFormat);
        assertEquals("", uut.getDateDumpWasCreated());
    }

    @Test
    public void dumpedDateFailsOnShortDates() {
        final int shortDate = 991105;
        uut.setDateDumpWasCreated(shortDate);
        assertEquals("", uut.getDateDumpWasCreated());
    }

    @Test
    public void dumpedDate_yyyyMMdd() {
        final int date = 20000401;
        uut.setDateDumpWasCreated(date);
        assertEquals(Integer.toString(date), uut.getDateDumpWasCreated());
    }

}

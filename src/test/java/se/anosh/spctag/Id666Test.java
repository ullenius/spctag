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

    @Test
    public void dumpedDateStrictResolverStyle() {
        final int date = createBinaryDumpDate((short) 1999, (byte) 2, (byte) 29);
     //   uut.setDateDumpWasCreated(date);
        assertEquals("", uut.getDateDumpWasCreated());
    }

    @Test
    public void dumpedDateFailsOnShortDates() {
        final int shortDate = 991105;
   //     uut.setDateDumpWasCreated(shortDate);
        assertEquals("", uut.getDateDumpWasCreated());
    }

    @Test
    public void dumpedDate_yyyyMMdd() {
        final int date = 20000401;
      //  uut.setDateDumpWasCreated(date);
        assertEquals(Integer.toString(date), uut.getDateDumpWasCreated());
    }

    private int createBinaryDumpDate(LocalDate date) {
        final short year = (short) date.getYear();
        final byte month = (byte) date.getMonthValue();
        final byte day = (byte) date.getDayOfMonth();
        return createBinaryDumpDate(year, month, day);
    }

    private int createBinaryDumpDate(short year, byte month, byte day) {
        var buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(day);
        buffer.put(month);
        buffer.putShort(year);
        buffer.rewind();
        return buffer.getInt();
    }

}

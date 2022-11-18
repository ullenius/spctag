package se.anosh.spctag.domain;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.Comparator;
import java.util.Objects;

import org.tinylog.Logger;
import se.anosh.spctag.emulator.factory.Emulator;
import se.anosh.spctag.emulator.factory.EmulatorI;

public final class Id666 implements Comparable <Id666> {
	
	// Thanks to Lukasz Wiktor @ stack overflow (2014)
    private static final Comparator<String> nullSafeStringComparator = Comparator.nullsFirst(String::compareToIgnoreCase);
    private static final Comparator<Id666> id666Comparator = Comparator
            .comparing(Id666::getGameTitle, nullSafeStringComparator)
            .thenComparing(Id666::getArtist, nullSafeStringComparator)
            .thenComparing(Id666::getSongTitle, nullSafeStringComparator);

	//SPC's didn't exist before 15 Apr 1998
	private static final LocalDate SPC_FORMAT_BIRTHDAY = LocalDate.of(1998, Month.APRIL, 15);

	private static final String DATE_SEPARATOR = "/";

	private String header;
	private String artist;
	private String songTitle;
	private String gameTitle;
	private String nameOfDumper;
	private String comments;
	private LocalDate dateDumpWasCreated;
	private EmulatorI emulatorUsedToCreateDump;

	private boolean hasId666Tags;
	private boolean binaryTagFormat;
	
	public String getHeader() {
		return header;
	}
	public String getArtist() {
		return artist;
	}
	public String getSongTitle() {
		return songTitle;
	}
	public String getGameTitle() {
		return gameTitle;
	}
	public String getNameOfDumper() {
		return nameOfDumper;
	}
	public String getComments() {
		return comments;
	}
	public LocalDate getDateDumpWasCreated() {
		return dateDumpWasCreated;
	}
	public String dateDumpWasCreated() {
		return dateDumpWasCreated != null
				? dateDumpWasCreated.toString().replaceAll("-", "/")
				: "";
	}
	public EmulatorI getEmulatorUsedToCreateDump() {
		return emulatorUsedToCreateDump;
	}
	public boolean hasId666Tags() {
		return hasId666Tags;
	}
	public boolean isTextTagFormat() {
		return !isBinaryTagFormat();
	}
	public boolean isBinaryTagFormat() {
		return binaryTagFormat;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public void setSongTitle(String songTitle) {
		this.songTitle = songTitle;
	}
	public void setGameTitle(String gameTitle) {
		this.gameTitle = gameTitle;
	}
	public void setNameOfDumper(String nameOfDumper) {
		this.nameOfDumper = nameOfDumper;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public void setDateDumpWasCreated(final LocalDate dumpdate) {
		Objects.requireNonNull(dumpdate);
		if (dumpdate.isBefore(SPC_FORMAT_BIRTHDAY)) {
			Logger.warn("SPC dumped date pre-dates the SPC-format ({}): {}", SPC_FORMAT_BIRTHDAY, dumpdate);
		}
		this.dateDumpWasCreated = dumpdate;
	}

	public void setDateDumpWasCreated(String dateDumpWasCreated) { // FIXME add validation
		if (dateDumpWasCreated == null) {
			return; // do nothing
		}
		if (dateDumpWasCreated.length() > Field.DUMP_DATE_TEXT_FORMAT.getLength()) {
			Logger.warn("Dump date is longer than allowed");
		}
		this.dateDumpWasCreated = dateDumpWasCreated.isBlank()
				? null
				: parseDate(dateDumpWasCreated);
	}

	/**
	 *
	 * @param date
	 * Spec says: MM/DD/YYYY
	 * Allowed formats: YYYY-MM-DD, MM-DD-YYYY. Sometimes allowed: YYYY-DD-MM, DD-MM-YYYY
	 * Allowed separators: '/' or '-'
	 * Leading zeroes are ignored for month or day fields
	 * ---
	 *  Behaviour:
	 * 	1. Try to parse as ISO-8601 date YYYY-MM-DD
	 * 	2. Try to parse as spec-date MM/DD/YYYY
	 * 	a) If month/day is invalid in step 1 or 2. Swap them and parse as DD/MM
	 * ---
	 * 	Examples:
	 * 	2005-31-12 gets parsed as 2005-12-31
	 * 	31-12-2005 gets parsed as 2005-12-31
	 * 	05-12-1999 gets parsed as 1999-05-12
	 *
	 */
	private LocalDate parseDate(final String date) {
		try {
			return parseDateSlashSeparator(date.replaceAll("-", DATE_SEPARATOR));
		} catch (DateTimeException ex) {
			Logger.warn("Unable to parse date: {}", ex);
			Logger.debug("Raw datestring: {}", date);
			return null;
		}
	}

	private LocalDate parseDateSlashSeparator(final String date) {
		final String[] arr = date.split(DATE_SEPARATOR);
		if (arr.length != 3) {
			Logger.warn("Illegal date-string format: {}", date);
			return null;
		}

		if (arr[0].length() == 4) { // iso8601 YYYY-MM-DD
			return parseIso8601(arr);
		}

		final String month = arr[0]; // spec compliant
		final String day = arr[1];
		final String year = arr[2];
		Logger.debug("Year: {}, Month: {}, Day: {}", year, month, day);
		return buildDate(Year.parse(year), Integer.parseInt(month), Integer.parseInt(day));
	}

	/**
	 * ISO-8601 date-format YYYY-MM-DD
	 */
	private LocalDate parseIso8601(String[] arr) {
		return buildDate(Year.parse(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
	}

	private static LocalDate buildDate(final Year year, final int month, final int day) {
		if (month > 12 && day <= 12) {
			return buildDate(year, day, month); // swap order
		}
		return LocalDate.of(year.getValue(), month, day);
	}

	public void setEmulatorUsedToCreateDump(EmulatorI emulatorUsedToCreateDump) {
		this.emulatorUsedToCreateDump = emulatorUsedToCreateDump;
	}
	public void setHasId666Tags(boolean hasId666Tags) {
		this.hasId666Tags = hasId666Tags;
	}
	public void setBinaryTagFormat(boolean binaryTagFormat) {
		this.binaryTagFormat = binaryTagFormat;
	}
	
	 @Override
	    public int compareTo(Id666 o) {
	        return id666Comparator.compare(this, o);
	    }

	@Override
	public String toString() {
		return "Id666 [artist=" + artist + ", songTitle=" + songTitle + ", gameTitle=" + gameTitle + ", nameOfDumper="
				+ nameOfDumper + ", emulatorUsedToCreateDump=" + emulatorUsedToCreateDump + ", hasId666Tags="
				+ hasId666Tags + ", binaryTagFormat=" + binaryTagFormat + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(artist, songTitle, gameTitle, nameOfDumper, comments, dateDumpWasCreated,
				emulatorUsedToCreateDump, hasId666Tags, binaryTagFormat);
	}

	/**
	 * Using all fields to compare except hasId666Tags.
	 * Even the Header is used. Since older versions used a different
	 * version number in the header.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Id666 other = (Id666) obj;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (dateDumpWasCreated == null) {
			if (other.dateDumpWasCreated != null)
				return false;
		} else if (!dateDumpWasCreated.equals(other.dateDumpWasCreated))
			return false;
		if (emulatorUsedToCreateDump == null) {
			if (other.emulatorUsedToCreateDump != null)
				return false;
		} else if (!emulatorUsedToCreateDump.equals(other.emulatorUsedToCreateDump))
			return false;
		if (gameTitle == null) {
			if (other.gameTitle != null)
				return false;
		} else if (!gameTitle.equals(other.gameTitle))
			return false;
		if (header == null) {
			if (other.header != null)
				return false;
		} else if (!header.equals(other.header))
			return false;
		if (nameOfDumper == null) {
			if (other.nameOfDumper != null)
				return false;
		} else if (!nameOfDumper.equals(other.nameOfDumper))
			return false;
		if (songTitle == null) {
			if (other.songTitle != null)
				return false;
		} else if (!songTitle.equals(other.songTitle))
			return false;
		return true;
	}

	public enum Field {
		HEADER(0x00, 33),
		SONG_TITLE(0x2E, 32),
		GAME_TITLE(0x4E, 32),
		NAME_OF_DUMPER(0x6E, 16),
		COMMENTS(0x7E, 32),
		DUMP_DATE_TEXT_FORMAT(0x9E, 11),
		DUMP_DATE_BINARY_FORMAT(0x9E, 4),
		ARTIST_OF_SONG_TEXT_FORMAT(0xB1, 32),
		ARTIST_OF_SONG_BINARY_FORMAT(0xB0, 32),
		EMULATOR_TEXT_FORMAT(0xD2, 1),
		EMULATOR_BINARY_FORMAT(0xD1, 1),

		HEADER_CONTAINS_ID666_TAG(0x23, 1);

		private final int length;
		private final int offset;

		Field(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}

		public int getLength() {
			return length;
		}

		public int getOffset() {
			return offset;
		}
	}

}

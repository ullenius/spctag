package se.anosh.spctag.domain;

import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Objects;

import org.tinylog.Logger;
import se.anosh.spctag.emulator.factory.Emulator;

public final class Id666 implements Comparable <Id666> {
	
	// Thanks to Lukasz Wiktor @ stack overflow (2014)
    private static final Comparator<String> nullSafeStringComparator = Comparator.nullsFirst(String::compareToIgnoreCase);
    private static final Comparator<Id666> id666Comparator = Comparator
            .comparing(Id666::getGameTitle, nullSafeStringComparator)
            .thenComparing(Id666::getArtist, nullSafeStringComparator)
            .thenComparing(Id666::getSongTitle, nullSafeStringComparator);

	private static final DateTimeFormatter BINARY_DUMP_DATE_FORMAT = DateUtil.id666DumpedDateFormatter();

	//SPC's didn't exist before 15 Apr 1998
	private static final LocalDate SPC_FORMAT_BIRTHDAY = LocalDate.of(1998, Month.APRIL, 15);

	private String header;
	private String artist;
	private String songTitle;
	private String gameTitle;
	private String nameOfDumper;
	private String comments;
	private LocalDate dateDumpWasCreated;
	private Emulator emulatorUsedToCreateDump;

	// primitive type wrappers so that they will cause
	// NPE instead of default value if setMethod is never called
	private Boolean hasId666Tags;
	private Boolean binaryTagFormat;
	
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
	public String getDateDumpWasCreated() {
		if (dateDumpWasCreated == null) {
			return "";
		}
		return isTextTagFormat()
				? dateDumpWasCreated.toString().replaceAll("-", "/")
				: dateDumpWasCreated.format(BINARY_DUMP_DATE_FORMAT)
				.replaceAll("\\D", "");
	}
	public Emulator getEmulatorUsedToCreateDump() {
		return emulatorUsedToCreateDump;
	}
	public boolean hasId666Tags() {
		return hasId666Tags;
	}
	public boolean isBinaryTagFormat() {
		return binaryTagFormat;
	}
	public boolean isTextTagFormat() {
		return !isBinaryTagFormat();
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

	private LocalDate parseDate(String date) {
		final String[] arr = date.split("");
		if (arr.length != 3) {
			throw new IllegalArgumentException("Illegal date-string format");
		}
		final int i = (arr[0].length() >> 2) & 1;
		final String day = arr[i + i];
		final String year = arr[2 - ( i + i) ];
		final String month = arr[1];
		return LocalDate.of(
				Integer.parseInt(year),
				Month.of(Integer.parseInt(month)),
				Integer.parseInt(day)
		);
	}

	public void setEmulatorUsedToCreateDump(Emulator emulatorUsedToCreateDump) {
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + ((binaryTagFormat == null) ? 0 : binaryTagFormat.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((dateDumpWasCreated == null) ? 0 : dateDumpWasCreated.hashCode());
		result = prime * result + ((emulatorUsedToCreateDump == null) ? 0 : emulatorUsedToCreateDump.hashCode());
		result = prime * result + ((gameTitle == null) ? 0 : gameTitle.hashCode());
		result = prime * result + ((header == null) ? 0 : header.hashCode());
		result = prime * result + ((nameOfDumper == null) ? 0 : nameOfDumper.hashCode());
		result = prime * result + ((songTitle == null) ? 0 : songTitle.hashCode());
		return result;
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
		if (binaryTagFormat == null) {
			if (other.binaryTagFormat != null)
				return false;
		} else if (!binaryTagFormat.equals(other.binaryTagFormat))
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

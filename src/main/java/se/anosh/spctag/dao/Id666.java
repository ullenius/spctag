package se.anosh.spctag.dao;

import java.util.Comparator;

import se.anosh.spctag.emulator.factory.Emulator;

public class Id666 implements Comparable <Id666> {
	
	// Thanks to Lukasz Wiktor @ stack overflow (2014)
    private static final Comparator<String> nullSafeStringComparator = Comparator.nullsFirst(String::compareToIgnoreCase);
    private static final Comparator<Id666> id666Comparator = Comparator
            .comparing(Id666::getGameTitle, nullSafeStringComparator)
            .thenComparing(Id666::getArtist, nullSafeStringComparator)
            .thenComparing(Id666::getSongTitle, nullSafeStringComparator);

	private String header;
	private String artist;
	private String songTitle;
	private String gameTitle;
	private String nameOfDumper;
	private String comments;
	private String dateDumpWasCreated;
	private Emulator emulatorUsedToCreateDump;

	// primitive type wrappers so that they will cause
	// nullpointerexception instead of default value
	// if setMethod is never called
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
		return dateDumpWasCreated;
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
	public boolean isTextTagFormat() { // the opposite boolean result of isBinaryTagFormat
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
	public void setDateDumpWasCreated(String dateDumpWasCreated) {
		this.dateDumpWasCreated = dateDumpWasCreated;
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
	 
	
	
	
	
}

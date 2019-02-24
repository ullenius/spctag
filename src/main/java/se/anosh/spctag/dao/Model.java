package se.anosh.spctag.dao;

import se.anosh.spctag.emulator.Emulator;

public class Model {

	private String header;
	private String artist;
	private String songTitle;
	private String gameTitle;
	private String nameOfDumper;
	private String comments;
	private String dateDumpWasCreated;
	private Emulator emulatorUsedToCreateDump;

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
	public String getDateDumpWasCreated() {
		return dateDumpWasCreated;
	}
	public Emulator getEmulatorUsedToCreateDump() {
		return emulatorUsedToCreateDump;
	}
	public boolean isHasId666Tags() {
		return hasId666Tags;
	}
	public boolean isBinaryTagFormat() {
		return binaryTagFormat;
	}

	
}

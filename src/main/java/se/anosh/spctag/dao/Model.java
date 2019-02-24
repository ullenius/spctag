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
	public boolean isHasId666Tags() {
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
	

	
}

package se.anosh.spctag.domain;

import se.anosh.spctag.dao.Xid6Reader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;

public class Xid6 {

    private static final double INTRO_LENGTH_DIVISOR = 64_0000.0;

    private String song;
    private String game;
    private String artist;
    private String dumper;
    private LocalDate dumped; // date song was dumped
    private Year year;
    private byte emulator; // fixme unsigned
    private String comments;
    private byte ostDisc;
    private final char[] ostTrack = new char[2];
    private String publisher;

    private int loopLength;
    private int endLength;
    private int fadeLength;
    private byte mutedVoices; // fixme unsigned
    private byte loop; // fixme
    private byte mixingLevel; // fixme
    private int introLength;

    private static final SimpleDateFormat dumpedDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void setDate(int date) throws ParseException {
        dumpedDateFormat.parse(Integer.toString(date));
    }

    public void setNumber(Object foo, int num) {

    }


    public void setIntro(int length) {
        this.introLength = length;
    }

    public double getIntrolength() {
        return introLength / INTRO_LENGTH_DIVISOR;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDumper() {
        return dumper;
    }

    public void setDumper(String dumper) {
        this.dumper = dumper;
    }


    public byte getEmulator() {
        return emulator;
    }

    public void setEmulator(byte emulator) {
        this.emulator = emulator;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public byte getOstDisc() {
        return ostDisc;
    }

    public void setOstDisc(byte ostDisc) {
        this.ostDisc = ostDisc;
    }

    public char[] getOstTrack() {
        return ostTrack;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getLoopLength() {
        return loopLength;
    }

    public void setLoopLength(int loopLength) {
        this.loopLength = loopLength;
    }

    public int getEndLength() {
        return endLength;
    }

    public void setEndLength(int endLength) {
        this.endLength = endLength;
    }

    public int getFadeLength() {
        return fadeLength;
    }

    public void setFadeLength(int fadeLength) {
        this.fadeLength = fadeLength;
    }

    public byte getMutedVoices() {
        return mutedVoices;
    }

    public void setMutedVoices(byte mutedVoices) {
        this.mutedVoices = mutedVoices;
    }

    public byte getLoop() {
        return loop;
    }

    public void setLoop(byte loop) {
        this.loop = loop;
    }

    public byte getMixingLevel() {
        return mixingLevel;
    }

    public void setMixingLevel(byte mixingLevel) {
        this.mixingLevel = mixingLevel;
    }

    public void setYear(int year) {
        this.year = Year.of(year);
    }

    public Year getYear() {
        return year;
    }

}

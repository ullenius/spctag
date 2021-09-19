package se.anosh.spctag.domain;

import java.text.SimpleDateFormat;
import java.time.Year;

public class Xid6 {

    private static final double INTRO_LENGTH_DIVISOR = 64_0000.0;

    private String song;
    private String game;
    private String artist;
    private String dumper;
    private int dumped; // date song was dumped, yyyy-mm-dd FIXME
    private Year year;
    private Byte emulator; // fixme unsigned
    private String comments;
    private String ostTitle;
    private Byte ostDisc;
    private final char[] ostTrack = new char[2];
    private String publisher;

    private int loopLength;
    private int endLength;
    private int fadeLength;
    private byte mutedVoices; // fixme unsigned
    private byte loop; // fixme
    private byte mixingLevel; // fixme
    private Integer introLength;

    private static final SimpleDateFormat dumpedDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String getOstTitle() {
        return ostTitle;
    }

    public void setDate(int date) {
        dumped = date;
    }

    public int getDate() {
        return dumped;
    }

    public void setData(Xid6Tag tag, byte b) {
        switch (tag) {
            case EMULATOR:
                setEmulator(b);
                break;
            case OST_DISC:
                setOstDisc(b);
                break;
            case LOOP:
                setLoop(b);
                break;
            default:
                throw new IllegalArgumentException("no mapping found for: " + tag);
        }
    }

    public void setNumber(Xid6Tag tag, int num) {
        switch (tag) {
            case DATE:
                setDate(num);
                break;
            case LOOP:
                setLoop((byte) num);
                break;
            case END:
                setEndLength(num);
                break;
            case FADE:
                setFadeLength(num);
                break;
            case MUTED:
                setMutedVoices( (byte) num);
                break;
            case MIXING:
                setMixingLevel( (byte) num);
                break;
            default:
                throw new IllegalArgumentException("no mapping found for: " + tag);
        }
    }

    public void setText(Xid6Tag tag, String text) {
        switch (tag) {
            case SONG:
                setSong(text);
                break;
            case GAME:
                setGame(text);
                break;
            case ARTIST:
                setArtist(text);
                break;
            case DUMPER:
                setDumper(text);
                break;
            case COMMENTS:
                setComments(text);
                break;
            case OST_TITLE:
                setOstTitle(text);
                break;
            case PUBLISHER:
                setPublisher(text);
                break;
            default:
                throw new IllegalArgumentException("no mapping found for: " + tag);
        }
    }

    public void setOstTitle(String title) {
        ostTitle = title;
    }

    public void setIntro(int length) {
        this.introLength = length;
    }

    public Double getIntrolength() {
        return (introLength != null) ? introLength / INTRO_LENGTH_DIVISOR : null;
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


    public Byte getEmulator() {
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

    public Byte getOstDisc() {
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

    public void printMutedVoices() {
        for (int i = 0; i < 8; i++) {
            System.out.print(((1 << i) & mutedVoices) != 0 ? 0 : 1);
        }
        System.out.println();
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

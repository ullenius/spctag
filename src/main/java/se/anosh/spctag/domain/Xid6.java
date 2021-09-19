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
    private OstTrack ostTrack;
    private String publisher;

    private Integer loopLength;
    private Integer endLength;
    private Integer fadeLength;
    private byte mutedVoices; // fixme unsigned
    private Byte loops;
    private Byte mixingLevel; // fixme
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

    public void setOstTitle(String title) {
        ostTitle = title;
    }

    public void setIntro(int length) {
        this.introLength = length;
    }

    public Double getIntrolength() {
        return introLength != null ? introLength / INTRO_LENGTH_DIVISOR : null;
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

    public OstTrack getOstTrack() {
        return ostTrack;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getLoopLength() {
        return loopLength;
    }

    public void setLoopLength(int loopLength) {
        this.loopLength = loopLength;
    }

    public Integer getEndLength() {
        return endLength;
    }

    public void setEndLength(int endLength) {
        this.endLength = endLength;
    }

    public Integer getFadeLength() {
        return fadeLength;
    }

    public void setFadeLength(int fadeLength) {
        this.fadeLength = fadeLength;
    }

    public byte getMutedVoices() {
        return mutedVoices;
    }

    public void printMutedVoices() {
        if (mutedVoices == 0) {
            return; // do nothing
        }
        for (int i = 0; i < 8; i++) {
            System.out.print(((1 << i) & mutedVoices) != 0 ? 0 : 1);
        }
        System.out.println();
    }

    public void setMutedVoices(byte mutedVoices) {
        this.mutedVoices = mutedVoices;
    }

    public Byte getLoops() {
        return loops;
    }

    public void setLoop(byte loops) {
        this.loops = loops;
    }

    public Byte getMixingLevel() {
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

    public void setOstTrack(OstTrack ostTrack) {
        this.ostTrack = ostTrack;
    }

   public static final class OstTrack {
       final byte track;
       final char ch;
        public OstTrack(byte track) {
            this.track = track;
            this.ch = 0;
        }
        public OstTrack(byte track, char ch) {
            this.track = track;
            this.ch = ch;
        }
       public String toString() {
           return ch != 0 ? track + " " + ch : Byte.toString(track);
       }
    }


}

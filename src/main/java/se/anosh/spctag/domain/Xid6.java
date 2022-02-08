package se.anosh.spctag.domain;

import org.tinylog.Logger;
import se.anosh.spctag.emulator.factory.Emulator;
import se.anosh.spctag.emulator.factory.EmulatorFactory;
import se.anosh.spctag.emulator.factory.ModernEmulatorFactory;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class Xid6 {

    private static final double INTRO_LENGTH_DIVISOR = 64_0000.0;

    private String song;
    private String game;
    private String artist;
    private String dumper;
    private LocalDate dumped;
    private Year year;
    private Emulator emulator;
    private String comments;
    private String ostTitle;
    private Byte ostDisc;
    private OstTrack ostTrack;
    private String publisher;

    private Integer loopLength;
    private Integer endLength;
    private Integer fadeLength;
    private short mutedChannels; // unsigned byte
    private Byte loops;
    private Byte mixingLevel;
    private Integer introLength;

    private static final DateTimeFormatter dumpedDateFromatter = DateTimeFormatter.BASIC_ISO_DATE;

    public String getOstTitle() {
        return ostTitle;
    }

    public void setDate(int date) {
        try {
            dumped = LocalDate.parse(Integer.toString(date), dumpedDateFromatter);
        } catch (DateTimeException ex) {
            Logger.error("Invalid date format (date song was dumped): ", ex);
            dumped = null;
        }
    }

    public LocalDate getDate() {
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

    public Emulator getEmulator() {
        return emulator;
    }

    public void setEmulator(byte emulator) {
        EmulatorFactory factory = new ModernEmulatorFactory();
        this.emulator = factory.orderEmulator(emulator);
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

    public boolean hasMutedChannels() {
        return mutedChannels != 0;
    }

    public String getMutedChannels() {
        if (mutedChannels == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = Byte.SIZE - 1; i >= 0; i--) {
            sb.append( (((1 << i) & mutedChannels) > 0) ? 1 : 0);
        }
        return sb.toString();
    }

    public void setMutedChannels(short mutedVoices) {
        this.mutedChannels = mutedVoices;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Xid6 xid6 = (Xid6) o;
        return mutedChannels == xid6.mutedChannels && Objects.equals(song, xid6.song) && Objects.equals(game, xid6.game)
                && Objects.equals(artist, xid6.artist) && Objects.equals(dumper, xid6.dumper)
                && Objects.equals(dumped, xid6.dumped) && Objects.equals(year, xid6.year)
                && Objects.equals(emulator, xid6.emulator) && Objects.equals(comments, xid6.comments)
                && Objects.equals(ostTitle, xid6.ostTitle) && Objects.equals(ostDisc, xid6.ostDisc)
                && Objects.equals(ostTrack, xid6.ostTrack) && Objects.equals(publisher, xid6.publisher)
                && Objects.equals(loopLength, xid6.loopLength) && Objects.equals(endLength, xid6.endLength)
                && Objects.equals(fadeLength, xid6.fadeLength) && Objects.equals(loops, xid6.loops)
                && Objects.equals(mixingLevel, xid6.mixingLevel) && Objects.equals(introLength, xid6.introLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(song, game, artist, dumper, dumped, year, emulator, comments, ostTitle, ostDisc, ostTrack,
                publisher, loopLength, endLength, fadeLength, mutedChannels, loops, mixingLevel, introLength);
    }

    @Override
    public String toString() {
        return "Xid6{" +
                "song='" + song + '\'' +
                ", game='" + game + '\'' +
                ", artist='" + artist + '\'' +
                ", dumper='" + dumper + '\'' +
                ", dumped=" + dumped +
                ", year=" + year +
                ", emulator=" + emulator +
                ", comments='" + comments + '\'' +
                ", ostTitle='" + ostTitle + '\'' +
                ", ostDisc=" + ostDisc +
                ", ostTrack=" + ostTrack +
                ", publisher='" + publisher + '\'' +
                ", loopLength=" + loopLength +
                ", endLength=" + endLength +
                ", fadeLength=" + fadeLength +
                ", mutedChannels=" + mutedChannels +
                ", loops=" + loops +
                ", mixingLevel=" + mixingLevel +
                ", introLength=" + introLength +
                '}';
    }
}

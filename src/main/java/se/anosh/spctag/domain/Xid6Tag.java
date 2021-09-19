package se.anosh.spctag.domain;

public enum Xid6Tag {
        SONG("Song name"),
        GAME("Game name"),
        ARTIST("Artist's name"),
        DUMPER("Dumper's name"),
        DATE("Date song was dumped"),
        EMULATOR("Emulator used"),
        COMMENTS("Comments"),
        OST_TITLE("Official Soundtrack Title"),
        OST_DISC("OST disc"),
        OST_TRACK("OST track"),
        PUBLISHER("Publisher's name"),
        COPYRIGHT_YEAR("Copyright year"),
        INTRO("Introduction length"),
        LOOP_LENGTH("Loop length"),
        LOOP_TIMES("Number of times to loop"),
        END("End length"),
        FADE("Fade length"),
        MUTED("Muted voices (1 bit for each muted voice"),
        MIXING("Mixing (preamp) level");

    Xid6Tag(String text) {
        this.text = text;
    }
    private final String text;

    public String toString() {
        return text;
    }

}

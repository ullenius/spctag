package se.anosh.spctag.emulator.factory;

public interface Emulator {

    Name getName();
    int code();

    public enum Name {
        Unknown,
        Other,
        ZSNES,
        Snes9x,
        ZST2SPC,
        SNEShout,
        ZSNES_W,
        Snes9xpp,
        SNESGT

    }

}

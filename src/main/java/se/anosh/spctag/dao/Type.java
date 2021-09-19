package se.anosh.spctag.dao;

enum Type {
    TEXT(256),
    OST(2),
    DATA(1),
    NUMBER(4),
    INTRO(4),
    MUTED(1),
    YEAR(2);

    private final int size;

    Type(int size) {
        this.size = size;
    }

    int size() {
        return size;
    }
}

package ar.edu.itba.paw.models.room;

public enum RoomType {
    SIMPLE, DOUBLE, TRIPLE;

    @Override
    public String toString() {
        return this + "ROOM";
    }
}

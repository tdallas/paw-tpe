package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.room.Room;

import java.util.List;

public interface RoomService {

    List<Room> getRoomsList();

    Room getRoom(long roomID);
}
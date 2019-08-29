package ar.edu.itba.paw.interfaces;

import java.util.List;

public interface RoomService {

    List<String> getRoomsList();

    String getRoom(long roomID);

    String getARoom();

}

package ar.edu.itba.paw.interfaces;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    List<String> getRoomsList();

    String getRoom(long roomID);

    String getARoom();

    String checkin(LocalDate startDate, LocalDate endDate);

    String checkingInto(long roomID, LocalDate startDate, LocalDate endDate);

    String checkout(long roomID);

}

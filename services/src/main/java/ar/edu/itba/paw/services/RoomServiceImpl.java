package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.RoomService;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RoomServiceImpl implements RoomService {

    // TODO: this is a placeholder. Update this to use persistence once implemented.
    private List<String> roomsList = Arrays.asList("101", "102", "201", "202");

    @Override
    public List<String> getRoomsList() {
        return roomsList;
    }

    @Override
    public String getRoom(long roomID) {
        // TODO: make room IDs
        return roomsList.get((int) roomID);
    }

    @Override
    public String getARoom() {
        return roomsList.get(0);
    }

}

package ar.edu.itba.paw.interfaces.dtos;

import ar.edu.itba.paw.models.room.Room;
import ar.edu.itba.paw.models.room.RoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoomResponse {
    private int number;
    private RoomType roomType;
    private long id;
    private boolean freeNow;

    public static RoomResponse fromRoom(Room room) {
        final RoomResponse rDto = new RoomResponse();

        rDto.id = room.getId();
        rDto.freeNow = room.isFreeNow();
        rDto.roomType = room.getRoomType();
        rDto.number = room.getNumber();

        return rDto;
    }
}

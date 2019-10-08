package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.models.dtos.RoomReservationDTO;
import ar.edu.itba.paw.models.reservation.Reservation;
import ar.edu.itba.paw.models.room.Room;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface RoomService {

    List<Room> getRoomsList();

    Room getRoom(long roomID);

    void doReservation(Reservation reserva) throws EntityNotFoundException;

    void reservateRoom(long roomId, Reservation reservation);

    void freeRoom(long roomId);

    List<RoomReservationDTO> findAllFreeBetweenDates(LocalDate startDate, LocalDate endDate);

    List<RoomReservationDTO> findAllBetweenDatesAndEmail(String startDate, String endDate, String email);

    List<RoomReservationDTO> getRoomsReservedActive();

}

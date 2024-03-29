package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.dtos.ReservationResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.occupant.Occupant;
import ar.edu.itba.paw.models.reservation.Reservation;

import java.util.Calendar;
import java.util.List;

public interface ReservationService {
    ReservationResponse getReservationById(long reservationId) throws EntityNotFoundException;

    Reservation getReservationByHash(String hash) throws EntityNotFoundException;

    boolean activeReservation(long reservationId) throws RequestInvalidException;

    void inactiveReservation(long reservationId) throws RequestInvalidException;

    PaginatedDTO<ReservationResponse> getAll(int page, int pageSize);

    PaginatedDTO<ReservationResponse> findAllBetweenDatesOrEmailAndSurname(Calendar startDate, Calendar endDate, String email, String occupantSurname, int page, int pageSize);

    PaginatedDTO<ReservationResponse> getRoomsReservedActive(int page, int pageSize);

    ReservationResponse doReservation(long roomId, String userEmail, Calendar startDate, Calendar endDate) throws RequestInvalidException;

    boolean isRoomFreeOnDate(long roomId, Calendar startDate, Calendar endDate) throws RequestInvalidException;

    void registerOccupants(String reservation_hash, List<Occupant> listOfOccupantsFromForm) throws EntityNotFoundException;
}

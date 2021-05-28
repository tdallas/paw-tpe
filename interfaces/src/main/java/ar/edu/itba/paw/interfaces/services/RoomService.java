package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.dtos.ReservationResponse;
import ar.edu.itba.paw.interfaces.dtos.RoomResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.models.dtos.CheckoutDTO;
import ar.edu.itba.paw.models.reservation.Reservation;

import java.util.Calendar;
import java.util.List;

public interface RoomService {
    RoomResponse getRoomById(long roomId) throws EntityNotFoundException;

    void reserveRoom(long roomId, Reservation reservation) throws RequestInvalidException, EntityNotFoundException;

    void freeRoom(long roomId);

    List<RoomResponse> findAllFreeBetweenDates(Calendar startDate, Calendar endDate);

    CheckoutDTO doCheckout(String reservationHash, String uriInfo) throws EntityNotFoundException, RequestInvalidException;

    ReservationResponse doCheckin(String reservationHash) throws RequestInvalidException, EntityNotFoundException;
}

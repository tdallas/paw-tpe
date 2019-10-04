package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.reservation.Reservation;

import java.util.List;

public interface ReservationService {
    Reservation getReservationByHash(String hash);

    void activeReservation(long reservationId);

    Reservation getActiveReservationByUserEmail(String email);
    List<Reservation> getNextReservationsByUserEmail(String email);
}

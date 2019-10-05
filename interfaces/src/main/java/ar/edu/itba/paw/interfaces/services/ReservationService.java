package ar.edu.itba.paw.interfaces.services;


import java.util.List;

public interface ReservationService {
    Reservation getReservationByHash(String hash);

    void activeReservation(long reservationId);

    List<Reservation> getAll();
}

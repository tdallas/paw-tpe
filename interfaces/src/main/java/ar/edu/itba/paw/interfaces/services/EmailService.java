package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.reservation.Reservation;

public interface EmailService {
    void sendConfirmationOfReservation(String to, String text);

    void sendCheckinEmail(Reservation reservation, String newPassword);

    void sendRateStayEmail(String reservationHash, String uriInfo);

    void sendConfirmationOfRate(String reservationHash);
}

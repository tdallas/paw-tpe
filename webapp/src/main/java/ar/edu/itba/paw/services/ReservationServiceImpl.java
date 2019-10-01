package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.ReservationDao;
import ar.edu.itba.paw.interfaces.services.ReservationService;
import ar.edu.itba.paw.models.reservation.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationServiceImpl implements ReservationService {

    private final ReservationDao reservationDao;

    @Autowired
    public ReservationServiceImpl(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    @Override
    public Reservation getReservationByHash(String hash) {
        return reservationDao.findReservationByHash(hash);
    }
}
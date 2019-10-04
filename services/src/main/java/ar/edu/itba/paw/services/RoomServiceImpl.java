package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.ReservationDao;
import ar.edu.itba.paw.interfaces.daos.RoomDao;
import ar.edu.itba.paw.interfaces.daos.UserDao;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.RoomService;
import ar.edu.itba.paw.models.reservation.Reservation;
import ar.edu.itba.paw.models.room.Room;
import ar.edu.itba.paw.models.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RoomServiceImpl implements RoomService {

    private final ReservationDao reservationDao;
    private final RoomDao roomDao;
    private final UserDao userDao;
    private final EmailService emailService;


    @Autowired
    public RoomServiceImpl(RoomDao roomDao, UserDao userDao, ReservationDao reservationDao, EmailService emailService) {
        this.reservationDao = reservationDao;
        this.roomDao = roomDao;
        this.userDao = userDao;

        this.emailService = emailService;
    }

    public List<Room> getRoomsList() {
        return roomDao.findAllFree();
    }

    @Override
    public Room getRoom(long roomID) {
        return roomDao.findById(roomID).orElse(null);
    }

    @Override
    public void doReservation(Reservation reserva) {
        User user = userDao.findByEmail(reserva.getUserEmail());
        if (user != null) {
            reserva.setUserId(user.getId());
        } else {
            reserva.setUserId(userDao.save(new User(reserva.getUserEmail())).getId());
        }
        reservationDao.save(reserva);
        emailService.sendConfirmationOfReservation(reserva.getUserEmail(), "Reserva confirmada",
                "Su reserva ha sido confirmada! " +
                        "Hash de la reserva: " + reserva.getHash());
    }

    public void reservateRoom(long roomID){
        roomDao.reservateRoom(roomID);
    }
}

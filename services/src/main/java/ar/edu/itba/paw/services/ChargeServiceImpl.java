package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.ChargeDao;
import ar.edu.itba.paw.interfaces.daos.ReservationDao;
import ar.edu.itba.paw.interfaces.daos.RoomDao;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.ChargeService;
import ar.edu.itba.paw.models.charge.Charge;
import ar.edu.itba.paw.models.reservation.Reservation;
import ar.edu.itba.paw.models.room.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ChargeServiceImpl implements ChargeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChargeServiceImpl.class);

    private final ReservationDao reservationDao;
    private final ChargeDao chargeDao;
    private final RoomDao roomDao;

    public ChargeServiceImpl(ReservationDao reservationDao, ChargeDao chargeDao, RoomDao roomDao) {
        this.reservationDao = reservationDao;
        this.chargeDao = chargeDao;
        this.roomDao = roomDao;
    }

    @Override
    public List<Charge> getAllChargesByReservationId(long reservationId) throws RequestInvalidException {
        LOGGER.debug("Getting all current charges for reservation with id " + reservationId);
        Optional<Reservation> reservationOptional = reservationDao.findById(reservationId);
        if (!reservationOptional.isPresent() || !reservationOptional.get().isActive()) {
            throw new RequestInvalidException();
        }
        return chargeDao.findChargeByReservationId(reservationId);
    }

    @Override
    public double sumCharge(long reservationId) {
        LOGGER.debug("Getting the balance of reservation with id" + reservationId);
        return chargeDao.sumCharge(reservationId);
    }

    @Override
    public List<Charge> getAllChargesNotDelivered() {
        return chargeDao.findAllChargesNotDelivered();
    }

    @Override
    @Transactional
    public int setChargeToDelivered(long chargeId) throws RequestInvalidException, EntityNotFoundException {
        Charge charge = chargeDao.findById(chargeId).orElseThrow(() ->
                new EntityNotFoundException("Can't find charge with id " + chargeId));
        if (charge.isDelivered()) {
            throw new RequestInvalidException();
        }
        return chargeDao.updateChargeToDelivered(chargeId);
    }

    @Override
    public int setChargesToDelivered(long roomId) throws RequestInvalidException, EntityNotFoundException {
        Optional<Room> room = roomDao.findById(roomId);
        if (room.isPresent()) {
            List<Charge> chargeList = chargeDao.findChargesByRoomNumber(room.get().getNumber());
            for (Charge c : chargeList) {
                if (c.isDelivered()) {
                    LOGGER.debug("Charge with ID: " + c.getId() + " was already delivered.");
                    throw new RequestInvalidException();
                }
            }
            return chargeDao.updateChargesToDelivered(roomId);
        }
        throw new EntityNotFoundException("Can't find room with id " + roomId);
    }
}

package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.ReservationDao;
import ar.edu.itba.paw.interfaces.daos.RoomDao;
import ar.edu.itba.paw.interfaces.dtos.ReservationResponse;
import ar.edu.itba.paw.interfaces.dtos.RoomResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.charge.Charge;
import ar.edu.itba.paw.models.dtos.CheckoutDTO;
import ar.edu.itba.paw.models.reservation.Reservation;
import ar.edu.itba.paw.models.room.Room;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomServiceImpl implements RoomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomDao roomDao;
    private final ReservationDao reservationDao;
    private final EmailService emailService;
    private final ReservationService reservationService;
    private final ChargeService chargeService;

    @Autowired
    public RoomServiceImpl(RoomDao roomDao, ReservationDao reservationDao, EmailService emailService,
                           ReservationService reservationService, ChargeService chargeService) {
        this.roomDao = roomDao;
        this.reservationDao = reservationDao;
        this.emailService = emailService;
        this.reservationService = reservationService;
        this.chargeService = chargeService;
    }

    @Override
    @Transactional
    public RoomResponse getRoomById(long roomId) throws EntityNotFoundException {
        Optional<Room> possibleRoom = roomDao.findById(roomId);
        if (possibleRoom.isPresent()) {
            return RoomResponse.fromRoom(possibleRoom.get());
        }
        throw new EntityNotFoundException("Can't find room with id " + roomId);
    }

    @Override
    public void reserveRoom(long roomID, Reservation reservation) {
        List<Reservation> hadActiveReservations = reservationDao.findActiveReservationsByEmail(reservation.getUserEmail());
        roomDao.reserveRoom(roomID);
        emailService.sendCheckinEmail(reservation, hadActiveReservations.isEmpty());
    }

    @Override
    public void freeRoom(long roomId) {
        roomDao.freeRoom(roomId);
    }

    @Override
    public List<RoomResponse> findAllFreeBetweenDates(Calendar startDate, Calendar endDate) {
        return roomDao.findAllFreeBetweenDates(startDate, endDate)
                .stream().map(room -> new RoomResponse(room.getNumber(), room.getRoomType(), room.getId(), room.isFreeNow()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CheckoutDTO doCheckout(String reservationHash, String uriInfo) throws EntityNotFoundException, RequestInvalidException {
        Reservation reservation = reservationService.getReservationByHash(reservationHash.trim());
        if (!reservation.isActive()) {
            throw new RequestInvalidException();
        }
        LOGGER.info("Request received to do the check-out on reservation with hash: " + reservationHash);
        freeRoom(reservation.getRoom().getId());
        // FIXME delete this
        List<Charge> charges = chargeService.getAllChargesByReservationId(reservation.getId());
        CheckoutDTO checkoutDTO = new CheckoutDTO(charges,
                charges.size() > 0 ? chargeService.sumCharge(reservation.getId()) : 0d);
        reservationService.inactiveReservation(reservation.getId());
        emailService.sendRateStayEmail(reservationHash, uriInfo);
        return checkoutDTO;
    }

    @Override
    @Transactional
    public ReservationResponse doCheckin(String reservationHash) throws RequestInvalidException, EntityNotFoundException {
        Reservation reservation = reservationService.getReservationByHash(reservationHash.trim());
        if (reservation.isActive()) {
            throw new RequestInvalidException();
        }
        reserveRoom(reservation.getRoom().getId(), reservation);
        if (reservationService.activeReservation(reservation.getId())) {
            return ReservationResponse.fromReservation(reservation);
        }
        return null;
    }

}

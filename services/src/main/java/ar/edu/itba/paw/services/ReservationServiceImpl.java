package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.OccupantDao;
import ar.edu.itba.paw.interfaces.daos.ReservationDao;
import ar.edu.itba.paw.interfaces.daos.RoomDao;
import ar.edu.itba.paw.interfaces.dtos.ReservationResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.ReservationService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.occupant.Occupant;
import ar.edu.itba.paw.models.reservation.Reservation;
import ar.edu.itba.paw.models.room.Room;
import ar.edu.itba.paw.models.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReservationServiceImpl implements ReservationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final OccupantDao occupantDao;
    private final ReservationDao reservationDao;
    private final RoomDao roomDao;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public ReservationServiceImpl(
            OccupantDao occupantDao,
            ReservationDao reservationDao,
            RoomDao roomDao,
            UserService userService,
            EmailService emailService) {
        this.occupantDao = occupantDao;
        this.reservationDao = reservationDao;
        this.roomDao = roomDao;
        this.userService = userService;
        this.emailService = emailService;

    }

    @Override
    @Transactional
    public ReservationResponse getReservationById(long reservationId) throws EntityNotFoundException {
        LOGGER.info("About to get reservation with id " + reservationId);
        return ReservationResponse.fromReservation(reservationDao.findById(reservationId).orElseThrow(
            () -> new EntityNotFoundException("Reservation with id " + reservationId + " not found")));
    }

    @Override
    public Reservation getReservationByHash(String hash) throws EntityNotFoundException {
        LOGGER.info("About to get reservation with hash " + hash);
        return reservationDao.findReservationByHash(hash.trim()).orElseThrow(
                () -> new EntityNotFoundException("Reservation of hash " + hash + " not found"));
    }

    @Override
    public boolean activeReservation(long reservationId) throws RequestInvalidException {
        LOGGER.info("About to set reservation with id " + reservationId + " to active");
        Optional<Reservation> possibleReservation = reservationDao.findById(reservationId);
        if (!possibleReservation.isPresent()) {
            return false;
        }
        if (possibleReservation.get().isActive()) {
            throw new RequestInvalidException();
        }
        return reservationDao.updateActive(reservationId, true);
    }

    @Override
    public void inactiveReservation(long reservationId) throws RequestInvalidException {
        LOGGER.info("About to set reservation with id " + reservationId + " to unactivated");
        if (!reservationDao.findById(reservationId).orElseThrow(RequestInvalidException::new).isActive()) {
            throw new RequestInvalidException();
        }
        reservationDao.updateActive(reservationId, false);
    }

    @Override
    @Transactional
    public PaginatedDTO<ReservationResponse> getAll(int page, int pageSize) {
        if (pageSize < 1 || page < 1) throw new IndexOutOfBoundsException("Pagination requested invalid.");
        LOGGER.info("About to get all the confirmed reservations");
        PaginatedDTO<Reservation> reservations = reservationDao.findAll(page, pageSize);
        return new PaginatedDTO<>(reservations.getList()
                .stream().map(ReservationResponse::fromReservation).collect(Collectors.toList()),
                reservations.getMaxItems());
    }

    private boolean isValidDate(Calendar startDate, Calendar endDate) {
        return startDate.getTimeInMillis() < endDate.getTimeInMillis();
    }

    @Override
    public boolean isRoomFreeOnDate(long roomId, Calendar startDate, Calendar endDate) {
        return reservationDao.isRoomFreeOnDate(roomId, startDate, endDate);
    }

    @Override
    public void registerOccupants(String reservationHash, List<Occupant> listOfOccupantsFromForm)
            throws EntityNotFoundException {
        Reservation reservation = reservationDao.findReservationByHash(reservationHash)
                .orElseThrow(() -> new EntityNotFoundException("Reservation was not found"));
        listOfOccupantsFromForm
                .parallelStream()
                .peek(occupant -> occupant.setReservation(reservation))
                .forEach(occupantDao::save);
    }

    @Override
    @Transactional
    public ReservationResponse doReservation(long roomId, String userEmail, Calendar startDate, Calendar endDate)
            throws RequestInvalidException {
        final boolean isValidDate = isValidDate(startDate, endDate);
        final boolean isRoomFree = isRoomFreeOnDate(roomId, startDate, endDate);
        if (!isValidDate || !isRoomFree) {
            throw new RequestInvalidException(
                    !isValidDate ? "The dates are invalid" : "The room is not available on selected dates");
        }
        LOGGER.info("Looking if there is already a user created with email " + userEmail);
        User user = userService.getUserForReservation(userEmail);
        LOGGER.info("Getting room...");
        Room room = roomDao.findById(roomId).orElseThrow(javax.persistence.EntityNotFoundException::new);
        LOGGER.info("Saving reservation...");
        Reservation reservation = reservationDao.save(new Reservation(room, userEmail, startDate, endDate, user));
        LOGGER.info("Sending email with confirmation of reservation to user");
        emailService.sendConfirmationOfReservation(userEmail, reservation.getHash());
        return ReservationResponse.fromReservation(reservation);
    }

    @Override
    @Transactional
    public PaginatedDTO<ReservationResponse> findAllBetweenDatesOrEmailAndSurname(
            Calendar startDate, Calendar endDate, String email, String occupantSurname,
            int page, int pageSize) {
        if (pageSize < 1 || page < 1) throw new IndexOutOfBoundsException("Pagination requested invalid.");
        PaginatedDTO<Reservation> reservations = reservationDao
                .findAllBetweenDatesOrEmailAndSurname(startDate, endDate, email, occupantSurname, page, pageSize);
        return new PaginatedDTO<>(reservations.getList()
                .stream().map(ReservationResponse::fromReservation).collect(Collectors.toList()),
                reservations.getMaxItems());
    }

    @Override
    @Transactional
    public PaginatedDTO<ReservationResponse> getRoomsReservedActive(int page, int pageSize) {
        if (pageSize < 1 || page < 1) throw new IndexOutOfBoundsException("Pagination requested invalid.");
        PaginatedDTO<Reservation> paginatedReservationResponseList = reservationDao
                .getActiveReservations(page, pageSize);
        return new PaginatedDTO<>(paginatedReservationResponseList.getList()
                .stream().map(ReservationResponse::fromReservation).collect(Collectors.toList()),
                paginatedReservationResponseList.getMaxItems());
    }
}

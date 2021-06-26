package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.HelpDao;
import ar.edu.itba.paw.interfaces.daos.ReservationDao;
import ar.edu.itba.paw.interfaces.dtos.HelpResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.HelpService;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.help.Help;
import ar.edu.itba.paw.models.help.HelpStep;
import ar.edu.itba.paw.models.reservation.Reservation;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Component
public class HelpServiceImpl implements HelpService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ReservationDao reservationDao;
    private final HelpDao helpDao;

    @Autowired
    public HelpServiceImpl(ReservationDao reservationDao, HelpDao helpDao) {
        this.reservationDao = reservationDao;
        this.helpDao = helpDao;
    }

    @Override
    public PaginatedDTO<HelpResponse> getAllHelpRequestsByReservationId(long reservationId, int page, int pageSize)
        throws EntityNotFoundException {
        if (pageSize < 1 || page < 1) throw new IndexOutOfBoundsException("Pagination requested invalid.");
        LOGGER.info("Fetching reservation by id: " + reservationId);
        Reservation reservation = reservationDao.findById(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("Can't find reservation with id " + reservationId));
        LOGGER.info("Fetching all help request for reservation: " + reservationId);
        PaginatedDTO<Help> helpRequests = helpDao
            .findHelpRequestsByReservationHash(reservation.getId(), page, pageSize);
        return new PaginatedDTO<>(helpRequests.getList()
                .stream().map(HelpResponse::fromHelpRequest).collect(Collectors.toList()),
                helpRequests.getMaxItems());
    }

    @Override
    public PaginatedDTO<HelpResponse> getAllRequestsNotAttendedTo(int page, int pageSize) {
        if (pageSize < 1 || page < 1) throw new IndexOutOfBoundsException("Pagination requested invalid.");
        PaginatedDTO<Help> helpRequests = helpDao.findAllHelpRequestsNotSentFor(page, pageSize);
        return new PaginatedDTO<>(helpRequests.getList()
                .stream().map(HelpResponse::fromHelpRequest).collect(Collectors.toList()),
                helpRequests.getMaxItems());
    }

    @Override
    public PaginatedDTO<HelpResponse> getAllRequestsThatRequireAction(int page, int pageSize) {
        if (pageSize < 1 || page < 1) throw new IndexOutOfBoundsException("Pagination requested invalid.");
        PaginatedDTO<Help> helpRequests = helpDao.findAllHelpRequestsNotResolved(page, pageSize);
        return new PaginatedDTO<>(helpRequests.getList()
                .stream().map(HelpResponse::fromHelpRequest).collect(Collectors.toList()),
                helpRequests.getMaxItems());
    }

    @Transactional
    @Override
    public boolean updateStatus(long helpId, HelpStep status)
            throws RequestInvalidException, EntityNotFoundException {
        Help helpRequest = helpDao.findById(helpId)
            .orElseThrow(() -> new EntityNotFoundException("Can't find help request with id " + helpId));
        if(helpRequest != null) {
            if (status == HelpStep.REQUIRES_FURTHER_ACTION) {
                return this.setRequestToRequiresFurtherAction(helpId);
            } else if (status == HelpStep.RESOLVED) {
                return this.setRequestToResolved(helpId);
            }
        }
        return false;
    }

    @Transactional
    @Override
    public boolean setRequestToResolved(long helpId) throws RequestInvalidException, EntityNotFoundException {
        Help helpRequest = helpDao.findById(helpId)
            .orElseThrow(() -> new EntityNotFoundException("Can't find help request with id " + helpId));
        if(helpRequest != null) {
            return helpDao.updateToHelpRequestResolved(helpId);
        }
        throw new RequestInvalidException();
    }

    @Transactional
    @Override
    public boolean setRequestToRequiresFurtherAction(long helpId)
        throws RequestInvalidException, EntityNotFoundException {
        Help helpRequest = helpDao.findById(helpId)
            .orElseThrow(() -> new EntityNotFoundException("Can't find help request with id " + helpId));
        if(helpRequest != null) {
            return helpDao.updateRequestToRequiresFurtherAction(helpId);
        }
        throw new RequestInvalidException();
    }

    @Override
    @Transactional
    public HelpResponse getHelpById(long helpId) throws EntityNotFoundException {
        Optional<Help> possibleHelpRequest = helpDao.findById(helpId);
        if (possibleHelpRequest.isPresent()) {
            return HelpResponse.fromHelpRequest(possibleHelpRequest.get());
        }
        throw new EntityNotFoundException("Can't find help request with id " + helpId);
    }

    @Transactional
    @Override
    public String requestHelp(String text, long reservationId) throws EntityNotFoundException {
        Reservation reservation = reservationDao.findById(reservationId)
            .orElseThrow(() -> new EntityNotFoundException("Can't find reservation with id " + reservationId));
        if(text.length() > 0 && isValidString(text)) {
            return helpDao.save(new Help(text, reservation)).getHelpText();
        }
        return null;
    }

    private boolean isValidString(String text) {
        return text.matches("^.*[^a-zA-Z0-9áéíóúüñÁÉÍÓÚÑ ].*$");
    }
}

package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.dtos.HelpResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;

public interface HelpService {
    HelpResponse getHelpById(long helpId) throws EntityNotFoundException;

    String requestHelp(String text, long reservationId) throws EntityNotFoundException;

    PaginatedDTO<HelpResponse> getAllHelpRequestsByReservationId(long reservationId, int page, int pageSize)
        throws RequestInvalidException, EntityNotFoundException;

    PaginatedDTO<HelpResponse> getAllRequestsNotAttendedTo(int page, int pageSize);

    PaginatedDTO<HelpResponse> getAllRequestsThatRequireAction(int page, int pageSize);

    boolean markResolved(long helpId) throws EntityNotFoundException;
}

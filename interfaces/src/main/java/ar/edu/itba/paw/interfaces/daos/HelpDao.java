package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.help.Help;

public interface HelpDao extends SimpleDao<ar.edu.itba.paw.models.help.Help> {
    PaginatedDTO<Help> findHelpRequestsByReservationHash(long reservationId, int page, int pageSize);

    PaginatedDTO<Help> findAllHelpRequestsNotSentFor(int page, int pageSize);

    PaginatedDTO<Help> findAllHelpRequestsNotResolved(int page, int pageSize);

    boolean markHelpRequestAsResolved(long helpId);
}

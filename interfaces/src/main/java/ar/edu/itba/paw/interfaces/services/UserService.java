package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.dtos.*;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.models.charge.Charge;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.user.User;

import java.util.List;

public interface UserService {
    UserResponse getUserById(long userId) throws EntityNotFoundException;

    List<ChargesByUserResponse> checkProductsPurchasedByUserByReservationId(String userEmail, long reservationId)
        throws EntityNotFoundException;

    List<ActiveReservationResponse> findActiveReservations(String userEmail);

    PaginatedDTO<ProductResponse> getProducts(int page, int pageSize);

    Charge addCharge(long productId, long reservationId) throws EntityNotFoundException;

    User getUserForReservation(String userEmail);

    String createNewPassword(long id) throws EntityNotFoundException;

    HelpResponse requestHelp(String text, long reservationId) throws EntityNotFoundException;

    void rateStay(String rate, String reservationHash) throws EntityNotFoundException, RequestInvalidException;

    HelpResponse getHelpRequestById(long reservationId, long helpId) throws EntityNotFoundException;

    ChargeDeliveryResponse getCharge(long chargeId) throws EntityNotFoundException;
}

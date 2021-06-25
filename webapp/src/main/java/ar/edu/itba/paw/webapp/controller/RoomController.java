package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dtos.ChargeDeliveryResponse;
import ar.edu.itba.paw.interfaces.dtos.ReservationResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.ChargeService;
import ar.edu.itba.paw.interfaces.services.MessageSourceExternalizer;
import ar.edu.itba.paw.interfaces.services.ReservationService;
import ar.edu.itba.paw.interfaces.services.RoomService;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.occupant.Occupant;
import ar.edu.itba.paw.models.reservation.Reservation;
import ar.edu.itba.paw.webapp.dtos.OccupantsRequest;
import ar.edu.itba.paw.webapp.dtos.ReservationRequest;
import ar.edu.itba.paw.webapp.utils.JsonToCalendar;
import java.net.URI;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.stream.Collectors;

@Component
@Controller
@Path("rooms")
public class RoomController extends SimpleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomController.class);
    public static final String DEFAULT_FIRST_PAGE = "1";
    public static final String DEFAULT_PAGE_SIZE = "20";

    private final RoomService roomService;
    private final ReservationService reservationService;
    private final ChargeService chargeService;
    private final MessageSourceExternalizer messageSourceExternalizer;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public RoomController(RoomService roomService, ReservationService reservationService, ChargeService chargeService,
        MessageSourceExternalizer messageSourceExternalizer) {
        this.roomService = roomService;
        this.reservationService = reservationService;
        this.chargeService = chargeService;
        this.messageSourceExternalizer = messageSourceExternalizer;
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAllRooms(@QueryParam("page") @DefaultValue(DEFAULT_FIRST_PAGE) int page,
                                @QueryParam("limit") @DefaultValue(DEFAULT_PAGE_SIZE) int limit) {
        LOGGER.info("Request received to retrieve whole roomsList");
        PaginatedDTO<ReservationResponse> reservations;
        try {
            reservations = reservationService.getRoomsReservedActive(page, limit);
        } catch (IndexOutOfBoundsException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer.getMessage("error.404"));
        }
        return sendPaginatedResponse(page, limit, reservations.getMaxItems(), reservations.getList(),
                uriInfo.getAbsolutePathBuilder());
    }

    @GET
    @Path("/reservations")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAllReservations(@QueryParam("startDate")  String startDate,
                                       @QueryParam("endDate")  String endDate,
                                       @QueryParam("email")  String email,
                                       @QueryParam("lastName")  String lastName,
                                       @QueryParam("page") @DefaultValue(DEFAULT_FIRST_PAGE) int page,
                                       @QueryParam("limit") @DefaultValue(DEFAULT_PAGE_SIZE) int limit) {
        LOGGER.info("Request received to retrieve reservations.");
        PaginatedDTO<ReservationResponse> reservations = null;
        try {
            if (startDate == null && endDate == null && email == null && lastName == null) {
                LOGGER.info("Getting all reservations starting at page " + page);
                reservations = reservationService.getAll(page, limit);
            } else {
                if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
                    LOGGER.info("Getting all reservations between " + startDate + " and " + endDate);
                    Calendar startDateCalendar = JsonToCalendar.unmarshal(startDate);
                    Calendar endDateCalendar = JsonToCalendar.unmarshal(endDate);
                    if (startDateCalendar.before(endDateCalendar)) {
                        LOGGER.info("Valid dates received, continuing with fetch...");
                        reservations = reservationService.findAllBetweenDatesOrEmailAndSurname(
                                startDateCalendar, endDateCalendar, email, lastName, page, limit);
                    } else {
                        LOGGER.info("Request received with invalid dates.");
                        return sendErrorMessageResponse(Status.BAD_REQUEST,
                            messageSourceExternalizer.getMessage("reservation.date.error"));
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            LOGGER.info(e.getMessage());
            System.out.println(e.getMessage());
            // fixme: when does this error happen?
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                messageSourceExternalizer.getMessage("error.404"));
        } catch (ParseException e) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                messageSourceExternalizer.getMessage("reservation.date.error"));
        }
        if (reservations == null) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                messageSourceExternalizer.getMessage("error.404"));
        }
        LOGGER.info("Reservation(s) found!");
        return sendPaginatedResponse(page, limit, reservations.getMaxItems(), reservations.getList(),
                uriInfo.getAbsolutePathBuilder());
    }

    @GET
    @Path("/reservation/{reservationId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getReservation(@PathParam("reservationId") final long reservationId) {
        ReservationResponse reservation;
        try {
            reservation = reservationService.getReservationById(reservationId);
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer.getMessage("error.404"));
        }
        if (reservation == null) {
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer.getMessage("error.404"));
        }
        return Response.ok(reservation).build();
    }

    @POST
    @Path("/reservation")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response reservationPost(ReservationRequest reservationRequest) {
        LOGGER.info("Request received to do a reservation on room with id: " + reservationRequest.getRoomId());
        ReservationResponse reservation;
        try {
            reservation = reservationService.doReservation(reservationRequest.getRoomId(),
                reservationRequest.getUserEmail(), reservationRequest.getStartDate(), reservationRequest.getEndDate());
        } catch (RequestInvalidException e) {
            return sendErrorMessageResponse(Status.CONFLICT,
                messageSourceExternalizer.getMessage("reservation.invalid"));
        }
        return Response.created(URI.create(uriInfo.getRequestUri() + "/" + reservation.getId()))
            .entity(reservation).build();
    }

    @POST
    @Path("/checkin/{reservationHash}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response checkinPost(@PathParam("reservationHash") final String reservationHash) {
        LOGGER.info("Request received to do the check-in on reservation with hash: " + reservationHash);
        ReservationResponse reservation;
        try {
            reservation = roomService.doCheckin(reservationHash);
        } catch (RequestInvalidException e) {
            return sendErrorMessageResponse(Status.CONFLICT,
                messageSourceExternalizer.getMessage("reservation.checkin.error"));
        } catch (EntityNotFoundException | NoResultException e) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                    messageSourceExternalizer.getMessage("error.404"));
        }
        if (reservation != null) {
//           todo: erase System.out.println(uriInfo.getBaseUri().toString());  http://localhost:8080/api/
            return Response
                    .noContent()
                    .contentLocation(URI.create(uriInfo.getBaseUri() + "rooms/reservation/" + reservation.getId()))
                    .entity(reservation)
                    .build();
        } else {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                    messageSourceExternalizer.getMessage("error.404"));
        }
    }

    @POST
    @Path("/checkout/{reservationHash}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response checkoutPost(@PathParam(value = "reservationHash") final String reservationHash) {
        Reservation reservation;
        try {
            reservation = reservationService.getReservationByHash(reservationHash);
            roomService.doCheckout(reservationHash, uriInfo.getBaseUri().toString());
            return Response
                    .noContent()
                    .contentLocation(URI.create(uriInfo.getBaseUri() + "rooms/reservation/" + reservation.getId()))
                    .entity(chargeService.checkProductsPurchasedInCheckOut(reservationHash))
                    .build();
        } catch (EntityNotFoundException | NoResultException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("reservation.notfound"));
        } catch (RequestInvalidException e) {
            return sendErrorMessageResponse(Status.CONFLICT,
                messageSourceExternalizer.getMessage("reservation.checkout.error"));
        }
    }

    @GET
    @Path("/free")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response reservation(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
            Calendar startDateCalendar;
            Calendar endDateCalendar;
            try {
                startDateCalendar = JsonToCalendar.unmarshal(startDate);
                endDateCalendar = JsonToCalendar.unmarshal(endDate);
            } catch (Exception e) {
                return sendErrorMessageResponse(Status.INTERNAL_SERVER_ERROR,
                    messageSourceExternalizer.getMessage("error.500"));
            }
            if (startDateCalendar.before(endDateCalendar)) {
                return Response.ok(roomService.findAllFreeBetweenDates(startDateCalendar, endDateCalendar)).build();
            }
        }
        return sendErrorMessageResponse(Status.BAD_REQUEST,
            messageSourceExternalizer.getMessage("reservation.format.error"));
    }

    @GET
    @Path("/orders")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getUndeliveredOrders(@QueryParam("page") @DefaultValue(DEFAULT_FIRST_PAGE) int page,
                                         @QueryParam("limit") @DefaultValue(DEFAULT_PAGE_SIZE) int limit) {
        LOGGER.info("Request received to retrieve all undelivered orders");
        PaginatedDTO<ChargeDeliveryResponse> orders;
        try {
            orders = chargeService.getAllChargesNotDelivered(page, limit);
        } catch (IndexOutOfBoundsException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer.getMessage("error.404"));
        }
        return sendPaginatedResponse(page, limit, orders.getMaxItems(), orders.getList(),
                uriInfo.getAbsolutePathBuilder());
    }

    @POST
    @Path("/orders/{roomId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response sendOrder(@PathParam(value = "roomId") Long roomId) {
        LOGGER.info("Order request sent for room with id: " + roomId);
        try {
            chargeService.setChargesToDelivered(roomId);
        } catch (IndexOutOfBoundsException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("error.404"));
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                messageSourceExternalizer.getMessage("reservation.notfound"));
        } catch (RequestInvalidException e) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                    messageSourceExternalizer.getMessage("orders.already"));
        }
        return Response.noContent().build();
    }

    @POST
    @Path("/occupants/{reservationHash}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response registrationPost(@PathParam("reservationHash") String reservationHash,
                                     @RequestBody OccupantsRequest occupantsRequest) {
        LOGGER.info("Attempted to access registration form");
        if (reservationHash == null) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                    messageSourceExternalizer.getMessage("reservation.notfound"));
        }
        if (CollectionUtils.isEmpty(occupantsRequest.getOccupants())) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                    messageSourceExternalizer.getMessage("reservation.occupants.empty"));
        }
        LOGGER.info("Attempted to register occupants on reservation hash " + reservationHash);
        try {
            reservationService.registerOccupants(reservationHash.trim(),
                occupantsRequest.getOccupants()
                    .stream()
                    .map(
                        occupantR -> new Occupant(
                            occupantR.getFirstName(), occupantR.getLastName())
                    )
                    .collect(Collectors.toList()));
            return Response
                    .noContent()
                    .contentLocation(URI.create(
                        uriInfo.getBaseUri() + "rooms/reservation/" + reservationService
                            .getReservationByHash(reservationHash).getId()))
                    .build();
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("reservation.notfound"));
        }
    }
}

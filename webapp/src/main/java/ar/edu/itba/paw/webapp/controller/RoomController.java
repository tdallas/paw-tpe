package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dtos.ReservationConfirmedResponse;
import ar.edu.itba.paw.interfaces.dtos.ReservationResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.ChargeService;
import ar.edu.itba.paw.interfaces.services.ReservationService;
import ar.edu.itba.paw.interfaces.services.RoomService;
import ar.edu.itba.paw.models.dtos.CheckoutDTO;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.occupant.Occupant;
import ar.edu.itba.paw.models.reservation.Reservation;
import ar.edu.itba.paw.models.room.Room;
import ar.edu.itba.paw.webapp.dtos.OccupantsRequest;
import ar.edu.itba.paw.webapp.dtos.ReservationRequest;
import ar.edu.itba.paw.webapp.utils.JsonToCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Controller
@Path("/rooms")
public class RoomController extends SimpleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomController.class);
    public static final String DEFAULT_FIRST_PAGE = "1";
    public static final String DEFAULT_PAGE_SIZE = "20";

    private final RoomService roomService;
    private final ReservationService reservationService;
    private final ChargeService chargeService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public RoomController(RoomService roomService, ReservationService reservationService, ChargeService chargeService) {
        this.roomService = roomService;
        this.reservationService = reservationService;
        this.chargeService = chargeService;
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAllRooms(@QueryParam("page") @DefaultValue(DEFAULT_FIRST_PAGE) int page,
                                @QueryParam("limit") @DefaultValue(DEFAULT_PAGE_SIZE) int limit) {
        LOGGER.debug("Request received to retrieve whole roomsList");
        PaginatedDTO<ReservationResponse> reservations;
        try {
            reservations = reservationService.getRoomsReservedActive(page, limit);
        } catch (IndexOutOfBoundsException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return sendPaginatedResponse(page, limit, reservations.getMaxItems(), new GenericEntity<List<ReservationResponse>>(reservations.getList()) {
        }, uriInfo.getAbsolutePathBuilder());
    }

    @POST
    @Path("/reservation")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response reservationPost(ReservationRequest reservationRequest)
            throws RequestInvalidException {
        LOGGER.debug("Request received to do a reservation on room with id: " + reservationRequest.getRoomId());
        final Reservation reservation = reservationService.doReservation(reservationRequest.getRoomId(),
                reservationRequest.getUserEmail(), reservationRequest.getStartDate(), reservationRequest.getEndDate());
        return Response.ok(new GenericEntity<ReservationConfirmedResponse>(new ReservationConfirmedResponse(reservation.getId(), reservation.getHash(), reservation.getRoom().getNumber())) {
        }).build();
    }

    @POST
    @Path("/checkin/{reservationId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response checkinPost(@PathParam("reservationId") final String reservationId) throws EntityNotFoundException {
        LOGGER.debug("Request received to do the check-in on reservation with hash: " + reservationId);
        ReservationResponse reservation;
        try {
            reservation = roomService.doCheckin(reservationId);
        } catch (RequestInvalidException e) {
            return Response.status(Response.Status.CONFLICT).build();
        } catch (EntityNotFoundException | NoResultException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (reservation != null) {
            return Response.ok(new GenericEntity<ReservationResponse>(reservation) {
            }).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/checkout/{reservationId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response checkoutPost(@PathParam(value = "reservationId") final String reservationId) throws RequestInvalidException, EntityNotFoundException {
        CheckoutDTO checkoutDTO = roomService.doCheckout(reservationId);
        // TODO: make front end show total to pay
        return Response.ok(checkoutDTO.getCharges()).build();
    }

    @GET
    @Path("/free")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response reservation(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) throws Exception {
        if (!StringUtils.isEmpty(startDate) && !StringUtils.isEmpty(endDate)) {
            Calendar startDateCalendar = new JsonToCalendar().unmarshal(startDate);
            Calendar endDateCalendar = new JsonToCalendar().unmarshal(endDate);
            if (startDateCalendar.before(endDateCalendar)) {
                return Response.ok(new GenericEntity<List<Room>>(roomService.findAllFreeBetweenDates(startDateCalendar, endDateCalendar)) {
                }).build();
            }
        }
        String message = "Expected 'startDate' and 'endDate' in format yyyy-mm-dd.";
        return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
    }

    @POST
    @Path("/orders/{roomId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response sendOrder(@PathParam(value = "roomId") Long roomId) throws Exception {
        LOGGER.debug("Order request sent for room with id: " + roomId);
        chargeService.setChargesToDelivered(roomId);
        return Response.ok().build();
    }

    @POST
    @Path("/occupants/{reservationHash}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response registrationPost(@PathParam("reservationHash") String reservationHash, OccupantsRequest occupantsRequest) throws EntityNotFoundException {
        LOGGER.debug("Attempted to access registration form");
        if (reservationHash != null && !CollectionUtils.isEmpty(occupantsRequest.getOccupants())) {
            LOGGER.debug("Attempted to register occupants on reservation hash " + reservationHash);
            reservationService.registerOccupants(reservationHash.trim(),
                    occupantsRequest.getOccupants()
                            .stream()
                            .map(occupantR -> new Occupant(occupantR.getFirstName(), occupantR.getLastName()))
                            .collect(Collectors.toList()));
            return Response.status(Response.Status.CREATED).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}

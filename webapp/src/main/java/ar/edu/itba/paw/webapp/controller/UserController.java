package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dtos.*;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.MessageSourceExternalizer;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.charge.Charge;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.webapp.dtos.ActiveReservationsResponse;
import ar.edu.itba.paw.webapp.dtos.HelpRequest;
import ar.edu.itba.paw.webapp.dtos.RateReservationRequest;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;

@Controller
@Path("user")
public class UserController extends SimpleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    public static final String DEFAULT_FIRST_PAGE = "1";
    public static final String DEFAULT_PAGE_SIZE = "20";

    private final UserService userService;
    private final MessageSourceExternalizer messageSourceExternalizer;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController(UserService userService, MessageSourceExternalizer messageSourceExternalizer) {
        this.userService = userService;
        this.messageSourceExternalizer = messageSourceExternalizer;
    }

    @GET
    @Path("/{reservationId}/expenses")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response boughtProducts(@PathParam(value = "reservationId") long reservationId,
                                   @QueryParam("page") @DefaultValue(DEFAULT_FIRST_PAGE) int page,
                                   @QueryParam("limit") @DefaultValue(DEFAULT_PAGE_SIZE) int limit,
                                   @Context SecurityContext securityContext) {
        LOGGER.info("Request received to retrieve all expenses on reservation with id " + reservationId);
        List<ChargesByUserResponse> chargesByUser;
        try {
            chargesByUser = userService
                .checkProductsPurchasedByUserByReservationId(getUserEmailFromJwt(securityContext), reservationId);
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                messageSourceExternalizer.getMessage("reservation.notfound"));
        }
        return Response.ok(chargesByUser).build();
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    public Response getActiveReservations(@QueryParam("page") @DefaultValue(DEFAULT_FIRST_PAGE) int page,
                                          @QueryParam("limit") @DefaultValue(DEFAULT_PAGE_SIZE) int limit,
                                          @Context SecurityContext securityContext) {
        List<ActiveReservationResponse> activeReservations = userService
            .findActiveReservations(getUserEmailFromJwt(securityContext));
        return Response.ok(new ActiveReservationsResponse(activeReservations)).build();
    }

    @GET
    @Path("/{reservationId}/products")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAllProducts(@PathParam("reservationId") long reservationId,
                                   @QueryParam("page") @DefaultValue(DEFAULT_FIRST_PAGE) int page,
                                   @QueryParam("limit") @DefaultValue(DEFAULT_PAGE_SIZE) int limit) {
        LOGGER.info("Request received to retrieve all products list");
        PaginatedDTO<ProductResponse> productList;
        try {
            productList = userService.getProducts(page, limit);
        } catch (IndexOutOfBoundsException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer.getMessage("error.404"));
        }
        return sendPaginatedResponse(page, limit, productList.getMaxItems(), productList.getList(),
                uriInfo.getAbsolutePathBuilder());
    }

    @GET
    @Path("/{reservationId}/products/{chargeId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getCharge(@PathParam("reservationId") long reservationId, @PathParam("chargeId") long chargeId) {
        LOGGER.info("Request received to retrieve user's charge with id " + chargeId);
        ChargeDeliveryResponse chargeResponse;
        try {
            chargeResponse = userService.getCharge(chargeId);
        } catch (IndexOutOfBoundsException | EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("user.expenses.notfound"));
        }
        return Response.ok(chargeResponse).build();
    }

    @POST
    @Path("/{reservationId}/products/{productId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response buyProduct(@PathParam("reservationId") long reservationId, @PathParam("productId") Long productId) {
        LOGGER.info("Request received to buy products on reservation with id " + reservationId);
        if (productId != null) {
            Charge charge;
            try {
                charge = userService.addCharge(productId, reservationId);
                URI uri = URI.create(
                        (uriInfo.getRequestUri() + "")
                                .split("/products/")[0]
                                + "/products/" + charge.getId()
                );
                return Response.created(uri).entity(ChargeResponse.fromCharge(charge)).contentLocation(uri).build();
            } catch (EntityNotFoundException e) {
                if (e.getDescription().contains("product")) {
                    return sendErrorMessageResponse(Status.NOT_FOUND,
                            messageSourceExternalizer.getMessage("product.notfound"));
                } else if (e.getDescription().contains("reservation")) {
                    return sendErrorMessageResponse(Status.NOT_FOUND,
                            messageSourceExternalizer.getMessage("reservation.notfound"));
                }
                return sendErrorMessageResponse(Status.NOT_FOUND,
                        messageSourceExternalizer.getMessage("error.404"));
            }
        }
        return sendErrorMessageResponse(Status.NOT_FOUND,
            messageSourceExternalizer.getMessage("product.notfound"));
    }

    @GET
    @Path("/{reservationId}/help/{helpId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getHelp(@PathParam("reservationId") long reservationId, @PathParam("helpId") long helpId) {
        LOGGER.info("Attempting to get help request with id " + helpId + ", from reservation with id " + reservationId);
        HelpResponse helpRequest;
        try {
            helpRequest = userService.getHelpRequestById(reservationId, helpId);
        } catch (EntityNotFoundException e) {
            if (e.getDescription().contains("Help")) {
                return sendErrorMessageResponse(Status.NOT_FOUND,
                        messageSourceExternalizer.getMessage("help.notfound"));
            } else if (e.getDescription().contains("does not belong")) {
                return sendErrorMessageResponse(Status.FORBIDDEN,
                        messageSourceExternalizer.getMessage("error.403"));
            } else {
                return sendErrorMessageResponse(Status.NOT_FOUND,
                        messageSourceExternalizer.getMessage("reservation.notfound"));
            }
        }
        return Response.ok(helpRequest).build();
    }

    @POST
    @Path("/{reservationId}/help")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response requestHelp(@PathParam("reservationId") long reservationId, @RequestBody HelpRequest helpRequest) {
        LOGGER.info("Help request made on reservation with id " + reservationId);
        if (helpRequest == null) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                    messageSourceExternalizer.getMessage("help.notfound"));
        }
        if (helpRequest.getHelpDescription() != null && !helpRequest.getHelpDescription().isEmpty()) {
            HelpResponse helpRequested;
            try {
                helpRequested = userService.requestHelp(helpRequest.getHelpDescription(), reservationId);
            } catch (EntityNotFoundException e) {
                return sendErrorMessageResponse(Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("reservation.notfound"));
            }
            URI uri = URI.create(uriInfo.getRequestUri() + "/" + helpRequested.getId());
            return Response.created(uri).contentLocation(uri).entity(helpRequested).build();
        }
        return sendErrorMessageResponse(Status.BAD_REQUEST,
                messageSourceExternalizer.getMessage("help.notfound"));
    }

    @POST
    @Path("/ratings/{reservationHash}/rate")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response rateStay(@PathParam("reservationHash") String reservationHash,
                         @RequestBody RateReservationRequest rateRequest) {
        try {
            userService.rateStay(rateRequest.getRating(), reservationHash);
            // once submitted, this rated reservation is no longer accessible to the client, only to employees
            return Response.noContent().build();
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                messageSourceExternalizer.getMessage("reservation.notfound"));
        } catch (RequestInvalidException e) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                messageSourceExternalizer.getMessage("reservation.rated"));
        }
    }
}

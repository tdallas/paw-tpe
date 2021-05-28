package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dtos.ActiveReservationResponse;
import ar.edu.itba.paw.interfaces.dtos.ChargesByUserResponse;
import ar.edu.itba.paw.interfaces.dtos.ProductResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.MessageSourceExternalizer;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.charge.Charge;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.help.Help;
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
        return sendPaginatedResponse(page, limit, productList.getMaxItems(), productList.getList(), uriInfo.getAbsolutePathBuilder());
    }

    @POST
    @Path("/{reservationId}/products/{productId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response buyProduct(@PathParam("reservationId") long reservationId, @PathParam("productId") Long productId) throws EntityNotFoundException {
        LOGGER.info("Request received to buy products on reservation with id " + reservationId);
        if (productId != null) {
            Charge charge = userService.addCharge(productId, reservationId);
            URI uri = uriInfo.getAbsolutePathBuilder().path("/" + charge.getId()).build();
            return Response.created(uri).build();
        }
        return sendErrorMessageResponse(Status.NOT_FOUND,
            messageSourceExternalizer.getMessage("product.notfound"));
    }

    @POST
    @Path("/{reservationId}/help")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response requestHelp(@PathParam("reservationId") long reservationId,
                                @RequestBody HelpRequest helpRequest) {
        LOGGER.info("Help request made on reservation with id " + reservationId);
        if (helpRequest.getHelpDescription() != null) {
            Help helpRequested;
            try {
                helpRequested = userService.requestHelp(helpRequest.getHelpDescription(), reservationId);
            } catch (EntityNotFoundException e) {
                return sendErrorMessageResponse(Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("reservation.notfound"));
            }
            return Response.created(URI.create(uriInfo.getRequestUri() + "/" + helpRequested.getId())).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @POST
    @Path("/ratings/{reservationHash}/rate")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response rateStay(@PathParam("reservationHash") String reservationHash,
                         @RequestBody RateReservationRequest rateRequest) {
        try {
            userService.rateStay(rateRequest.getRating(), reservationHash);
            // send location uri? this rated stay is not public to the client, only to manager
            return Response.noContent().build();
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                messageSourceExternalizer.getMessage("reservation.notfound"));
        } catch (RequestInvalidException e) {
            return sendErrorMessageResponse(Status.INTERNAL_SERVER_ERROR,
                messageSourceExternalizer.getMessage("error.500"));
        }
    }
}

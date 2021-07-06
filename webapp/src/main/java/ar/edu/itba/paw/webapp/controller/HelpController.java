package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dtos.HelpResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.HelpService;
import ar.edu.itba.paw.interfaces.services.MessageSourceExternalizer;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;

import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

@Controller
@Path("help")
public class HelpController extends SimpleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelpController.class);
    public static final String DEFAULT_FIRST_PAGE = "1";
    public static final String DEFAULT_PAGE_SIZE = "20";

    private final HelpService helpService;
    private final MessageSourceExternalizer messageSourceExternalizer;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public HelpController(HelpService helpService, MessageSourceExternalizer messageSourceExternalizer) {
        this.helpService = helpService;
        this.messageSourceExternalizer = messageSourceExternalizer;
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response help(@QueryParam("page") @DefaultValue(DEFAULT_FIRST_PAGE) int page,
                         @QueryParam("limit") @DefaultValue(DEFAULT_PAGE_SIZE) int limit) {
        LOGGER.info("Request attempted to get the list of help requests.");
        PaginatedDTO<HelpResponse> helpRequests;
        try {
            helpRequests = helpService.getAllRequestsThatRequireAction(page, limit);
        } catch (IndexOutOfBoundsException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer
                    .getMessage("error.404"));
        }
        return sendPaginatedResponse(page, limit, helpRequests.getMaxItems(), helpRequests.getList(),
            uriInfo.getAbsolutePathBuilder());
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getHelpRequest(@PathParam("id") final long helpRequestId) {
        LOGGER.info("Attempted to get help request with id " + helpRequestId);
        try {
            return Response.ok(helpService.getHelpById(helpRequestId)).build();
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer
                    .getMessage("error.404"));
        }
    }

    @POST
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response markResolved(@PathParam("id") final long helpRequestId) {
        LOGGER.info("Attempted to update status on help request with id " + helpRequestId);
        try {
            URI uri = uriInfo.getRequestUri();
            if (helpService.markResolved(helpRequestId)) {
                return Response.created(uri).location(uri).contentLocation(uri).build();
            }
        } catch (EntityNotFoundException exception) {
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer
                    .getMessage("help.request.error"));
        } catch (IndexOutOfBoundsException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer
                    .getMessage("error.404"));
        }
        return sendErrorMessageResponse(Status.BAD_REQUEST,
            messageSourceExternalizer.getMessage("help.status.error"));
    }
}

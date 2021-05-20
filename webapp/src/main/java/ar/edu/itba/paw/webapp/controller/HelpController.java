package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dtos.HelpResponse;
import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.HelpService;
import ar.edu.itba.paw.interfaces.services.MessageSourceExternalizer;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.help.HelpStep;
import ar.edu.itba.paw.webapp.dtos.ErrorMessageResponse;
import javax.ws.rs.core.Response.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

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
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer.getMessage("error.404"));
        }
        return sendPaginatedResponse(page, limit, helpRequests.getMaxItems(), helpRequests.getList(), uriInfo.getAbsolutePathBuilder());
    }

    @PUT
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response updateHelpStep(@PathParam("id") final long helpRequestId, @QueryParam("getHelpFormStatus") HelpStep status,
                                   @QueryParam("page") @DefaultValue(DEFAULT_FIRST_PAGE) int page,
                                   @QueryParam("limit") @DefaultValue(DEFAULT_PAGE_SIZE) int limit) throws RequestInvalidException {
        LOGGER.info("Attempted to update status on help request.");
        try {
            if (helpService.updateStatus(helpRequestId, status)) {
                return help(page, limit);
            }
        } catch (IndexOutOfBoundsException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND, messageSourceExternalizer.getMessage("error.404"));
        }
        return sendErrorMessageResponse(Status.BAD_REQUEST,
            messageSourceExternalizer.getMessage("help.status.error"));
    }
}

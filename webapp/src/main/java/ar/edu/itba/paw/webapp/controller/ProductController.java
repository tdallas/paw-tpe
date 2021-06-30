package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dtos.ProductResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.services.MessageSourceExternalizer;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.webapp.dtos.ProductRequest;
import ar.edu.itba.paw.webapp.dtos.ProductToggleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Controller
@Path("products")
public class ProductController extends SimpleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    public static final String DEFAULT_FIRST_PAGE = "1";
    public static final String DEFAULT_PAGE_SIZE = "20";

    private final ProductService productService;
    private final MessageSourceExternalizer messageSourceExternalizer;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public ProductController(final ProductService productService,
                             final MessageSourceExternalizer messageSourceExternalizer) {
        this.productService = productService;
        this.messageSourceExternalizer = messageSourceExternalizer;
    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response products(@QueryParam("page") @DefaultValue(DEFAULT_FIRST_PAGE) int page,
                             @QueryParam("limit") @DefaultValue(DEFAULT_PAGE_SIZE) int limit) {
        PaginatedDTO<ProductResponse> products;
        try {
            products = productService.getAll(page, limit);
        } catch (IndexOutOfBoundsException e) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                    messageSourceExternalizer.getMessage("error.404"));
        }
        return sendPaginatedResponse(page, limit, products.getMaxItems(), products.getList(),
                uriInfo.getAbsolutePathBuilder());
    }

    @GET
    @Path("/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getProduct(@PathParam(value = "id") long productId) {
        ProductResponse product;
        try {
            product = productService.findProductById(productId);
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("product.notfound"));
        }
        return Response.ok(product).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response toggleProduct(@PathParam(value = "id") long productId,
                                  @RequestBody ProductToggleRequest productRequest) {
        boolean productsAffected;
        boolean enable = productRequest.isEnabled();
        try {
            if (enable) {
                productsAffected = productService.enableProduct(productId);
            } else {
                productsAffected = productService.disableProduct(productId);
            }
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("product.notfound"));
        }
        if (productsAffected) {
            return Response.noContent().contentLocation(uriInfo.getRequestUri()).build();
        }
        return sendErrorMessageResponse(Status.CONFLICT,
                messageSourceExternalizer.getMessage("product.error.status"));
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response addProduct(@RequestBody ProductRequest productRequest) {
        if (productRequest.getPrice() < 0) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                    messageSourceExternalizer.getMessage("product.price.positive"));
        }
        LOGGER.info("Request to add product to DB received");
        ProductResponse productResponse;
        try {
            productResponse = productService.saveProduct(productRequest.getDescription(),
                    productRequest.getPrice(), productRequest.getProductImageId());
        } catch (EntityNotFoundException exception) {
            // This is a bad request since the img pass as a id could not be found
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                    messageSourceExternalizer.getMessage("product.image.notfound"));
        }
        LOGGER.info("Product was saved successfully");
        return Response
                .created(URI.create(uriInfo.getRequestUri() + "" + productResponse.getId()))
                .contentLocation(URI.create(uriInfo.getRequestUri() + "" + productResponse.getId()))
                .entity(productResponse)
                .build();
    }
}

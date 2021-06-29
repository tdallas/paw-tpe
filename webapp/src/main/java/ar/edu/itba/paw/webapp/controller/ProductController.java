package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dtos.ProductImageResponse;
import ar.edu.itba.paw.interfaces.dtos.ProductResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.services.MessageSourceExternalizer;
import ar.edu.itba.paw.interfaces.services.ProductImageService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.webapp.dtos.FileUploadResponse;
import ar.edu.itba.paw.webapp.dtos.ProductRequest;
import ar.edu.itba.paw.webapp.dtos.ProductToggleRequest;
import ar.edu.itba.paw.webapp.utils.FilesUtils;

import java.net.URI;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;

@Controller
@Path("products")
public class ProductController extends SimpleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);
    public static final String DEFAULT_FIRST_PAGE = "1";
    public static final String DEFAULT_PAGE_SIZE = "20";

    private final ProductService productService;
    private final MessageSourceExternalizer messageSourceExternalizer;
    private final ProductImageService productImageService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public ProductController(final ProductService productService,
                             final MessageSourceExternalizer messageSourceExternalizer,
                             final ProductImageService productImageService) {
        this.productService = productService;
        this.messageSourceExternalizer = messageSourceExternalizer;
        this.productImageService = productImageService;
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
    public Response addProduct(@RequestBody ProductRequest productRequest) throws EntityNotFoundException {
        if (productRequest.getPrice() < 0) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                    messageSourceExternalizer.getMessage("product.price.positive"));
        }
        LOGGER.info("Request to add product to DB received");
        ProductResponse productResponse = productService.saveProduct(productRequest.getDescription(),
                productRequest.getPrice(), productRequest.getProductImgId());
        LOGGER.info("Product was saved successfully");
        return Response
                .created(URI.create(uriInfo.getRequestUri() + "" + productResponse.getId()))
                .contentLocation(URI.create(uriInfo.getRequestUri() + "" + productResponse.getId()))
                .entity(productResponse)
                .build();
    }

    @POST
    @Path("/upload-file")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response loadProductFile(@FormDataParam("file") InputStream file,
                                    @FormDataParam("file") FormDataContentDisposition fileDetail)
            throws IOException {
        final String fileName = fileDetail.getFileName();
        final ProductImageResponse productImageResponse = productImageService.saveProductImage(IOUtils.toByteArray(file), fileName);
        return Response
                .created(URI.create("/products/" + productImageResponse.getProductImageId() + "/img"))
                .entity(new FileUploadResponse(productImageResponse.getProductImageId()))
                .build();
    }

    @GET
    @Path(value = "/{productImageId}/img")
    @Produces("image/png")
    public Response getImgForProduct(@PathParam("productImageId") long productImageId) {
        byte[] productImage;
        try {
            productImage = productImageService.findImageById(productImageId).getFile();
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("product.notfound"));
        }
        return Response.ok(productImage).build();
    }
}

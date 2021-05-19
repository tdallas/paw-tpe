package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dtos.ProductResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.services.MessageSourceExternalizer;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.product.Product;
import ar.edu.itba.paw.webapp.dtos.FileUploadResponse;
import ar.edu.itba.paw.webapp.dtos.ProductRequest;
import ar.edu.itba.paw.webapp.utils.FilesUtils;
import java.io.FileNotFoundException;
import java.net.URI;
import javax.ws.rs.core.Response.Status;
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

    @Context
    private UriInfo uriInfo;

    @Autowired
    public ProductController(final ProductService productService, MessageSourceExternalizer messageSourceExternalizer) {
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
            return sendErrorMessageResponse(Status.BAD_REQUEST, messageSourceExternalizer.getMessage("error.404"));
        }
        return sendPaginatedResponse(page, limit, products.getMaxItems(), products.getList(),
            uriInfo.getAbsolutePathBuilder());
    }

    @POST
    @Path("/{id}/disable")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response hideProduct(@PathParam(value = "id") long productId) throws Exception {
        boolean productsAffected;
        try {
            productsAffected = productService.disableProduct(productId);
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                messageSourceExternalizer.getMessage("product.notfound"));
        }
        if (productsAffected) {
            return Response.ok().build();
        }
        return sendErrorMessageResponse(Status.CONFLICT,
            messageSourceExternalizer.getMessage("product.error.status"));
    }

    @POST
    @Path("/{id}/enable")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response showProduct(@PathParam(value = "id") long productId) throws Exception {
        boolean productsChanged;
        try {
            productsChanged = productService.enableProduct(productId);
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                messageSourceExternalizer.getMessage("product.notfound"));
        }
        if (productsChanged) {
            return Response.ok().build();
        }
        return sendErrorMessageResponse(Status.CONFLICT,
            messageSourceExternalizer.getMessage("product.error.status"));
    }

    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response addProduct(@RequestBody ProductRequest productRequest) throws IOException {
        if (productRequest.getPrice() < 0) {
            return sendErrorMessageResponse(Status.BAD_REQUEST,
                messageSourceExternalizer.getMessage("product.price.positive"));
        }
        LOGGER.info("Request to add product to DB received");
        Product newProduct;
        try {
            newProduct = new Product(productRequest.getDescription(), productRequest.getPrice(),
                FilesUtils.loadImg(productRequest.getImgPath()));
        } catch (FileNotFoundException fileNotFoundException) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                messageSourceExternalizer.getMessage("product.img.missing"));
        }
        newProduct = productService.saveProduct(newProduct);
        LOGGER.info("Product was saved successfully");
        return Response.created(URI.create(uriInfo.getRequestUri() + "/" + newProduct.getId())).build();
    }

    @POST
    @Path("/upload-file")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response loadProductFile(@FormDataParam("file") InputStream file,
                                    @FormDataParam("file") FormDataContentDisposition fileDetail) {
        String fileName = fileDetail.getFileName();
        String pathToFile;
        try {
            pathToFile = FilesUtils.saveFile(file, fileName);
        } catch (IOException e) {
            return sendErrorMessageResponse(Status.INTERNAL_SERVER_ERROR,
                messageSourceExternalizer.getMessage("error.500"));
        }
        return Response.ok(new FileUploadResponse(pathToFile)).build();
    }

    @GET
    @Path(value = "/{productId}/img")
    @Produces("image/png")
    public Response getImgForProduct(@PathParam("productId") long productId) {
        Product product;
        try {
            product = productService.findProductById(productId);
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Status.NOT_FOUND,
                messageSourceExternalizer.getMessage("product.notfound"));
        }
        return Response.ok(product.getFile()).build();
    }
}

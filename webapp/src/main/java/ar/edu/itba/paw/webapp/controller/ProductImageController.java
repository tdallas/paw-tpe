package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.dtos.ProductImageResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.services.MessageSourceExternalizer;
import ar.edu.itba.paw.interfaces.services.ProductImageService;
import ar.edu.itba.paw.models.product.ProductImage;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Controller
@Path("productImgs")
public class ProductImageController extends SimpleController {


    @Context
    private UriInfo uriInfo;

    private final ProductImageService productImageService;
    private final MessageSourceExternalizer messageSourceExternalizer;

    @Autowired
    public ProductImageController(final ProductImageService productImageService,
                                  final MessageSourceExternalizer messageSourceExternalizer) {
        this.productImageService = productImageService;
        this.messageSourceExternalizer = messageSourceExternalizer;
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
                .created(URI.create(uriInfo.getRequestUri() + "" + productImageResponse.getProductImageId()))
                .entity(productImageResponse)
                .build();
    }

    @GET
    @Path(value = "/{productImageId}")
    @Produces("image/png")
    public Response getImgForProduct(@PathParam("productImageId") long productImageId) {
        ProductImageResponse productImageResponse;
        try {
            ProductImage productImage = productImageService.findImageById(productImageId);
            productImageResponse = new ProductImageResponse(productImage.getId(), productImage.getFileName());
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Response.Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("product.notfound"));
        }
        return Response.ok(productImageResponse).build();
    }
}

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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response loadProductFile(@FormDataParam("file") InputStream file,
                                    @FormDataParam("file") FormDataContentDisposition fileDetail) {
        ProductImage productImage;
        final String fileName = fileDetail.getFileName();
        try {
            productImage = productImageService.saveProductImage(IOUtils.toByteArray(file), fileName);
        } catch (IOException exception) {
            return sendErrorMessageResponse(Response.Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("product.image.notfound"));
        }
        return Response
                .created(URI.create(uriInfo.getRequestUri() + "/" + productImage.getId()))
                .entity(ProductImageResponse.from(productImage))
                .build();
    }

    @GET
    @Path(value = "/{productImageId}")
    @Produces("image/png")
    public Response getImgForProduct(@PathParam("productImageId") long productImageId) {
        ProductImage productImage;
        try {
            productImage = productImageService.findImageById(productImageId);
        } catch (EntityNotFoundException e) {
            return sendErrorMessageResponse(Response.Status.NOT_FOUND,
                    messageSourceExternalizer.getMessage("product.notfound"));
        }
        return Response.ok(productImage.getFile()).build();
    }
}

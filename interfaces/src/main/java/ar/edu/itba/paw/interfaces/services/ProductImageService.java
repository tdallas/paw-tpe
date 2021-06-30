package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.models.product.ProductImage;

public interface ProductImageService {
    ProductImage saveProductImage(final byte[] productImage, final String filename);

    ProductImage findImageById(long productImageId) throws EntityNotFoundException;
}

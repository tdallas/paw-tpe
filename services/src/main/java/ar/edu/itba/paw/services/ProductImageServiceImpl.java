package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.ProductImageDao;
import ar.edu.itba.paw.interfaces.dtos.ProductImageResponse;
import ar.edu.itba.paw.interfaces.services.ProductImageService;
import ar.edu.itba.paw.models.product.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;

@Component
public class ProductImageServiceImpl implements ProductImageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductImageServiceImpl.class);

    private final ProductImageDao productImageDao;

    public ProductImageServiceImpl(ProductImageDao productImageDao) {
        this.productImageDao = productImageDao;
    }

    @Override
    public ProductImageResponse saveProductImage(byte[] imageBytes, String filename) {
        final ProductImage productImage = productImageDao.save(new ProductImage(imageBytes, filename));
        return new ProductImageResponse(productImage.getId());
    }

    @Override
    public ProductImage findImageById(long productImageId) {
        return productImageDao.findById(productImageId)
                .orElseThrow(() -> new EntityNotFoundException("Cant find product image with id " + productImageId));
    }
}

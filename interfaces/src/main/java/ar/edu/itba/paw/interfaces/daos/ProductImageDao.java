package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.models.product.ProductImage;

public interface ProductImageDao extends SimpleDao<ProductImage> {
    int deleteUnusedProductImages();
}

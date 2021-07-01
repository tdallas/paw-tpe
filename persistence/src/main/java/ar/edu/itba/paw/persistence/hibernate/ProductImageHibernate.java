package ar.edu.itba.paw.persistence.hibernate;

import ar.edu.itba.paw.interfaces.daos.ProductImageDao;
import ar.edu.itba.paw.models.product.ProductImage;
import org.springframework.stereotype.Repository;

@Repository
public class ProductImageHibernate extends SimpleRepositoryHibernate<ProductImage> implements ProductImageDao {
    @Override
    public int deleteUnusedProductImages() {
        // TODO implement
        return 0;
    }

    @Override
    String getModelName() {
        return "ProductImage";
    }

    @Override
    Class<ProductImage> getModelClass() {
        return ProductImage.class;
    }
}

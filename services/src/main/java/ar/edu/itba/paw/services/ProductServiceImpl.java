package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.ProductDao;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.product.Product;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public Product saveProduct(Product product) {
        return productDao.save(product);
    }

    @Override
    public boolean unableProduct(long productId) {
        return productDao.updateProductEnable(productId, false) > 0;
    }

    @Override
    public boolean enableProduct(long productId) {
        return productDao.updateProductEnable(productId, true) > 0;
    }

}

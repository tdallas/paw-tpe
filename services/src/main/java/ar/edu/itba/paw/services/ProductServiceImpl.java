package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.ProductDao;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.product.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Transactional
    @Override
    public Product saveProduct(Product product) {
        LOGGER.debug("About to save product with description " + product.getDescription() + " and price " + product.getPrice());
        return productDao.save(product);
    }

    @Transactional
    @Override
    public boolean disableProduct(long productId) throws EntityNotFoundException {
        LOGGER.debug("About to unable product for visibility with id " + productId);
        productDao.findById(productId).orElseThrow(() -> new EntityNotFoundException("Cant find product with id " + productId));
        return productDao.updateProductEnable(productId, false) > 0;
    }

    @Transactional
    @Override
    public boolean enableProduct(long productId) throws EntityNotFoundException {
        LOGGER.debug("About to enable product for visibility with id " + productId);
        productDao.findById(productId).orElseThrow(() -> new EntityNotFoundException("Cant find product with id " + productId));
        return productDao.updateProductEnable(productId, true) > 0;
    }

    @Override
    public PaginatedDTO<Product> getAll(int page, int pageSize) {
        if (pageSize < 1 || page < 1) throw new IndexOutOfBoundsException("Pagination requested invalid.");
        return productDao.findAll(page, pageSize);
    }

    @Transactional
    @Override
    public Product findProductById(long productId) throws EntityNotFoundException {
        return productDao.findById(productId).orElseThrow(() ->
                new EntityNotFoundException("Can't find product with id " + productId));
    }
}

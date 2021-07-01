package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.ProductDao;
import ar.edu.itba.paw.interfaces.daos.ProductImageDao;
import ar.edu.itba.paw.interfaces.dtos.ProductResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.interfaces.services.ProductImageService;
import ar.edu.itba.paw.interfaces.services.ProductService;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.product.Product;
import ar.edu.itba.paw.models.product.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductDao productDao;
    private final ProductImageService productImageService;

    @Autowired
    public ProductServiceImpl(final ProductDao productDao, final ProductImageService productImageService) {
        this.productDao = productDao;
        this.productImageService = productImageService;
    }

    @Transactional
    @Override
    public ProductResponse saveProduct(final String description, final double price, final long productImgId) throws EntityNotFoundException {
        LOGGER.info("About to save product with description " + description +
                " and price " + price + " with productImgId " + productImgId);
        final ProductImage productImage = productImageService.findImageById(productImgId);
        return ProductResponse.fromProduct(productDao.save(new Product(description, price, productImage)));
    }

    @Transactional
    @Override
    public boolean disableProduct(long productId) throws EntityNotFoundException {
        LOGGER.info("About to unable product for visibility with id " + productId);
        productDao.findById(productId).orElseThrow(() -> new EntityNotFoundException("Cant find product with id " + productId));
        return productDao.updateProductEnable(productId, false) > 0;
    }

    @Transactional
    @Override
    public boolean enableProduct(long productId) throws EntityNotFoundException {
        LOGGER.info("About to enable product for visibility with id " + productId);
        productDao.findById(productId).orElseThrow(() -> new EntityNotFoundException("Cant find product with id " + productId));
        return productDao.updateProductEnable(productId, true) > 0;
    }

    @Override
    public PaginatedDTO<ProductResponse> getAll(int page, int pageSize) {
        if (pageSize < 1 || page < 1) throw new IndexOutOfBoundsException("Pagination requested invalid.");
        PaginatedDTO<Product> paginatedResponseList = productDao.findAll(page, pageSize);
        return new PaginatedDTO<>(paginatedResponseList.getList()
                .stream().map(ProductResponse::fromProduct).collect(Collectors.toList()),
                paginatedResponseList.getMaxItems());
    }

    @Override
    @Transactional
    public ProductResponse findProductById(long productId) throws EntityNotFoundException {
        Optional<Product> possibleProduct = productDao.findById(productId);
        if (possibleProduct.isPresent()) {
            return ProductResponse.fromProduct(possibleProduct.get());
        }
        throw new EntityNotFoundException("Can't find product with id " + productId);
    }
}

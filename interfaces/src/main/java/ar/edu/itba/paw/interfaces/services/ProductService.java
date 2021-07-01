package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.dtos.ProductResponse;
import ar.edu.itba.paw.interfaces.exceptions.EntityNotFoundException;
import ar.edu.itba.paw.models.dtos.PaginatedDTO;
import ar.edu.itba.paw.models.product.Product;

public interface ProductService {
    ProductResponse saveProduct(final String description, final double price, final long productImgId) throws EntityNotFoundException;

    boolean disableProduct(final long productId) throws EntityNotFoundException;

    boolean enableProduct(final long productId) throws EntityNotFoundException;

    PaginatedDTO<ProductResponse> getAll(final int page, final int pageSize);

    ProductResponse findProductById(final long productId) throws EntityNotFoundException;
}

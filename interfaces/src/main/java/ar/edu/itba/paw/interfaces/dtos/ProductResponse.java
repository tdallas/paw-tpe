package ar.edu.itba.paw.interfaces.dtos;

import ar.edu.itba.paw.models.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ProductResponse implements Serializable {
    private long id;
    private String description;
    private double price;
    private boolean enabled;
    private long productImageId;

    public static ProductResponse fromProduct(Product product) {
        final ProductResponse pDto = new ProductResponse();

        pDto.id = product.getId();
        pDto.description = product.getDescription();
        pDto.price = product.getPrice();
        pDto.enabled = product.isEnable();
        pDto.productImageId = product.getProductImage().getId();

        return pDto;
    }
}

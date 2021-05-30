package ar.edu.itba.paw.interfaces.dtos;

import ar.edu.itba.paw.models.charge.Charge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChargeResponse {
    private long id;
    private boolean delivered;
    private ProductResponse product;

    public static ChargeResponse fromCharge(Charge charge) {
        final ChargeResponse cDto = new ChargeResponse();

        cDto.id = charge.getId();
        cDto.delivered = charge.isDelivered();
        cDto.product = ProductResponse.fromProduct(charge.getProduct());

        return cDto;
    }
}

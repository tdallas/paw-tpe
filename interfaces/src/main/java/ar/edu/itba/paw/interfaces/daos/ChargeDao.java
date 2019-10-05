package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.models.charge.Charge;
<<<<<<< HEAD
import ar.edu.itba.paw.models.product.Product;

import java.util.List;
import java.util.Map;
=======
import ar.edu.itba.paw.models.dtos.ChargeDTO;

import java.util.List;
>>>>>>> baac4aa5a487de8ab7aa983fadafcbb230d80b7e

public interface ChargeDao extends SimpleDao<Charge> {
    boolean addCharge(Charge product);

<<<<<<< HEAD
    Map<Product, Integer> getAllChargesByUser(long userID);
=======
    List<ChargeDTO> findChargeByReservationHash(long reservationId);
>>>>>>> baac4aa5a487de8ab7aa983fadafcbb230d80b7e
}

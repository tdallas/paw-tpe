package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.models.charge.Charge;
import ar.edu.itba.paw.models.dtos.ChargeDTO;

import java.util.List;

public interface ChargeDao extends SimpleDao<Charge> {
    boolean addCharge(Charge product);

    List<ChargeDTO> findChargeByReservationHash(long reservationId);
}

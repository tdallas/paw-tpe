package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.charge.Charge;
import ar.edu.itba.paw.models.product.Product;
import ar.edu.itba.paw.models.user.User;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<?> checkServicesUsed();

    List<Product> checkProductsPurchased();

    List<Product> getProducts();

    Map<?, List<?>> checkAllExpenses();

    long getReservation(long userID);

    boolean addCharge(Charge product);

    User findByUsername(String username);

//    boolean checkIn(long reservationID);
//
//    boolean checkOut(long reservationID);
//
//    boolean checkOut(int roomNumber);
//
//    boolean checkOut();
}

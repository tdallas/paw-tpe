package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.ChargeDao;
import ar.edu.itba.paw.interfaces.daos.ProductDao;
import ar.edu.itba.paw.interfaces.daos.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class UserServiceImpl implements UserService {

    private final ProductDao productDao;
    private final ChargeDao chargeDao;
    private final UserDao userDao;

    private List<Product> productsList;
    private String[] toursList = {"City Tour"};
    private String[] classesList = {"Clase de Tango"};

    @Autowired
    public UserServiceImpl(ProductDao productDao, ChargeDao chargeDao, UserDao userDao) {
        this.productDao = productDao;
        this.chargeDao = chargeDao;
        this.userDao = userDao;

//        productsList.add(new Product())
    }

    @Override
    public List<?> checkServicesUsed() {
        List<String> services = new LinkedList<>();
        services.addAll(Arrays.asList(toursList));
        services.addAll(Arrays.asList(classesList));
        return services;
    }

    @Override
    public List<Product> checkProductsPurchased() {
        return new LinkedList<Product>(Arrays.asList(productDao.));
//        return Collections.emptyList();
    }

    @Override
    public Map<String, List<?>> checkAllExpenses() {
        List<?> minibar = checkProductsPurchased();
        List<?> services = checkServicesUsed();
        Map<String, List<?>> expenses = new HashMap<>();
        expenses.put("Minibar", minibar);
        expenses.put("Other services", services);
        System.out.println(expenses);
        return expenses;
    }

//    @Override
//    public boolean checkIn(long reservationID) {
//        return false;
//    }
//
//    @Override
//    public boolean checkOut(long reservationID) {
//        return false;
//    }
//
//    @Override
//    public boolean checkOut(int roomNumber) {
//        return false;
//    }
//
//    @Override
//    public boolean checkOut() {
//        return false;
//    }
}
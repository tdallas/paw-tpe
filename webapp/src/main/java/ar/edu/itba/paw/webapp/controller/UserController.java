package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.RequestInvalidException;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.charge.Charge;
import ar.edu.itba.paw.models.dtos.RoomReservationDTO;
import ar.edu.itba.paw.models.reservation.Reservation;
import form.BuyProductForm;
import form.ProductForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController extends SimpleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/home")
    public ModelAndView getLandingPage(Authentication authentication) throws RequestInvalidException {
        final ModelAndView mav = new ModelAndView("userLanding");
        LOGGER.debug("Request received to user's landing page");
        List<RoomReservationDTO> usersReservations = new LinkedList<>();
        usersReservations = userService.findActiveReservation(getUsername(authentication));
        if(usersReservations.isEmpty()) {
            throw new RequestInvalidException();
        }
        mav.addObject("ReservationsList", usersReservations);
        return mav;
    }

    @GetMapping("/expenses")
    public ModelAndView boughtProducts(Authentication authentication, @RequestParam(value = "reservationId") long reservationId) {
        final ModelAndView mav = new ModelAndView("expenses");
        LOGGER.debug("Request received to retrieve all expenses on reservation with id" + reservationId);
        mav.addObject("ProductsList",
                userService.checkProductsPurchasedByUserByReservationId(getUsername(authentication), reservationId));
        return mav;
    }

    @GetMapping("/products")
    public ModelAndView getAllProducts(@ModelAttribute("buyProductForm") BuyProductForm productForm,
                                       @RequestParam(value = "reservationId") long reservationId) {
        final ModelAndView mav = new ModelAndView("browseProducts");
        LOGGER.debug("Request received to retrieve all products list");
        mav.addObject("ProductsList", userService.getProducts());
        return mav;
    }

    @PostMapping("/buyProducts")
    public ModelAndView buyProduct(@ModelAttribute("buyProductForm") BuyProductForm buyProductForm, @RequestParam(value = "reservationId") long reservationId) throws RequestInvalidException {
        LOGGER.debug("Request received to buy products on reservation with id" + reservationId);
        if (buyProductForm == null) {
            throw new RequestInvalidException();
        }
        final ModelAndView mav = new ModelAndView("buyProducts");
        Charge charge = new Charge(buyProductForm.getProductId(), reservationId);
        mav.addObject("charge", userService.addCharge(charge));
        return mav;
    }
}

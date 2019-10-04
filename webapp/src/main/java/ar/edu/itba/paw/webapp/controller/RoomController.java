package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ChargeService;
import ar.edu.itba.paw.interfaces.services.ReservationService;
import ar.edu.itba.paw.interfaces.services.RoomService;
import ar.edu.itba.paw.models.reservation.Reservation;
import form.CheckinForm;
import form.CheckoutForm;
import form.ReservationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Date;

@Controller
public class RoomController {
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final ChargeService chargeService;

    @Autowired
    public RoomController(RoomService roomService,ReservationService reservationService,ChargeService chargeService) {
        this.roomService = roomService;
        this.reservationService = reservationService;
        this.chargeService = chargeService;
    }

    @GetMapping("/")
    public ModelAndView getAllRooms() {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("RoomList", roomService.getRoomsList());
        return mav;
    }

    @GetMapping("/rooms/room/{id}")
    public ModelAndView getRoom(@PathVariable long id) {
        final ModelAndView mav = new ModelAndView("room");
        mav.addObject("RoomSelected", roomService.getRoom(id));
        return mav;
    }

    @GetMapping("/rooms/reservation")
    public ModelAndView reservation(@ModelAttribute("reservationForm") final ReservationForm form) {
        final ModelAndView mav = new ModelAndView("reservation");
        mav.addObject("allRooms", roomService.getRoomsList());
        return mav;
    }

    @PostMapping("/rooms/reservationPost")
    public ModelAndView reservationPost(@ModelAttribute("reservationForm") final ReservationForm form) {
        final ModelAndView mav = new ModelAndView("reservationPost");
        Reservation reserva = new Reservation(form.getRoomId(),
                form.getUserEmail(), Date.valueOf(form.getStartDate()).toLocalDate(),
                Date.valueOf(form.getEndDate()).toLocalDate(), 0L);
        roomService.doReservation(reserva);
        mav.addObject("reserva", reserva);
        return mav;
    }

    @GetMapping("/rooms/checkin")
    public ModelAndView chackin(@ModelAttribute("checkinForm") final CheckinForm form){
        final ModelAndView mav = new ModelAndView("checkin");
        return  mav;
    }

    @PostMapping("/rooms/checkinPost")
    public ModelAndView checkinPost(@ModelAttribute("checkinForm") final CheckinForm form){
        final ModelAndView mav = new ModelAndView("checkinPost");
        Reservation reser = reservationService.getReservationByHash(form.getId_reservation());
        roomService.reservateRoom(reser.getRoomId());
        return mav;
    }

    @GetMapping("/rooms/checkout")
    public ModelAndView checkout(@ModelAttribute("checkoutForm") final CheckoutForm form) {
        final ModelAndView mav = new ModelAndView("checkout");
        return mav;
    }

    @PostMapping("/rooms/checkoutPost")
    public ModelAndView checkoutPost(@ModelAttribute("checkoutForm") final CheckoutForm form){
        final ModelAndView mav = new ModelAndView("checkoutPost");
        mav.addObject("charges",chargeService.getAllChargesByReservationId(reservationService.getReservationByHash(form.getId_reservation()).getId()));
        roomService.freeRoom(reservationService.getReservationByHash(form.getId_reservation()).getRoomId());
        return mav;
    }


    @GetMapping("/rooms/reservations")
    public ModelAndView reservations() {
        final ModelAndView mav = new ModelAndView("reservations");
        return mav;
    }

}

package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.RoomService;
import ar.edu.itba.paw.models.reservation.Reservation;
import ar.edu.itba.paw.models.room.Room;
import form.ReservationForm;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static ar.edu.itba.paw.models.room.RoomType.*;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("")
    public ModelAndView getAllRooms() {
        final ModelAndView mav = new ModelAndView("index");
        roomService.setRooms();

        mav.addObject("RoomList", roomService.getRoomsList());
        return mav;
    }

    @GetMapping("/room/{id}")
    public ModelAndView getRoom(@PathVariable long id) {
        final ModelAndView mav = new ModelAndView("room");
        mav.addObject("RoomSelected", roomService.getRoom(id));
        return mav;
    }

    @GetMapping("/checkin")
    public ModelAndView checkIn(@ModelAttribute("reservationForm") final ReservationForm form) {
        final ModelAndView mav = new ModelAndView("checkin");
        roomService.setRooms();
        mav.addObject("allRooms", roomService.getRoomsList());
        return mav;
    }

    @PostMapping("/checkinPost")
    public ModelAndView checkInPost(@ModelAttribute("reservationForm") final ReservationForm form) {
        final ModelAndView mav = new ModelAndView("checkinPost");
        Reservation reserva = new Reservation(form.getRoomId(), form.getUserEmail(), Date.valueOf(form.getStartDate()).toLocalDate(), Date.valueOf(form.getEndDate()).toLocalDate());
        mav.addObject("reserva", reserva);
        return mav;
    }

    @PostMapping("/checkout")
    public void checkOut(long roomID) {
        // do mark room as in-use
    }
}

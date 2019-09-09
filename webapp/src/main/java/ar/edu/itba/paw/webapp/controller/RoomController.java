package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/all")
    public ModelAndView getAllRooms() {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("RoomList", roomService.getRoomsList());
        return mav;
    }

    @GetMapping("/room")
    public ModelAndView getRoom(@RequestParam long roomID) {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("RoomNumber", roomService.getRoom(roomID));
        return mav;
    }

    @PostMapping("/checkin")
    public ModelAndView checkIn(LocalDate startDate, LocalDate endDate) {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("Checked in, your room is: ", roomService.checkin(startDate, endDate));
        return mav;
    }

    @PostMapping("/checkinto/room")
    public ModelAndView checkIn(@RequestParam long roomID, LocalDate startDate, LocalDate endDate) {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("Checked into room: ", roomService.checkingInto(roomID, startDate, endDate));
        return mav;
    }

    @PostMapping("/checkout/room")
    public ModelAndView checkOut(@RequestParam long roomID) {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("Checked out of room: ", roomService.checkout(roomID));
        return mav;
    }

//    @PostMapping("/checkout")
//    public ModelAndView checkOut() {
//        final ModelAndView mav = new ModelAndView("index");
//        mav.addObject("Checked out of your room, room ", roomService.getARoom());
//        return mav;
//    }
}

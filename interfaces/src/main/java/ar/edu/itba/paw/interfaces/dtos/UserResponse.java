package ar.edu.itba.paw.interfaces.dtos;

import ar.edu.itba.paw.models.reservation.Reservation;
import ar.edu.itba.paw.models.user.User;
import ar.edu.itba.paw.models.user.UserRole;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponse {
    private long id;
    private String username;
    private String email;
    private UserRole role;
    private List<ReservationResponse> reservations;

    public static UserResponse fromUser(User user) {
        final UserResponse uDto = new UserResponse();

        uDto.id = user.getId();
        uDto.username = user.getUsername();
        uDto.email = user.getEmail();
        uDto.role = user.getRole();
        uDto.reservations = new ArrayList<>();
        for (Reservation r: user.getReservations()) {
            uDto.reservations.add(ReservationResponse.fromReservation(r));
        }

        return uDto;
    }
}

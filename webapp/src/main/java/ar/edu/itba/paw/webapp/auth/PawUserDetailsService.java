package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class PawUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public PawUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user by the name " + username);
        }
        final Collection<? extends GrantedAuthority> authorities =
                Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_EMPLOYEE"),
                        new SimpleGrantedAuthority("ROLE_MANAGER"),
                        new SimpleGrantedAuthority("ROLE_CLIENT")
                );
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);
    }
}

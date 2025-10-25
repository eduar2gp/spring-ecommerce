package com.ecommerce.backend.security;

import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user's data from the database.
     * Required by Spring Security's AuthenticationManager.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Fetch the User entity from the PostgreSQL database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 2. Convert the User entity and its roles into a Spring Security UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // This is the HASHED password from the DB
                mapRolesToAuthorities(user)
        );
    }

    // Helper method to convert User Roles into Granted Authorities
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}

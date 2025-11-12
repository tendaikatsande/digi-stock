package zw.co.digistock.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zw.co.digistock.domain.Officer;
import zw.co.digistock.repository.OfficerRepository;

import java.util.Collections;

/**
 * Custom UserDetailsService implementation for loading officer details
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final OfficerRepository officerRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        Officer officer = officerRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Officer not found with email: " + email));

        if (!officer.isActive()) {
            throw new UsernameNotFoundException("Officer account is inactive: " + email);
        }

        return User.builder()
            .username(officer.getEmail())
            .password(officer.getPasswordHash())
            .authorities(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + officer.getRole().name())
            ))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!officer.isActive())
            .build();
    }
}

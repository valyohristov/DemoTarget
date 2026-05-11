package com.example.target.security;

import com.example.target.model.Person;
import com.example.target.repository.PersonRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersonUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public PersonUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Person person = personRepository.findByEmailForAuth(email)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + email));

        if (person.getPasswordHash() == null || person.getPasswordHash().isBlank()) {
            throw new UsernameNotFoundException("No password configured for: " + email);
        }

        return User.builder()
                .username(person.getEmail())
                .password(person.getPasswordHash())
                .roles("USER")
                .build();
    }
}

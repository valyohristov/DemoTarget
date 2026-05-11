package com.example.target.security;

import com.example.target.model.Person;
import com.example.target.repository.PersonRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentPersonService {

    private final PersonRepository personRepository;

    public CurrentPersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Optional<Person> getCurrentPerson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            return Optional.empty();
        }
        String email = auth.getName();
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return personRepository.findByEmailForAuth(email);
    }

    public Person requireCurrentPerson() {
        return getCurrentPerson().orElseThrow(() -> new IllegalStateException("No authenticated person in session"));
    }
}

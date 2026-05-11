package com.example.target.service;

import com.example.target.model.AccessLevel;
import com.example.target.model.ObjectType;
import com.example.target.model.Person;
import com.example.target.model.PersonObjectTypeAccess;
import com.example.target.repository.LocationRepository;
import com.example.target.repository.PersonObjectTypeAccessRepository;
import com.example.target.repository.PersonRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonObjectTypeAccessRepository personObjectTypeAccessRepository;
    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonService(PersonRepository personRepository,
                         PersonObjectTypeAccessRepository personObjectTypeAccessRepository,
                         LocationRepository locationRepository,
                         PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.personObjectTypeAccessRepository = personObjectTypeAccessRepository;
        this.locationRepository = locationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Person> getAll() {
        return personRepository.findAll(Sort.by(Sort.Direction.ASC, "lastName", "firstName"));
    }

    public Optional<Person> getById(Long id) {
        return personRepository.findById(id);
    }

    @Transactional
    public Person saveNew(Person person, String plainPassword, Long locationId) {
        person.setEmail(normalizeEmail(person.getEmail()));
        person.setPasswordHash(passwordEncoder.encode(plainPassword));
        applyLocation(person, locationId);
        return personRepository.save(person);
    }

    @Transactional
    public Person updateExisting(Person incoming, String newPasswordOptional, Long locationId) {
        Person existing = personRepository.findById(incoming.getId())
                .orElseThrow(() -> new IllegalArgumentException("Person not found: " + incoming.getId()));
        existing.setFirstName(incoming.getFirstName());
        existing.setLastName(incoming.getLastName());
        existing.setEmail(normalizeEmail(incoming.getEmail()));
        if (newPasswordOptional != null && !newPasswordOptional.isBlank()) {
            existing.setPasswordHash(passwordEncoder.encode(newPasswordOptional));
        }
        applyLocation(existing, locationId);
        return personRepository.save(existing);
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private void applyLocation(Person person, Long locationId) {
        if (locationId != null) {
            person.setLocation(locationRepository.getReferenceById(locationId));
        } else {
            person.setLocation(null);
        }
    }

    public void delete(Long id) {
        personRepository.deleteById(id);
    }

    public Set<AccessLevel> getAccessLevelsForType(Long personId, ObjectType objectType) {
        if (personId == null) {
            return Collections.emptySet();
        }
        return personObjectTypeAccessRepository.findByPerson_Id(personId).stream()
                .filter(row -> row.getObjectType() == objectType)
                .map(PersonObjectTypeAccess::getAccessLevel)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Transactional
    public void replaceTypeAccess(Long personId, Map<ObjectType, Set<AccessLevel>> accessByType) {
        personObjectTypeAccessRepository.deleteByPerson_Id(personId);
        Person personRef = personRepository.getReferenceById(personId);
        for (Map.Entry<ObjectType, Set<AccessLevel>> e : accessByType.entrySet()) {
            if (e.getValue() == null) {
                continue;
            }
            for (AccessLevel level : new HashSet<>(e.getValue())) {
                personObjectTypeAccessRepository.save(
                        new PersonObjectTypeAccess(personRef, e.getKey(), level));
            }
        }
    }

    public Map<ObjectType, Set<AccessLevel>> typeAccessForForm(Long personId) {
        if (personId == null) {
            return emptyTypeAccessMap();
        }
        Map<ObjectType, Set<AccessLevel>> out = emptyTypeAccessMap();
        for (PersonObjectTypeAccess row : personObjectTypeAccessRepository.findByPerson_Id(personId)) {
            out.get(row.getObjectType()).add(row.getAccessLevel());
        }
        return out;
    }

    private static Map<ObjectType, Set<AccessLevel>> emptyTypeAccessMap() {
        Map<ObjectType, Set<AccessLevel>> m = new EnumMap<>(ObjectType.class);
        for (ObjectType t : ObjectType.values()) {
            m.put(t, new HashSet<>());
        }
        return m;
    }
}

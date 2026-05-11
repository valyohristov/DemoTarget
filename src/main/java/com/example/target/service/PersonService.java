package com.example.target.service;

import com.example.target.model.AccessLevel;
import com.example.target.model.ObjectType;
import com.example.target.model.Person;
import com.example.target.model.PersonObjectTypeAccess;
import com.example.target.repository.PersonObjectTypeAccessRepository;
import com.example.target.repository.PersonRepository;
import org.springframework.data.domain.Sort;
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

    public PersonService(PersonRepository personRepository,
                         PersonObjectTypeAccessRepository personObjectTypeAccessRepository) {
        this.personRepository = personRepository;
        this.personObjectTypeAccessRepository = personObjectTypeAccessRepository;
    }

    public List<Person> getAll() {
        return personRepository.findAll(Sort.by(Sort.Direction.ASC, "lastName", "firstName"));
    }

    public Optional<Person> getById(Long id) {
        return personRepository.findById(id);
    }

    public Person save(Person person) {
        return personRepository.save(person);
    }

    public void delete(Long id) {
        personRepository.deleteById(id);
    }

    /**
     * Access levels granted for a person on an object type (applies to all instances of that type).
     */
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

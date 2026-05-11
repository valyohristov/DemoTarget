package com.example.target.service;

import com.example.target.model.AccessLevel;
import com.example.target.model.Person;
import com.example.target.model.PersonRouteAccess;
import com.example.target.model.PersonVehicleAccess;
import com.example.target.model.Route;
import com.example.target.model.Vehicle;
import com.example.target.repository.PersonRepository;
import com.example.target.repository.PersonRouteAccessRepository;
import com.example.target.repository.PersonVehicleAccessRepository;
import com.example.target.repository.RouteRepository;
import com.example.target.repository.VehicleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonRouteAccessRepository personRouteAccessRepository;
    private final PersonVehicleAccessRepository personVehicleAccessRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;

    public PersonService(PersonRepository personRepository,
                         PersonRouteAccessRepository personRouteAccessRepository,
                         PersonVehicleAccessRepository personVehicleAccessRepository,
                         RouteRepository routeRepository,
                         VehicleRepository vehicleRepository) {
        this.personRepository = personRepository;
        this.personRouteAccessRepository = personRouteAccessRepository;
        this.personVehicleAccessRepository = personVehicleAccessRepository;
        this.routeRepository = routeRepository;
        this.vehicleRepository = vehicleRepository;
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

    public Map<Long, AccessLevel> getRouteAccessByPersonId(Long personId) {
        return personRouteAccessRepository.findByPerson_Id(personId).stream()
                .collect(Collectors.toMap(pra -> pra.getRoute().getId(), PersonRouteAccess::getAccessLevel));
    }

    public Map<Long, AccessLevel> getVehicleAccessByPersonId(Long personId) {
        return personVehicleAccessRepository.findByPerson_Id(personId).stream()
                .collect(Collectors.toMap(pva -> pva.getVehicle().getId(), PersonVehicleAccess::getAccessLevel));
    }

    @Transactional
    public void replaceAccess(Long personId, Map<Long, AccessLevel> routeAccess, Map<Long, AccessLevel> vehicleAccess) {
        personRouteAccessRepository.deleteByPerson_Id(personId);
        Person personRef = personRepository.getReferenceById(personId);
        for (Map.Entry<Long, AccessLevel> e : routeAccess.entrySet()) {
            Route routeRef = routeRepository.getReferenceById(e.getKey());
            personRouteAccessRepository.save(new PersonRouteAccess(personRef, routeRef, e.getValue()));
        }

        personVehicleAccessRepository.deleteByPerson_Id(personId);
        for (Map.Entry<Long, AccessLevel> e : vehicleAccess.entrySet()) {
            Vehicle vehicleRef = vehicleRepository.getReferenceById(e.getKey());
            personVehicleAccessRepository.save(new PersonVehicleAccess(personRef, vehicleRef, e.getValue()));
        }
    }

    /**
     * Safe for Thymeleaf when person id is null (add form): returns empty map.
     */
    public Map<Long, AccessLevel> routeAccessMapForForm(Long personId) {
        if (personId == null) {
            return Collections.emptyMap();
        }
        return new HashMap<>(getRouteAccessByPersonId(personId));
    }

    public Map<Long, AccessLevel> vehicleAccessMapForForm(Long personId) {
        if (personId == null) {
            return Collections.emptyMap();
        }
        return new HashMap<>(getVehicleAccessByPersonId(personId));
    }
}

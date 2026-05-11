package com.example.target.service;

import com.example.target.model.Person;
import com.example.target.model.Route;
import com.example.target.model.RoutePersonVehicle;
import com.example.target.model.Vehicle;
import com.example.target.repository.PersonRepository;
import com.example.target.repository.RoutePersonVehicleRepository;
import com.example.target.repository.RouteRepository;
import com.example.target.repository.VehicleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoutePersonVehicleService {

    private final RoutePersonVehicleRepository routePersonVehicleRepository;
    private final RouteRepository routeRepository;
    private final PersonRepository personRepository;
    private final VehicleRepository vehicleRepository;

    public RoutePersonVehicleService(RoutePersonVehicleRepository routePersonVehicleRepository,
                                     RouteRepository routeRepository,
                                     PersonRepository personRepository,
                                     VehicleRepository vehicleRepository) {
        this.routePersonVehicleRepository = routePersonVehicleRepository;
        this.routeRepository = routeRepository;
        this.personRepository = personRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<RoutePersonVehicle> getAll() {
        return routePersonVehicleRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Optional<RoutePersonVehicle> getById(Long id) {
        return routePersonVehicleRepository.findById(id);
    }

    @Transactional
    public RoutePersonVehicle save(Long id, Long routeId, Long personId, Long vehicleId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + routeId));
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new IllegalArgumentException("Person not found: " + personId));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        RoutePersonVehicle row;
        if (id != null) {
            row = routePersonVehicleRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Row not found: " + id));
            row.setRoute(route);
            row.setPerson(person);
            row.setVehicle(vehicle);
        } else {
            row = new RoutePersonVehicle(route, person, vehicle);
        }
        return routePersonVehicleRepository.save(row);
    }

    public void delete(Long id) {
        routePersonVehicleRepository.deleteById(id);
    }
}

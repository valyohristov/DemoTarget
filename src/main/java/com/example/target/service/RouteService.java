package com.example.target.service;

import com.example.target.model.Person;
import com.example.target.model.Route;
import com.example.target.repository.LocationRepository;
import com.example.target.repository.RouteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final LocationRepository locationRepository;

    public RouteService(RouteRepository routeRepository, LocationRepository locationRepository) {
        this.routeRepository = routeRepository;
        this.locationRepository = locationRepository;
    }

    /**
     * Back-office: no home location → all routes. Otherwise routes starting at that location only.
     */
    public List<Route> getRoutesVisibleTo(Person viewer) {
        if (viewer.getLocation() == null) {
            return routeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        }
        return routeRepository.findByStartLocation_Id(
                viewer.getLocation().getId(),
                Sort.by(Sort.Direction.ASC, "name"));
    }

    public List<Route> getAll() {
        return routeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Optional<Route> getById(Long id) {
        return routeRepository.findById(id);
    }

    public Route save(Route route, Long startLocationId) {
        applyStartLocation(route, startLocationId);
        return routeRepository.save(route);
    }

    private void applyStartLocation(Route route, Long startLocationId) {
        if (startLocationId != null) {
            route.setStartLocation(locationRepository.getReferenceById(startLocationId));
        } else {
            route.setStartLocation(null);
        }
    }

    public void delete(Long id) {
        routeRepository.deleteById(id);
    }
}

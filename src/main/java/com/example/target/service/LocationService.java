package com.example.target.service;

import com.example.target.model.Location;
import com.example.target.repository.LocationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getAll() {
        return locationRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Optional<Location> getById(Long id) {
        return locationRepository.findById(id);
    }

    public Location save(Location location) {
        return locationRepository.save(location);
    }

    public void delete(Long id) {
        locationRepository.deleteById(id);
    }
}

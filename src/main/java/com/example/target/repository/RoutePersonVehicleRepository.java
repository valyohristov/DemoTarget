package com.example.target.repository;

import com.example.target.model.RoutePersonVehicle;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutePersonVehicleRepository extends JpaRepository<RoutePersonVehicle, Long> {

    List<RoutePersonVehicle> findByRoute_StartLocation_Id(Long startLocationId, Sort sort);
}

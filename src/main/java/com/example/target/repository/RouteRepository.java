package com.example.target.repository;

import com.example.target.model.Route;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByStartLocation_Id(Long startLocationId, Sort sort);
}

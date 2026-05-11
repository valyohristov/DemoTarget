package com.example.target.repository;

import com.example.target.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}

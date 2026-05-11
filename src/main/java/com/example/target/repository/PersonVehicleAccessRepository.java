package com.example.target.repository;

import com.example.target.model.PersonVehicleAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonVehicleAccessRepository extends JpaRepository<PersonVehicleAccess, Long> {

    List<PersonVehicleAccess> findByPerson_Id(Long personId);

    void deleteByPerson_Id(Long personId);
}

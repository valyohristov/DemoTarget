package com.example.target.repository;

import com.example.target.model.PersonRouteAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRouteAccessRepository extends JpaRepository<PersonRouteAccess, Long> {

    List<PersonRouteAccess> findByPerson_Id(Long personId);

    void deleteByPerson_Id(Long personId);
}

package com.example.target.repository;

import com.example.target.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("SELECT p FROM Person p LEFT JOIN FETCH p.location WHERE LOWER(TRIM(p.email)) = LOWER(TRIM(:email))")
    Optional<Person> findByEmailForAuth(@Param("email") String email);
}

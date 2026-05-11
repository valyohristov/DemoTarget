package com.example.target.repository;

import com.example.target.model.PersonObjectTypeAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonObjectTypeAccessRepository extends JpaRepository<PersonObjectTypeAccess, Long> {

    List<PersonObjectTypeAccess> findByPerson_Id(Long personId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PersonObjectTypeAccess p where p.person.id = :personId")
    int deleteByPerson_Id(@Param("personId") Long personId);
}

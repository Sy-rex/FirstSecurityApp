package com.sobolev.spring.FirstSecurityApp.repositories;

import com.sobolev.spring.FirstSecurityApp.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByUsername(String username);
}

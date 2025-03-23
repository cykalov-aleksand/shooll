package ru.hogwarts.shooll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.shooll.model.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);
   Collection<Student>findByAgeGreaterThan(int age);
 }

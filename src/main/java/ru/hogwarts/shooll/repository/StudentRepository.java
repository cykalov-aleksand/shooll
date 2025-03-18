package ru.hogwarts.shooll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.shooll.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByAge(int age);
}

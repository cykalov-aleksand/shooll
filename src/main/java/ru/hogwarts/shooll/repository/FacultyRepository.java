package ru.hogwarts.shooll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.shooll.model.Faculty;


import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    List<Faculty> findByColor(String color);
}
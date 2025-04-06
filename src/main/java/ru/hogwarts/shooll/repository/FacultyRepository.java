package ru.hogwarts.shooll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.shooll.model.Faculty;
import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
   Collection<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String name, String color);
   Faculty findByNameIgnoreCase(String name);
}

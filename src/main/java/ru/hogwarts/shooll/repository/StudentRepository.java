package ru.hogwarts.shooll.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.shooll.entity.EntityLastPage;
import ru.hogwarts.shooll.model.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);

    Collection<Student> findByAgeGreaterThan(int age);

    @Query(value = "SELECT COUNT (id) FROM student", nativeQuery = true)
    Integer getCount();

    @Query(value = "SELECT AVG (age) FROM student", nativeQuery = true)
    float getAvrAge();

    @Query(value = "SELECT id, name, age, faculty_id FROM student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    Collection<EntityLastPage> lastPage();
}


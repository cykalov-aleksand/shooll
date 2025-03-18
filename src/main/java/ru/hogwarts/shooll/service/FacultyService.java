package ru.hogwarts.shooll.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.shooll.model.Faculty;
import ru.hogwarts.shooll.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Collection<Faculty> findFacultyAll() {
        return facultyRepository.findAll();
    }

    public Faculty addFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }


    public Faculty findFaculty(long id) {
        return facultyRepository.findById(id).get();
    }

    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
    }

    public List<Faculty> findByColor(String color) {
        return facultyRepository.findByColor(color);
    }

}
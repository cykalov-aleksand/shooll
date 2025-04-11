package ru.hogwarts.shooll.service;


import org.springframework.stereotype.Service;
import ru.hogwarts.shooll.model.Faculty;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.repository.FacultyRepository;


import java.util.Collection;
import java.util.List;

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

    public Collection<Faculty> findByNameOrColor(String name, String color) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    public List<Student> getListStudent(String name) {
        return facultyRepository.findByNameIgnoreCase(name).getStudents();
    }
}
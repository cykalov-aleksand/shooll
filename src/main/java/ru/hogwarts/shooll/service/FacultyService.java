package ru.hogwarts.shooll.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public Collection<Faculty> findFacultyAll() {
        logger.info("Был вызван метод вывода всех факультетов находящихся в таблице faculty");
        return facultyRepository.findAll();
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Был вызван метод добавления факультета в БД с характеристиками - {}", faculty);
        return facultyRepository.save(faculty);
    }


    public Faculty findFaculty(long id) {
        logger.info("Был вызван метод вывода информации по факультету с ID - {}", id);
        return facultyRepository.findById(id).get();
    }

    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
        logger.info("Был выполнен метод удаления факультета с ID - {}", id);
    }

    public Collection<Faculty> findByNameOrColor(String name, String color) {
        logger.info("Был вызван метод вывода списка факультетов по названию - {} или цвету - {}", name, color);
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    public List<Student> getListStudent(String name) {
        logger.info("Был вызван метод вывода всех студентов проходящих обучение в факультете - {}", name);
        return facultyRepository.findByNameIgnoreCase(name).getStudents();
    }
}
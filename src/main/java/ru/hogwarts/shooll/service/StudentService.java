package ru.hogwarts.shooll.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.shooll.entity.EntityLastPage;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.repository.AvatarRepository;
import ru.hogwarts.shooll.repository.StudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class StudentService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    public StudentService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public Collection<Student> findStudentAll() {
        logger.debug("Был вызван метод вывода всех студентов находящихся в БД student - (findStudentAll())");
        return studentRepository.findAll();
    }

    public Student addStudent(Student student) {
        logger.info("Был вызван метод добавления студента в таблицу student с характеристиками: {} - (addStudent({}))",
                student, student);
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        logger.info("Был вызван метод поиска студента в таблице student с ID:{} - (findStudent({}))", id, id);
        return studentRepository.findById(id).get();
    }

    public void deleteStudent(long id) {
        avatarRepository.deleteById(id);
        studentRepository.deleteById(id);
        logger.info("Был выполнен метод удаления студента с таблицы student  и avatar к нему с" +
                " ID:{} - (deleteStudent({}))", id, id);
    }

    public Collection<Student> findByAge(int age) {
        logger.debug("Был вызван метод вывода списка студентов с возрастом равным - {} годам - (findByAge({}))", age, age);
        return studentRepository.findByAge(age);
    }

    public Collection<Student> findByAgeGreatThen(int age) {
        logger.debug("Был вызван метод вывода списка студентов с возрастом равным и более" +
                " - {} годам - (findByAgeGreatThen({}))", age, age);
        return studentRepository.findByAgeGreaterThan(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        if (max == 0) {
            return findStudentAll();
        }
        if (max == min) {
            return findByAge(max);
        }
        List<Student> between = new ArrayList<>();
        for (Student value : findByAgeGreatThen(min)) {
            if (value.getAge() <= max) {
                between.add(value);
            }
        }
        logger.info("Был выполнен метод вывода списка студентов с возрастом от {} и до {} лет" +
                " - (findByAgeBetween({}, {}))", min, max, min, max);
        return between;
    }

    public String getFacultyId(long studentId) {
        logger.info("Был вызван метод вывода названия факультета в котором учится студент" +
                " с индивидуальным номером - ({}) - (getFacultyId({}))", studentId, studentId);
        return findStudent(studentId).getFaculty().getName();
    }

    public Integer getCount() {
        logger.info("Был вызван метод вывода числа студентов находящихся в таблице student - (getCount())");
        return studentRepository.getCount();
    }

    public double getAvrAge() {
        logger.info("Был вызван метод получения среднего возраста студентов" +
                " находящихся в таблице student - (getAvrAge())");
        return studentRepository.getAvrAge();
    }

    public Collection<EntityLastPage> getLastPage() {
        logger.info("Был вызван метод вывода данные по 5 последним " +
                "студентам находящихся в таблице student - (getLastPage())");
        return studentRepository.lastPage();
    }

    public Collection<String> aGetFilterStudentName() {
        logger.info("Был вызван метод фильтрации по первой букве \"А\" и вывода в верхнем регистре списка студентов " +
                " - (aGetFilterStudentName())");
        return (studentRepository.findAll().stream().parallel()
                .map(Student::getName).filter(name -> name.trim().startsWith("А"))
                .map(String::toUpperCase).sorted().toList());
    }

    public double getAverAge() {
        logger.info("Был вызван метод вычисления среднего возраста студентов в таблице student " +
                " - getAverAge())");
        long count = studentRepository.findAll().size();
        if (count == 0) {
            return 0;
        }
        int sum = studentRepository.findAll().stream().parallel().mapToInt(Student::getAge).sum();
        return (double) sum / count;
    }
}
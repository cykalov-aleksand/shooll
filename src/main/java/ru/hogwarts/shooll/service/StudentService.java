package ru.hogwarts.shooll.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.repository.StudentRepository;

import java.util.Collection;
import java.util.List;


@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Collection<Student> findStudentAll() {
        return studentRepository.findAll();
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        return studentRepository.findById(id).get();
    }

    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> findByAge(int age) {
        return studentRepository.findByAge(age);
    }

}
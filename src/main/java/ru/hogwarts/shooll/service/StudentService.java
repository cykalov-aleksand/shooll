package ru.hogwarts.shooll.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.repository.StudentRepository;

import java.util.ArrayList;
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

    public Collection<Student> findByAge(int age) {
        return studentRepository.findByAge(age);
    }

   public Collection<Student> findByAgeGreatThen(int age) {
        return studentRepository.findByAgeGreaterThan(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        if(max==0){
            return findStudentAll();
        }
        if(max==min) {
            return findByAge(max);
        }
        List<Student> between = new ArrayList<>();
        for (Student value : findByAgeGreatThen(min)) {
            if (value.getAge() <= max) {
                between.add(value);
           }
       }
        return between;
    }
    public String getFacultyId(long studentId){
        return findStudent(studentId).getFaculty().getName();
    }
}
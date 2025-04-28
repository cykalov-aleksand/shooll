package ru.hogwarts.shooll.testresttemplate;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.hogwarts.shooll.controller.StudentController;
import ru.hogwarts.shooll.model.Student;

import java.util.Collection;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles(value = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private StudentController studentController;
    @Autowired
    private TestRestTemplate testRestTemplate;
    String baseUrl = "http://localhost:";

    @Test
    void contextLoad() throws Exception {
        Assertions.assertThat(studentController).isNotNull();
    }

    @Test
    public void getTestStudentInfoAge() throws Exception {
        Student dataSqlWrite = startTest();
        ResponseEntity<Collection<Student>> person = testRestTemplate
                .exchange(baseUrl + port + "/student?min=" + 1110 + "&max=" + 1112,
                        HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                        });
        Collection<Student> persons = person.getBody();
        Assertions.assertThat(persons).isNotNull();
        Assertions.assertThat(persons).contains(dataSqlWrite);
        testRestTemplate.delete(baseUrl + port + "/student/{id}", dataSqlWrite.getId());
    }

    @Test
    public void getStudentInfoIdTest() throws Exception {
        Student dataSqlWrite = startTest();
        ResponseEntity<Student> student = testRestTemplate
                .getForEntity(baseUrl + port + "/student/{id}", Student.class, dataSqlWrite.getId());
        Assertions.assertThat(student.getStatusCode()).isIn(HttpStatus.OK);
        assertThat(Objects.requireNonNull(student.getBody()).getName()).isEqualTo(dataSqlWrite.getName());
        assertThat(Objects.requireNonNull(student.getBody()).getAge()).isEqualTo(dataSqlWrite.getAge());
        testRestTemplate.delete(baseUrl + port + "/student/{id}", dataSqlWrite.getId());
    }

    @Test
    public void getStudentFacultyIdTest() throws Exception {
        ResponseEntity<String> faculty = testRestTemplate
                .getForEntity(baseUrl + port + "/student/faculty/{studentId}", String.class, 1L);
        Assertions.assertThat(faculty.getStatusCode()).isIn(HttpStatus.OK);
        assertThat((faculty.getBody())).isEqualTo("физмат");
    }

    @Test
    public void createStudentTest() throws Exception {
        Student dataSqlWrite = startTest();
        ResponseEntity<Student> dataSqlRead = testRestTemplate
                .getForEntity(baseUrl + port + "/student/{id}", Student.class, dataSqlWrite.getId());
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getName()).isEqualTo(dataSqlWrite.getName());
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getAge()).isEqualTo(dataSqlWrite.getAge());
        testRestTemplate.delete(baseUrl + port + "/student/{id}", dataSqlWrite.getId());
    }

    @Test
    public void deleteStudentTest() throws Exception {
        Student dataSqlWrite = startTest();
        long id = dataSqlWrite.getAge();
        testRestTemplate.delete(baseUrl + port + "/student/{id}", dataSqlWrite.getId());
        ResponseEntity<Student> student1 = testRestTemplate
                .getForEntity(baseUrl + port + "/student/{id}", Student.class, id);
        Assertions.assertThat(student1.getStatusCode()).isNotEqualTo(HttpStatus.OK);
    }

    @Test
    public void editStudentTest() throws Exception {
        Student student = new Student();
        student.setName("TEST");
        student.setAge(1111);
        Student dataSqlWrite = testRestTemplate
                .postForEntity(baseUrl + port + "/student", student, Student.class).getBody();
        String name = dataSqlWrite.getName();
        int age = dataSqlWrite.getAge();
        long id = dataSqlWrite.getId();
        student.setId(id);
        student.setName("sdjfsdlfj");
        student.setAge(5464);
        testRestTemplate.put(baseUrl + port + "/student", student);
        ResponseEntity<Student> dataSqlRead = testRestTemplate
                .getForEntity(baseUrl + port + "/student/{id}", Student.class, id);
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getId()).isEqualTo(id);
        assertThat(dataSqlRead.getBody().getName()).isNotEqualTo(name);
        assertThat(dataSqlRead.getBody().getAge()).isNotEqualTo(age);
        testRestTemplate.delete(baseUrl + port + "/student/{id}", dataSqlWrite.getId());
    }

    @Test
    public void getCountTest() throws Exception {
        Integer count = testRestTemplate.getForObject(baseUrl + port + "/student/count", Integer.class);
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void getAvrAgeTest() throws Exception {
        double avrAge = testRestTemplate.getForObject(baseUrl + port + "/student/avgAge", Double.class);
        assertThat(avrAge).isEqualTo(35.5);
    }

    Student startTest() {
        Student student = new Student();
        student.setName("TEST");
        student.setAge(1111);
        return testRestTemplate.postForEntity(baseUrl + port + "/student", student, Student.class).getBody();
    }

    @Test
    public void getSynchronouslyStudentsTest() throws Exception {
        String answerEtalon = "Вывод в параллельном синхронном режиме в консоль 6 имен студентов совершен";
        String answer = testRestTemplate.getForObject(baseUrl + port + "/student/print-synchronized", String.class);
        assertThat(answer).isEqualTo(answerEtalon);
    }

    @Test
    public void getStudentParallelTest() throws Exception {
        String answerEtalon = "Вывод в параллельном несинхронном режиме в консоль 6 имен студентов совершен";
        String answer = testRestTemplate.getForObject(baseUrl + port + "/student/print-parallel", String.class);
        assertThat(answer).isEqualTo(answerEtalon);
    }
}

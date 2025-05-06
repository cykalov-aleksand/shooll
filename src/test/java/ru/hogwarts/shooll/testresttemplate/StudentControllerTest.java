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
        //вводим тестовые данные
        Student dataSqlWrite = startTest();
        //производим тестирование запроса
        ResponseEntity<Collection<Student>> person = testRestTemplate
                .exchange(baseUrl + port + "/student?min=" + 1110 + "&max=" + 1112,
                        HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                        });
        Collection<Student> persons = person.getBody();
        //производим анализ ответа на запрос
        Assertions.assertThat(persons).isNotNull();
        Assertions.assertThat(persons).contains(dataSqlWrite);
        //удаляем тестовый метод
        testRestTemplate.delete(baseUrl + port + "/student/{id}", dataSqlWrite.getId());
    }

    @Test
    public void getStudentInfoIdTest() throws Exception {
        //вводим тестовые данные
        Student dataSqlWrite = startTest();
        //производим тестирование запроса
        ResponseEntity<Student> student = testRestTemplate
                .getForEntity(baseUrl + port + "/student/{id}", Student.class, dataSqlWrite.getId());
        //проводим анализ статуса ответа на запрос
        Assertions.assertThat(student.getStatusCode()).isIn(HttpStatus.OK);
        //производим анализ ответа на запрос
        assertThat(Objects.requireNonNull(student.getBody()).getName()).isEqualTo(dataSqlWrite.getName());
        assertThat(Objects.requireNonNull(student.getBody()).getAge()).isEqualTo(dataSqlWrite.getAge());
        //удаляем тестовые данные
        testRestTemplate.delete(baseUrl + port + "/student/{id}", dataSqlWrite.getId());
    }

    @Test
    public void getStudentFacultyIdTest() throws Exception {
        //производим тестирование запроса
        ResponseEntity<String> faculty = testRestTemplate
                .getForEntity(baseUrl + port + "/student/faculty/{studentId}", String.class, 1L);
        //производим анализ статуса ответа на запрос
        Assertions.assertThat(faculty.getStatusCode()).isIn(HttpStatus.OK);
        //производим анализ ответа на запрос
        assertThat((faculty.getBody())).isEqualTo("физмат");
    }

    @Test
    public void createStudentTest() throws Exception {
        //вводим тестовые данные
        Student dataSqlWrite = startTest();
        //производим тестирование запроса
        ResponseEntity<Student> dataSqlRead = testRestTemplate
                .getForEntity(baseUrl + port + "/student/{id}", Student.class, dataSqlWrite.getId());
        //производим анализ статуса ответа на запрос
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        //производим анализ ответа на запрос
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getName()).isEqualTo(dataSqlWrite.getName());
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getAge()).isEqualTo(dataSqlWrite.getAge());
        testRestTemplate.delete(baseUrl + port + "/student/{id}", dataSqlWrite.getId());
    }

    @Test
    public void deleteStudentTest() throws Exception {
        //вводим тестовые данные
        Student dataSqlWrite = startTest();
        long id = dataSqlWrite.getAge();
        //производим тестирование запроса на удаление тестовых данных
        testRestTemplate.delete(baseUrl + port + "/student/{id}", dataSqlWrite.getId());
        ResponseEntity<Student> student1 = testRestTemplate
                .getForEntity(baseUrl + port + "/student/{id}", Student.class, id);
        //проводим анализ статуса ответа на запрос
        Assertions.assertThat(student1.getStatusCode()).isNotEqualTo(HttpStatus.OK);
    }

    @Test
    public void editStudentTest() throws Exception {
        //вводим тестовые данные
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
        //производим тестирование запроса
        testRestTemplate.put(baseUrl + port + "/student", student);
        ResponseEntity<Student> dataSqlRead = testRestTemplate
                .getForEntity(baseUrl + port + "/student/{id}", Student.class, id);
        //производим анализ статуса ответа
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        //производим анализ ответа на запрос
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getId()).isEqualTo(id);
        assertThat(dataSqlRead.getBody().getName()).isNotEqualTo(name);
        assertThat(dataSqlRead.getBody().getAge()).isNotEqualTo(age);
        //удаляем тестовые данные
        testRestTemplate.delete(baseUrl + port + "/student/{id}", dataSqlWrite.getId());
    }

    @Test
    public void getCountTest() throws Exception {
        //проводим тестирование запроса из тестовой таблицы
        Integer count = testRestTemplate.getForObject(baseUrl + port + "/student/count", Integer.class);
        //проводим анализ ответа на запрос
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void getAvrAgeTest() throws Exception {
        //проводим тестирование запроса из тестовой таблицы
        double avrAge = testRestTemplate.getForObject(baseUrl + port + "/student/avgAge", Double.class);
        //проводим анализ ответа на запрос
        assertThat(avrAge).isEqualTo(35.5);
    }

    @Test
    public void getSynchronouslyStudentsTest() throws Exception {
        String answerEtalon = "Вывод в параллельном синхронном режиме в консоль 6 имен студентов совершен";
        //проводим тестирование запроса
        String answer = testRestTemplate.getForObject(baseUrl + port + "/student/print-synchronized", String.class);
        //проводим анализ ответа
        assertThat(answer).isEqualTo(answerEtalon);
    }

    @Test
    public void getStudentParallelTest() throws Exception {
        String answerEtalon = "Вывод в параллельном несинхронном режиме в консоль 6 имен студентов совершен";
        //проводим тестирование запроса
        String answer = testRestTemplate.getForObject(baseUrl + port + "/student/print-parallel", String.class);
        //проводим анализ ответа
        assertThat(answer).isEqualTo(answerEtalon);
    }

    //метод для проведения тестирования
    Student startTest() {
        Student student = new Student();
        student.setName("TEST");
        student.setAge(1111);
        return testRestTemplate.postForEntity(baseUrl + port + "/student", student, Student.class).getBody();
    }
}

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
import ru.hogwarts.shooll.controller.FacultyController;
import ru.hogwarts.shooll.model.Faculty;
import ru.hogwarts.shooll.model.Student;

import java.util.Collection;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles(value = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private FacultyController facultyController;
    @Autowired
    private TestRestTemplate testRestTemplate;
    String baseUrl = "http://localhost:";

    @Test
    void contextLoad() throws Exception {
        Assertions.assertThat(facultyController).isNotNull();
    }

    @Test
    void createFacultyTest() throws Exception {
        //вводим тестовые значения
        Faculty dataSqlWrite = startTest();
        ResponseEntity<Faculty> dataSqlRead = testRestTemplate.getForEntity(baseUrl + port + "/faculty/{id}", Faculty.class,
                Objects.requireNonNull(dataSqlWrite).getId());
        //производим проверку статуса ответа
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        //проводим проверку полученных значений с эталонными
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getName()).isEqualTo(dataSqlWrite.getName());
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getColor()).isEqualTo(dataSqlWrite.getColor());
        //удаляем тестовые значения
        testRestTemplate.delete(baseUrl + port + "/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void editFacultyTest() throws Exception {
        //вводим тестовые значения
        Faculty faculty = new Faculty();
        faculty.setName("TEST");
        faculty.setColor("TEST color");
        Faculty dataSqlWrite = testRestTemplate.postForEntity(baseUrl + port + "/faculty",
                faculty, Faculty.class).getBody();
        faculty.setId(dataSqlWrite.getId());
        faculty.setName("sdjfsdlfj");
        faculty.setColor("белый");
        //производим запись тестовых данных
        testRestTemplate.put(baseUrl + port + "/faculty", faculty);
        //производим тестирование введенных значений
        ResponseEntity<Faculty> dataSqlRead = testRestTemplate.getForEntity(baseUrl + port + "/faculty/{id}",
                Faculty.class, dataSqlWrite.getId());
        //проводим проверку статуса ответа
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        //проводим проверку ответа на введенные данные
        assertThat(dataSqlRead.getBody().getId()).isEqualTo(dataSqlWrite.getId());
        assertThat(dataSqlRead.getBody().getName()).isNotEqualTo(dataSqlWrite.getName());
        assertThat(dataSqlRead.getBody().getColor()).isNotEqualTo(dataSqlWrite.getColor());
        //удаляем тестовые значения
        testRestTemplate.delete(baseUrl + port + "/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void deleteFacultyTest() throws Exception {
        //производим запись тестовых данных
        Faculty dataSqlWrite = startTest();
        long idDelete = dataSqlWrite.getId();
        //производим тестирование запроса на удаление данных
        testRestTemplate.delete(baseUrl + port + "/faculty/{id}", dataSqlWrite.getId());
        ResponseEntity<Faculty> dataSqlRead = testRestTemplate
                .getForEntity(baseUrl + port + "/faculty/{id}", Faculty.class, idDelete);
        //проводим оценку статуса ответа
        Assertions.assertThat(dataSqlRead.getStatusCode()).isNotEqualTo(HttpStatus.OK);
        //удаляем тестовые значения
        testRestTemplate.delete(baseUrl + port + "/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void getFacultyInfoTest() throws Exception {
        //производим запись тестовых данных
        Faculty dataSqlWrite = startTest();
        //проводим тестирование запроса
        ResponseEntity<Collection<Faculty>> dataSqlRead = testRestTemplate.exchange(baseUrl + port + "/faculty",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        Collection<Faculty> facultys = dataSqlRead.getBody();
        //проводим анализ ответа
        Assertions.assertThat(facultys).isNotNull();
        //удаляем тестовые значения
        testRestTemplate.delete(baseUrl + port + "/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void getFacultyInfoIdTest() throws Exception {
        //производим запись тестовых данных
        Faculty dataSqlWrite = startTest();
        long idRead = dataSqlWrite.getId();
        //производим тестирование запроса
        ResponseEntity<Faculty> dataSqlRead = testRestTemplate
                .getForEntity(baseUrl + port + "/faculty/{id}", Faculty.class, idRead);
        //производим анализ статуса ответа на запрос
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        //производим анализ ответа на запрос
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getId()).isEqualTo(dataSqlWrite.getId());
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getName()).isEqualTo(dataSqlWrite.getName());
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getColor()).isEqualTo(dataSqlWrite.getColor());
        //удаляем тестовые значения
        testRestTemplate.delete(baseUrl + port + "/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void findFacultyOrColorTest() throws Exception {
        //производим запись тестовых данных
        Faculty dataSqlWrite = startTest();
        //производим тестирование запроса
        ResponseEntity<Collection<Faculty>> dataSqlRead = testRestTemplate
                .exchange(baseUrl + port + "/faculty/color?name=" + "TEST" + "&color=" + "красный",
                        HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                        });
        Collection<Faculty> persons = dataSqlRead.getBody();
        //производим анализ ответа на запрос
        Assertions.assertThat(persons).contains(dataSqlWrite);
        //удаляем тестовые значения
        testRestTemplate.delete(baseUrl + port + "/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void getListStudentTest() throws Exception {
        //производим тестирование запроса
        ResponseEntity<Collection<Student>> dataSqlRead = testRestTemplate
                .exchange(baseUrl + port + "/faculty/faculty/" + "физмат",
                        HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                        });
        //производим анализ статуса ответа на запрос
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        Collection<Student> persons = dataSqlRead.getBody();
        //производим анализ ответа на запрос
        Assertions.assertThat(persons).isNotEmpty();
    }
    //метод для проведения тестирования
    Faculty startTest() {
        Faculty faculty = new Faculty();
        faculty.setName("TEST");
        faculty.setColor("TEST color");
        return testRestTemplate.postForEntity(baseUrl + port + "/faculty", faculty, Faculty.class).getBody();
    }
}

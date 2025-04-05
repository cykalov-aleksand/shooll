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
import ru.hogwarts.shooll.controller.FacultyController;
import ru.hogwarts.shooll.model.Faculty;
import ru.hogwarts.shooll.model.Student;


import java.util.Collection;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


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
    void createFacultyTest()throws Exception {
        Faculty dataSqlWrite = startTest();
        ResponseEntity<Faculty> dataSqlRead = testRestTemplate.getForEntity(baseUrl+port+"/faculty/{id}", Faculty.class,
                Objects.requireNonNull(dataSqlWrite).getId());
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getName()).isEqualTo(dataSqlWrite.getName());
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getColor()).isEqualTo(dataSqlWrite.getColor());
        testRestTemplate.delete(baseUrl+port+"/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void editFacultyTest() throws Exception{
        Faculty faculty = new Faculty();
        faculty.setName("TEST");
        faculty.setColor("TEST color");
        Faculty dataSqlWrite = testRestTemplate.postForEntity(baseUrl + port + "/faculty",
                faculty, Faculty.class).getBody();
        faculty.setId(dataSqlWrite.getId());
        faculty.setName("sdjfsdlfj");
        faculty.setColor("белый");
        testRestTemplate.put(baseUrl+port+"/faculty", faculty);
        ResponseEntity<Faculty> dataSqlRead = testRestTemplate.getForEntity(baseUrl+port+"/faculty/{id}",
                Faculty.class, dataSqlWrite.getId());
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        assertThat(dataSqlRead.getBody().getId()).isEqualTo(dataSqlWrite.getId());
        assertThat(dataSqlRead.getBody().getName()).isNotEqualTo(dataSqlWrite.getName());
        assertThat(dataSqlRead.getBody().getColor()).isNotEqualTo(dataSqlWrite.getColor());
        testRestTemplate.delete(baseUrl+port+"/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void deleteFacultyTest() throws Exception{
        Faculty dataSqlWrite = startTest();
        long idDelete = dataSqlWrite.getId();
        testRestTemplate.delete(baseUrl+port+"/faculty/{id}", dataSqlWrite.getId());
        ResponseEntity<Faculty> dataSqlRead = testRestTemplate.getForEntity(baseUrl+port+"/faculty/{id}", Faculty.class, idDelete);
        Assertions.assertThat(dataSqlRead.getStatusCode()).isNotEqualTo(HttpStatus.OK);
        testRestTemplate.delete(baseUrl+port+"/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void getFacultyInfoTest() throws Exception{
        Faculty dataSqlWrite = startTest();
        ResponseEntity<Collection<Faculty>> dataSqlRead = testRestTemplate.exchange(baseUrl+port+"/faculty",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        Collection<Faculty> facultys = dataSqlRead.getBody();
        Assertions.assertThat(facultys).isNotNull();
        testRestTemplate.delete(baseUrl+port+"/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void getFacultyInfoIdTest() throws Exception{
        Faculty dataSqlWrite = startTest();
        long idRead = dataSqlWrite.getId();
        ResponseEntity<Faculty> dataSqlRead = testRestTemplate.getForEntity(baseUrl+port+"/faculty/{id}", Faculty.class, idRead);
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getId()).isEqualTo(dataSqlWrite.getId());
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getName()).isEqualTo(dataSqlWrite.getName());
        assertThat(Objects.requireNonNull(dataSqlRead.getBody()).getColor()).isEqualTo(dataSqlWrite.getColor());
        testRestTemplate.delete(baseUrl+port+"/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void findFacultyOrColorTest() throws Exception{
        Faculty dataSqlWrite = startTest();
        ResponseEntity<Collection<Faculty>> dataSqlRead = testRestTemplate
                .exchange(baseUrl+port+"/faculty/color?name=" + "TEST" + "&color=" + "красный",
                        HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                        });
        Collection<Faculty> persons = dataSqlRead.getBody();
        Assertions.assertThat(persons).contains(dataSqlWrite);
        testRestTemplate.delete(baseUrl+port+"/faculty/{id}", dataSqlWrite.getId());
    }

    @Test
    void getListStudentTest() throws Exception{
        ResponseEntity<Collection<Student>> dataSqlRead = testRestTemplate.exchange(baseUrl+port+"/faculty/faculty/" + "физмат",
                HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                });
        Assertions.assertThat(dataSqlRead.getStatusCode()).isIn(HttpStatus.OK);
        Collection<Student> persons = dataSqlRead.getBody();
        Assertions.assertThat(persons).isNotEmpty();
    }

    Faculty startTest() throws Exception{
        Faculty faculty = new Faculty();
        faculty.setName("TEST");
        faculty.setColor("TEST color");
        return testRestTemplate.postForEntity(baseUrl + port + "/faculty", faculty, Faculty.class).getBody();
    }
}

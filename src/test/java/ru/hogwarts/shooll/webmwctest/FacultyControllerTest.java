package ru.hogwarts.shooll.webmwctest;


import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.shooll.controller.FacultyController;
import ru.hogwarts.shooll.model.Faculty;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.repository.FacultyRepository;
import ru.hogwarts.shooll.service.FacultyService;


import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacultyController.class)
public class FacultyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FacultyRepository facultyRepository;
    @SpyBean
    private FacultyService facultyService;
    @InjectMocks
    private FacultyController facultyController;

    @Test
    public void addFacultyTest() throws Exception {
        //вводим тестовые данные
        List<Faculty> faculties = testObject();
        JSONObject facultyObject = new org.json.JSONObject();
        //формируем тестовый ответ на запрос
        facultyObject.put("name", faculties.get(0).getName());
        facultyObject.put("color", faculties.get(0).getColor());
        //проводим тестирование запроса
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculties.get(0));
        mockMvc.perform(MockMvcRequestBuilders
                        //формируем тестовый запрос
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                //выводим на консоль результаты работы тестового запроса
                .andDo(print())
                //проводим анализ статуса ответа
                .andExpect(status().isOk())
                //проводим анализ ответа на запрос
                .andExpect(jsonPath("$.id").value(faculties.get(0).getId()))
                .andExpect(jsonPath("$.name").value(faculties.get(0).getName()))
                .andExpect(jsonPath("$.color").value(faculties.get(0).getColor()));
    }

    @Test
    public void editFacultyTest() throws Exception {
        //вводим тестовые данные
        List<Faculty> faculties = testObject();
        String name1 = "TEST new";
        String color1 = "TEST color new";
        JSONObject facultyObject = new org.json.JSONObject();
        facultyObject.put("id", faculties.get(1).getId());
        facultyObject.put("name", name1);
        facultyObject.put("color", color1);
        //проводим тестирование запроса
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculties.get(1));
        mockMvc.perform(MockMvcRequestBuilders
                        //формируем тестовый запрос
                        .put("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                //проводим анализ статуса ответа на запрос
                .andExpect(status().isOk())
                //проводим анализ ответа на запрос
                .andExpect(jsonPath("$.id").value(faculties.get(1).getId()))
                .andExpect(jsonPath("$.name").value(faculties.get(1).getName()))
                .andExpect(jsonPath("$.color").value(faculties.get(1).getColor()));
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        long id = 2L;
        //провозим тестирование запроса DELETE
        facultyService.deleteFaculty(id);
        verify(facultyRepository).deleteById(id);
    }

    @Test
    public void getFacultyInfoIdTest() throws Exception {
        //вводим тестовые данные
        List<Faculty> faculties = testObject();
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(testObject().get(0)));
        mockMvc.perform(MockMvcRequestBuilders
                        //формируем запрос
                        .get("/faculty/" + testObject().get(0).getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                //проводим анализ статуса ответа
                .andExpect(status().isOk())
                //проводим анализ ответа на запрос
                .andExpect(jsonPath("$.id").value(faculties.get(0).getId()))
                .andExpect(jsonPath("$.name").value(faculties.get(0).getName()))
                .andExpect(jsonPath("$.color").value(faculties.get(0).getColor()));
    }

    @Test
    public void findFacultiesTest() throws Exception {
        //вводим тестовые данные
        List<Faculty> faculties = testObject();
        String name = "Программирование";
        String color = "Красный";
        when(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color))
                .thenReturn(List.of(faculties.get(0), faculties.get(1)));
        mockMvc.perform(MockMvcRequestBuilders
                        //формируем запрос
                        .get("/faculty/color?name=" + name + "&color=" + color)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                //проводим анализ количества выведенных элементов по тестовому запросу
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void getFacultyInfoTest() throws Exception {
        //вводим тестовые данные
        List<Faculty> faculties = testObject();
        when(facultyRepository.findAll()).thenReturn(faculties);
        mockMvc.perform(MockMvcRequestBuilders
                        //формируем запрос
                        .get("/faculty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                //проводим анализ количества выведенных элементов по тестовому запросу
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    public void getListStudentTest() throws Exception {
        //вводим тестовые данные
        List<Faculty> faculties = testObject();
        when(facultyRepository.findByNameIgnoreCase(faculties.get(0).getName())).thenReturn(faculties.get(0));
        mockMvc.perform(MockMvcRequestBuilders
                        //формируем запрос
                        .get("/faculty/faculty/" + faculties.get(0).getName())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                //проводим анализ количества выведенных данных по выполненому запросу
                .andExpect(jsonPath("$.length()").value(3))
                //проводим анализ выведенных данных по 0 элементу при проведении тестирования
                .andExpect(jsonPath("$[0].id").value(faculties.get(0).getStudents().get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(faculties.get(0).getStudents().get(0).getName()))
                .andExpect(jsonPath("$[0].age").value(faculties.get(0).getStudents().get(0).getAge()));
    }

    //метод для ввода тестовых данных
    private List<Faculty> testObject() {
        Faculty faculty = new Faculty(1L, "Исторический", "красный");
        Faculty faculty1 = new Faculty(2L, "Программирования", "зелёный");
        Faculty faculty2 = new Faculty(3L, "Математики", "синий");
        Student student1 = new Student(1L, "Александр", 25, faculty);
        Student student2 = new Student(2L, "Василий", 30, faculty);
        Student student3 = new Student(3L, "Надежда", 25, faculty);
        faculty.setStudents(List.of(student1, student2, student3));
        return List.of(faculty, faculty1, faculty2);
    }
}

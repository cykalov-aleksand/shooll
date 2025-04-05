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
        List<Faculty> faculties = testObject();
        JSONObject facultyObject = new org.json.JSONObject();
        facultyObject.put("name", faculties.get(0).getName());
        facultyObject.put("color", faculties.get(0).getColor());
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculties.get(0));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculties.get(0).getId()))
                .andExpect(jsonPath("$.name").value(faculties.get(0).getName()))
                .andExpect(jsonPath("$.color").value(faculties.get(0).getColor()));
    }

    @Test
    public void editFacultyTest() throws Exception {
        List<Faculty> faculties = testObject();
        String name1 = "TEST new";
        String color1 = "TEST color new";
        JSONObject facultyObject = new org.json.JSONObject();
        facultyObject.put("id", faculties.get(1).getId());
        facultyObject.put("name", name1);
        facultyObject.put("color", color1);
        when(facultyRepository.save(any(Faculty.class))).thenReturn(faculties.get(1));
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculties.get(1).getId()))
                .andExpect(jsonPath("$.name").value(faculties.get(1).getName()))
                .andExpect(jsonPath("$.color").value(faculties.get(1).getColor()));
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        long id = 2L;
        facultyService.deleteFaculty(id);
        verify(facultyRepository).deleteById(id);
    }

    @Test
    public void getFacultyInfoIdTest() throws Exception {
        List<Faculty> faculties = testObject();
        when(facultyRepository.findById(any(Long.class))).thenReturn(Optional.of(testObject().get(0)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/" + testObject().get(0).getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculties.get(0).getId()))
                .andExpect(jsonPath("$.name").value(faculties.get(0).getName()))
                .andExpect(jsonPath("$.color").value(faculties.get(0).getColor()));
    }

    @Test
    public void findFacultiesTest() throws Exception {
        List<Faculty> faculties = testObject();
        String name = "Программирование";
        String color = "Красный";
        when(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color))
                .thenReturn(List.of(faculties.get(0), faculties.get(1)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/color?name=" + name + "&color=" + color)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void getFacultyInfoTest() throws Exception {
        List<Faculty> faculties = testObject();
        when(facultyRepository.findAll()).thenReturn(faculties);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    public void getListStudentTest() throws Exception {
        List<Faculty> faculties = testObject();
        when(facultyRepository.findByNameIgnoreCase(faculties.get(0).getName())).thenReturn(faculties.get(0));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/faculty/faculty/" + faculties.get(0).getName())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(faculties.get(0).getStudents().get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(faculties.get(0).getStudents().get(0).getName()))
                .andExpect(jsonPath("$[0].age").value(faculties.get(0).getStudents().get(0).getAge()));
                  }

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

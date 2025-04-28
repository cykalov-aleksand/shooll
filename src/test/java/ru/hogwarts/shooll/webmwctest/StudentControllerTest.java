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
import ru.hogwarts.shooll.controller.StudentController;
import ru.hogwarts.shooll.model.Faculty;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.repository.AvatarRepository;
import ru.hogwarts.shooll.repository.StudentRepository;
import ru.hogwarts.shooll.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private AvatarRepository avatarRepository;
    @SpyBean
    private StudentService studentService;
    @InjectMocks
    private StudentService studentController;

    @Test
    public void createStudentTest() throws Exception {
        List<Student> students = testObject();
        JSONObject studentObject = new org.json.JSONObject();
        studentObject.put("name", students.get(0).getName());
        studentObject.put("age", students.get(0).getAge());
        when(studentRepository.save(any(Student.class))).thenReturn(students.get(0));
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(students.get(0).getId()))
                .andExpect(jsonPath("$.name").value(students.get(0).getName()))
                .andExpect(jsonPath("$.age").value(students.get(0).getAge()));
    }

    @Test
    public void editStudentTest() throws Exception {
        List<Student> students = testObject();
        String name1 = "TEST new";
        long age1 = 30;
        JSONObject facultyObject = new org.json.JSONObject();
        facultyObject.put("id", students.get(1).getId());
        facultyObject.put("name", name1);
        facultyObject.put("age", age1);
        when(studentRepository.save(any(Student.class))).thenReturn(students.get(1));
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/student")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(students.get(1).getId()))
                .andExpect(jsonPath("$.name").value(students.get(1).getName()))
                .andExpect(jsonPath("$.age").value(students.get(1).getAge()));
    }

    @Test
    public void deleteStudentTest() throws Exception {
        long id = 2L;
        studentService.deleteStudent(id);
        verify(studentRepository).deleteById(id);
    }

    @Test
    public void getStudentInfoTest() throws Exception {
        List<Student> students = testObject();
        int min = 24;
        int max = 27;
        when(studentRepository.findByAgeGreaterThan(min)).thenReturn(List.of(students.get(0), students.get(2)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student?min=" + min + "&max=" + max)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void getStudentInfoTest1() throws Exception {
        List<Student> students = testObject();
        int min = 0;
        int max = 0;
        when(studentRepository.findAll()).thenReturn(students);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student?min=" + min + "&max=" + max)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.length()").value(4));
    }

    @Test
    public void getStudentInfoIdTest() throws Exception {
        List<Student> students = testObject();
        when(studentRepository.findById(any(Long.class))).thenReturn(Optional.of(testObject().get(0)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/" + testObject().get(0).getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(students.get(0).getId()))
                .andExpect(jsonPath("$.name").value(students.get(0).getName()))
                .andExpect(jsonPath("$.age").value(students.get(0).getAge()));
    }

    @Test
    public void getFacultyTest() throws Exception {
        List<Student> students = testObject();
        long id = students.get(0).getId();
        String stringFaculti = students.get(0).getFaculty().getName().toLowerCase();
        when(studentRepository.findById(id)).thenReturn(Optional.of(testObject().get(0)));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/faculty/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .toString().equals(stringFaculti);
    }

    @Test
    public void getCountTest() throws Exception {
        when(studentRepository.getCount()).thenReturn(6);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/count")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("6"));
    }

    @Test
    public void getAvrAgeTest() throws Exception {
        when(studentRepository.getAvrAge()).thenReturn(6.5d);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/avgAge")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("6.5"));
    }

    @Test
    public void getStudentParallelTest() throws Exception {
        String list = "Вывод в параллельном несинхронном режиме в консоль 6 имен студентов совершен";
        List<Student> students = testObject();
        when(studentRepository.findAll()).thenReturn(students);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/print-parallel")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .toString().equals(list);
    }

    @Test
    public void getSynchronouslyStudentsTest() throws Exception {
        String list = "Вывод в параллельном несинхронном режиме в консоль 6 имен студентов совершен";
        List<Student> students = testObject();
        when(studentRepository.findAll()).thenReturn(students);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/student/print-synchronized")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .toString().equals(list);
    }

    private List<Student> testObject() {
        Faculty faculty = new Faculty(1L, "Исторический", "красный");
        Student student1 = new Student(1L, "Александр", 25, faculty);
        Student student2 = new Student(2L, "Василий", 30, faculty);
        Student student3 = new Student(3L, "Надежда", 25, faculty);
        Student student4 = new Student(4L, "Светлана", 30, null);
        Student student5 = new Student(5L, "Надежда", 25, faculty);
        Student student6 = new Student(6L, "Светлана", 30, null);
        faculty.setStudents(List.of(student1, student2, student3));
        return List.of(student1, student2, student3, student4);
    }
}


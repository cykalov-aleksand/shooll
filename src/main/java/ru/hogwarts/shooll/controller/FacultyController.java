package ru.hogwarts.shooll.controller;



import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.shooll.model.Faculty;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.service.FacultyService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    @Operation(summary = "Добавляем новый факультет")
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }

    @PutMapping
    @Operation(summary = "Проводим корректировку данных факультета")
    public Faculty editFaculty(@RequestBody Faculty faculty) {
        return facultyService.addFaculty(faculty);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Проводим удаление факультета по указанному id")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Получаем весь список таблицы faculty")
    public ResponseEntity<Collection<Faculty>> getFacultyInfo() {
        return ResponseEntity.ok(facultyService.findFacultyAll());
    }

    @GetMapping("{id}")
    @Operation(summary = "Проводим поиск факультета по указанному id")
    public ResponseEntity<Faculty> getFacultyInfoId(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/color")
    @Operation(summary = "Проводим поиск факультета по названию или цвету")
    public ResponseEntity<Collection<Faculty>> findFacultyOrColor(@RequestParam(required = false) String name,
                                                                  @RequestParam(required = false) String color) {
        if ((name != null && !name.isBlank()) || (color != null && !color.isBlank())) {
            return ResponseEntity.ok(facultyService.findByNameOrColor(name, color));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("faculty/{name}")
    @Operation(summary = "Получение списка студентов с их персональными данными из указанного факультета")
    public ResponseEntity<List<Student>> getListStudent(@PathVariable(required = false) String name) {
        return ResponseEntity.ok(facultyService.getListStudent(name));
    }
}
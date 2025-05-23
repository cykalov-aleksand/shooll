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
        // проводим проверку ответа метода, при ответе null возвращает статус ответа 404 и пустое тело ответа
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        //отправляем ответ на запрос если faculty not null
        return ResponseEntity.ok(faculty);
    }

    @GetMapping("/color")
    @Operation(summary = "Проводим поиск факультета по названию или цвету")
    //отправляем запрос в котором параметры name и color могут быть пустыми
    public ResponseEntity<Collection<Faculty>> findFacultyOrColor(@RequestParam(required = false) String name,
                                                                  @RequestParam(required = false) String color) {
        //в случае если один из параметров не равен null производим поиск по полям
        if ((name != null && !name.isBlank()) || (color != null && !color.isBlank())) {
            return ResponseEntity.ok(facultyService.findByNameOrColor(name, color));
        }
        // в противном случае, выводим пустой список
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("faculty/{name}")
    @Operation(summary = "Получение списка студентов с их персональными данными из указанного факультета")
    public ResponseEntity<List<Student>> getListStudent(@PathVariable(required = false) String name) {
        return ResponseEntity.ok(facultyService.getListStudent(name));
    }
    @GetMapping("/maxLength")
    @Operation(summary = "Выводим список названий факультетов имеющих максимальную длину")
    public Collection<String> getMaxLength(){
        return facultyService.getMaxNameFaculty();
    }
    @GetMapping("/number")
    @Operation(summary = "тест скорости вычисления")
    public long getSum(){
        return facultyService.sum();
    }
}
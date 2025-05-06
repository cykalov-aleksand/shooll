package ru.hogwarts.shooll.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.shooll.model.Faculty;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.repository.FacultyRepository;


import java.util.*;
import java.util.stream.LongStream;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public Collection<Faculty> findFacultyAll() {
        logger.info("Был вызван метод вывода всех факультетов находящихся в таблице faculty - (findFacultyAll())");
        return facultyRepository.findAll();
    }

    public Faculty addFaculty(Faculty faculty) {
        logger.info("Был вызван метод добавления факультета в БД с характеристиками - {} - (addFaculty({}))", faculty, faculty);
        return facultyRepository.save(faculty);
    }


    public Faculty findFaculty(long id) {
        logger.info("Был вызван метод вывода информации по факультету с ID - {} - (findFaculty({}))", id, id);
        return facultyRepository.findById(id).get();
    }

    public void deleteFaculty(long id) {
        facultyRepository.deleteById(id);
        logger.info("Был выполнен метод удаления факультета с ID - {} - (deleteFaculty({}))", id, id);
    }

    public Collection<Faculty> findByNameOrColor(String name, String color) {
        logger.info("Был вызван метод вывода списка факультетов по названию - {} или цвету - {}" +
                " - (findByNameOrColor({}, {}))", name, color, name, color);
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(name, color);
    }

    public List<Student> getListStudent(String name) {
        logger.info("Был вызван метод вывода всех студентов проходящих обучение в факультете - {}" +
                " - (getListStudent({}))", name, name);
        return facultyRepository.findByNameIgnoreCase(name).getStudents();
    }

    public Collection<String> getMaxNameFaculty() {
        logger.info("Был вызван метод поиска списка названий факультета имеющих максимальную длину" +
                " - getMaxNameFaculty())");
        List<String> list = new ArrayList<>();
        //производим поиск факультета с максимальной длиной названия факультета
        String name = facultyRepository.findAll().stream().parallel().max(Comparator.comparing(o -> o.getName()
                .length())).map(Faculty::getName).get();
        if (name.isBlank()) {
            list.add("Таблица пуста");
            return list;
        }
        //производим поиск названий факультета с заданной длиной названия
        list = facultyRepository.findAll().stream().map(Faculty::getName)
                .filter(oName -> oName.length() == name.length()).toList();
        return list;
    }

    public long sum() {
        //производим оценку времени выполнения потока
        long startTime = System.currentTimeMillis();
        long sum1 = LongStream.iterate(1, a -> a + 1).limit(1_000_000).reduce(0, Long::sum);
        long endTime = System.currentTimeMillis();
        long time1 = (endTime - startTime);
        startTime = System.currentTimeMillis();
        long sum2 = 0;
        //производим оценку времени выполнения цикла
        for (int number = 1; number <= 1_000_000; number++) {
            sum2+=number;
        }
        endTime = System.currentTimeMillis();
        long time2 = (endTime - startTime);
        logger.info("время отработки последовательного стрима= {} мс. Время отработки в цикле= {} мс. ", time1, time2);
        logger.info(" последовательный поток sum1= {}; вычисление с помощью цикла sum2= {}; ", sum1, sum2);
        return sum2;
    }
}
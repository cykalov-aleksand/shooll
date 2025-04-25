package ru.hogwarts.shooll.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.shooll.model.Faculty;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.repository.FacultyRepository;


import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

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
        String name = facultyRepository.findAll().stream().parallel().max(Comparator.comparing(o -> o.getName()
                .length())).map(Faculty::getName).get();
        if (name.isBlank()) {
            list.add("Таблица пуста");
            return list;
        }
        list = facultyRepository.findAll().stream().map(Faculty::getName)
                .filter(oName -> oName.length() == name.length()).toList();
        return list;
    }

    public long sum() {
        long startTime = System.currentTimeMillis();
        long sum1 = Stream.iterate(1, a -> a + 1).limit(1_000_000).reduce(0, (a, b) -> a+b);
        long endTime = System.currentTimeMillis();
        long time1 = (endTime - startTime);

        startTime = System.currentTimeMillis();
        Iterator<Integer> iter = Stream.iterate(1, a -> a + 1).limit(1_000_000).iterator();
        Spliterator<Integer> spliterator = getSpliteratorFromIterator(iter);
        AtomicLong sum = new AtomicLong();
        spliterator.forEachRemaining(sum::addAndGet);
        long sum2 = sum.intValue();
        endTime = System.currentTimeMillis();
        long time2 = (endTime - startTime);
        logger.info("время отработки последовательного стрима= {} мс. Время отработки параллельного стрима= {} мс.", time1, time2);
        logger.info(" последовательный поток sum1= {}; параллельный поток sum2= {}", sum1, sum2);
        return sum2;

    }

    public static <T> Spliterator<T> getSpliteratorFromIterator(Iterator<T> iterator) {
        logger.debug("Метод преобразования итератора в сплитератор");
        return Spliterators.spliteratorUnknownSize(iterator, 0);
    }
}
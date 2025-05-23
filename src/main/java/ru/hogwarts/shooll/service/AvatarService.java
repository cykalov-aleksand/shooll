package ru.hogwarts.shooll.service;

import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.shooll.entity.EntityListAvatar;
import ru.hogwarts.shooll.model.Avatar;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.repository.AvatarRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Transactional
@Service
public class AvatarService {
    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);
    @Value("${cover.dir.path}")
    private String coversDir;
    private final AvatarRepository avatarRepository;
    private final StudentService studentService;

    public AvatarService(AvatarRepository avatarRepository, StudentService studentService) {
        this.avatarRepository = avatarRepository;
        this.studentService = studentService;
    }

    public void uploadAvatar(long studentId, MultipartFile file) throws IOException {
        //производим поиск студента по указанному studentId
        Student student = studentService.findStudent(studentId);
        //производим переименование файла file в id.<тип файла>
        Path filePath = Path.of(coversDir, studentId + "." + getExtension(file.getOriginalFilename()));
        logger.info("Был вызван метод привязки картинки {} в БД к ID студента - {} - (uploadAvatar({}, {}))",
                file.getOriginalFilename(), studentId,studentId,file.getOriginalFilename());
        //создаем директорию из filePath.getParent()
        Files.createDirectories(filePath.getParent());
        //удаляем файл если он существует
        Files.deleteIfExists(filePath);
        //создаем поток чтения и записи указанного файла с последующим закрытием указанных потоков
        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }
        //заносим информацию о студенте и картинке в базу данных
        Avatar avatar = findStudentAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(generateImagePreview(filePath));
        avatarRepository.save(avatar);
    }

    public Avatar findStudentAvatar(Long studentId) {
        logger.debug("Был вызван метод поиска наличия студента по id {}, в случае отсутствия " +
                "создания нового объекта в таблице Avatar - (getExtension({}))", studentId,studentId);
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    private String getExtension(String fileName) {
        logger.debug("Был вызван метод возврата подстроки расширения принятого файла {} - (getExtension({}))", fileName,fileName);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private byte[] generateImagePreview(Path filepath) throws IOException {
        logger.debug("Был вызван метод преобразования оригинальной картинки по адресу - {}, " +
                "в формат пригодный для сохранения в БД", filepath);
        //открываем поток чтения картинки и поток записи картинки, проводим обработку оригинальной картинки и выдачу обработанной
        try (InputStream is = Files.newInputStream(filepath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);
            //уменьшаем размеры картинки в 100 раз
            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();
            //производим запись полученной картинки в переменную baos
            ImageIO.write(preview, getExtension(filepath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }

    public Avatar findAvatar(long studentId) {
        logger.info("Был вызван метод поиска avatar по ID студента. ID={} - (findAvatar({}))", studentId,studentId);
        return avatarRepository.findByStudentId(studentId).orElseThrow();
    }

    public Collection<EntityListAvatar> getFindAvatarAll(int page, int size) {
        //проверяем если введенные значения меньше 1 присваиваем им значения равные 1
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 1;
        }
        logger.info("Был вызван метод проведения пагинации данных аватар, по странице {}, " +
                "с выводимым количеством аватар равным {} - (getFindAvatarAll({}, {}))", page, size,page,size);
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return avatarRepository.listAvatar(pageRequest);
    }
}


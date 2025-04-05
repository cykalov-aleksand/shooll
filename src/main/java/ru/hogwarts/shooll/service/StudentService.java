package ru.hogwarts.shooll.service;

import jakarta.transaction.Transactional;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.shooll.model.Avatar;
import ru.hogwarts.shooll.model.Student;
import ru.hogwarts.shooll.repository.AvatarRepository;
import ru.hogwarts.shooll.repository.StudentRepository;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Transactional
@Service
public class StudentService {
    @Value("${cover.dir.path}")
    private String coversDir;
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    public StudentService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Collection<Student> findStudentAll() {
        return studentRepository.findAll();
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student findStudent(long id) {
        return studentRepository.findById(id).get();
    }

    public void deleteStudent(long id) {
        studentRepository.deleteById(id);
    }

    public Collection<Student> findByAge(int age) {
        return studentRepository.findByAge(age);
    }

    public Collection<Student> findByAgeGreatThen(int age) {
        return studentRepository.findByAgeGreaterThan(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        if(max==0){
            return findStudentAll();
        }
        if(max==min) {
            return findByAge(max);
        }
        List<Student> between = new ArrayList<>();
        for (Student value : findByAgeGreatThen(min)) {
            if (value.getAge() <= max) {
                between.add(value);
            }
        }
        return between;
    }
    public String getFacultyId(long studentId){
        return findStudent(studentId).getFaculty().getName();
    }
    public void uploadAvatar(long studentId, MultipartFile file)throws IOException {
        Student student=findStudent(studentId);
        Path filePath= Path.of(coversDir,studentId+"."+getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);
        try(InputStream is=file.getInputStream();
            OutputStream os=Files.newOutputStream(filePath,CREATE_NEW);
            BufferedInputStream bis=new BufferedInputStream(is,1024);
            BufferedOutputStream bos=new BufferedOutputStream(os,1024)
        ){
            bis.transferTo(bos);
        }
        Avatar avatar=findStudentAvatar(studentId);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(generateImagePreview(filePath));
        avatarRepository.save(avatar);
    }
    public Avatar findStudentAvatar(Long studentId){
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }
    private String getExtension(String fileName){
        return fileName.substring(fileName.lastIndexOf(".")+1);
    }
    private byte[] generateImagePreview(Path filepath)throws IOException {
        try (InputStream is = Files.newInputStream(filepath);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = ImageIO.read(bis);
            int height = image.getHeight() / (image.getWidth() / 100);
            BufferedImage preview = new BufferedImage(100, height, image.getType());
            Graphics2D graphics = preview.createGraphics();
            graphics.drawImage(image, 0, 0, 100, height, null);
            graphics.dispose();
            ImageIO.write(preview, getExtension(filepath.getFileName().toString()), baos);
            return baos.toByteArray();
        }
    }
    public Avatar findAvatar(long studentId) {
        return avatarRepository.findByStudentId(studentId).orElseThrow();
    }
}

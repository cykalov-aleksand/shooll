package ru.hogwarts.shooll.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.shooll.entity.EntityListAvatar;
import ru.hogwarts.shooll.model.Avatar;
import ru.hogwarts.shooll.service.AvatarService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@RestController
@RequestMapping("/student")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загружаем Avatar для студента с указанным ID студента")
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {
        //проверяем условие если загруженная картинка превышает или равно размеру (1024*300) байт, то в тело запроса
        //заносим сообщение "File is to big"
        if (avatar.getSize() >= 1024 * 300) {
            return ResponseEntity.badRequest().body("File is to big");
        }
        // иначе выполняем метод
        avatarService.uploadAvatar(id, avatar);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/avatar/preview")
    @Operation(summary = "Просмотр Avatar-ки загруженной в таблицу по указанному ID студента")
    public ResponseEntity<byte[]> downloadAvatar(@PathVariable Long id) {

        Avatar avatar = avatarService.findAvatar(id);
        HttpHeaders headers = new HttpHeaders();
        //устанавливаем в заголовке тип содержимого ответа
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        //устанавливаем объем содержимого ответа
        headers.setContentLength(avatar.getData().length);
        // отправляем ответ на запрос с заголовком ответа headers и телом вывода avatar.getData()
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(value = "/{id}/avatar")
    @Operation(summary = "Просмотр Avatar-ки для студента с указанным ID из директории")
    public void downloadAvatarFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.findAvatar(id);
        //определяем путь к загруженному файлу
        Path path = Path.of(avatar.getFilePath());
// производим чтение в потоке загруженного файла и вывод его в браузер с последующим закрытием потока
        try (InputStream is = Files.newInputStream(path);
             OutputStream os = response.getOutputStream()) {
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            is.transferTo(os);
        }
    }

    @GetMapping("/printPage")
    @Operation(summary = "Пагинация для AvatarService")
    //производим запрос по пагинации с указанными данными, по умолчанию устанавливаем page=1, size=1
    public Collection<EntityListAvatar> getAvatarInfo(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "1") int size) {
        return avatarService.getFindAvatarAll(page, size);
    }
}


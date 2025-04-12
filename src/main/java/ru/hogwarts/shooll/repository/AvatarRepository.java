package ru.hogwarts.shooll.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.shooll.entity.EntityListAvatar;
import ru.hogwarts.shooll.model.Avatar;

import java.util.Collection;
import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    Optional<Avatar> findByStudentId(long studentId);

    @Query(value = "SELECT id, file_path, media_type, file_size, student_id FROM avatar ORDER BY id", nativeQuery = true)
    Collection<EntityListAvatar> listAvatar(PageRequest pageRequest);
}
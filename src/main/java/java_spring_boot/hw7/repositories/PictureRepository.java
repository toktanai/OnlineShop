package java_spring_boot.hw7.repositories;

import java_spring_boot.hw7.entities.Pictures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PictureRepository extends JpaRepository<Pictures,Long> {
}

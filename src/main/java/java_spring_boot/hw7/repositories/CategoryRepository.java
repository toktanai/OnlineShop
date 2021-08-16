package java_spring_boot.hw7.repositories;

import java_spring_boot.hw7.entities.Categories;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Locale;

@Repository
@Transactional
public interface CategoryRepository extends JpaRepository<Categories,Long> {

}

package java_spring_boot.hw7.repositories;

import java_spring_boot.hw7.entities.Countries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CountryRepository extends JpaRepository<Countries,Long> {

}

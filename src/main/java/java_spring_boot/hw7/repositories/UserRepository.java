package java_spring_boot.hw7.repositories;

import java_spring_boot.hw7.entities.Roles;
import java_spring_boot.hw7.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
    List<Roles> findAllById(Users users);
}

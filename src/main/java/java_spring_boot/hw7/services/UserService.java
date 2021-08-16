package java_spring_boot.hw7.services;

import java_spring_boot.hw7.entities.Roles;
import java_spring_boot.hw7.entities.Users;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    Users getUserByEmail(String email);
    Users createUser(Users user);
    Users getUser(Long id);
    Users saveUser(Users user);
    List<Users> getAllUsers();
    void delete(Users user);

    List<Roles> getAllRoles();
    Roles saveRole(Roles role);
    Roles getRole(Long id);
    List<Roles> findAllById(Users users);
    List<Roles> findAllRoles();
    void deleteRole(Roles role);

}

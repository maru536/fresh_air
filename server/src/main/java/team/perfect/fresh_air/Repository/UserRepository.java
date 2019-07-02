package team.perfect.fresh_air.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import team.perfect.fresh_air.DAO.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);

    Optional<User> findByIdAndPasswd(String id, String passwd);
}
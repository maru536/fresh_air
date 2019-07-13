package team.perfect.fresh_air.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import team.perfect.fresh_air.DAO.User;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);

    Optional<User> findByUserIdAndPasswd(String userId, String passwd);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET using_measured_dust = :usingMeasuredDust WHERE user_id = :userId", nativeQuery = true)
    Integer updateDustType(@Param("userId") String userId, @Param("usingMeasuredDust") boolean usingMeasuredDust);
}
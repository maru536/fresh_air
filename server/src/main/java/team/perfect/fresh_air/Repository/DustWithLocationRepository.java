package team.perfect.fresh_air.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import team.perfect.fresh_air.DAO.DustPK;
import team.perfect.fresh_air.DAO.DustWithLocationDAO;

public interface DustWithLocationRepository extends CrudRepository<DustWithLocationDAO, DustPK> {
    @Query(value="SELECT * FROM dust_with_location WHERE user_id = :user_id AND pm100 != -1 AND pm25 != -1 ORDER BY time DESC LIMIT 1", nativeQuery=true)
    Optional<DustWithLocationDAO> findLatestDust(@Param("user_id") String user_id);

    @Query(value="SELECT * FROM dust_with_location WHERE user_id = :user_id ORDER BY time DESC LIMIT 10", nativeQuery=true)
    List<DustWithLocationDAO> findTenLatestDust(@Param("user_id") String userId);

    @Query(value="SELECT * FROM dust_with_location WHERE user_id = :user_id AND pm25 != -1 AND pm100 != -1 AND time >= :start_time AND time <= :end_time", nativeQuery=true)
    List<DustWithLocationDAO> findMeasuredDust(@Param("user_id") String userId, @Param("start_time") long startTime, @Param("end_time") long endTime);

    @Query(value="SELECT * FROM dust_with_location WHERE user_id = :user_id AND pm25 != -1 AND pm100 != -1 AND time >= :start_time", nativeQuery=true)
    List<DustWithLocationDAO> findMeasuredDust(@Param("user_id") String userId, @Param("start_time") long startTime);

    @Query(value="SELECT * FROM dust_with_location WHERE user_id = :user_id AND time >= :start_time AND time <= :end_time", nativeQuery=true)
    List<DustWithLocationDAO> findAllDust(@Param("user_id") String userId, @Param("start_time") long startTime, @Param("end_time") long endTime);

    @Query(value="SELECT * FROM dust_with_location WHERE user_id = :user_id AND time >= :start_time", nativeQuery=true)
    List<DustWithLocationDAO> findAllDust(@Param("user_id") String userId, @Param("start_time") long startTime);
}
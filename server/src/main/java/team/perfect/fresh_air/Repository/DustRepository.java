package team.perfect.fresh_air.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import team.perfect.fresh_air.DAO.DustPK;
import team.perfect.fresh_air.DAO.LatestDust;

public interface DustRepository extends CrudRepository<LatestDust, DustPK> {
    Optional<LatestDust> findFirstByUserIdOrderByTimeDesc(String userId);

    List<LatestDust> findByUserIdAndTimeBetween(String userId, long startTime, long endTime);

    List<LatestDust> findByUserIdAndTimeGreaterThanEqual(String userId, long startTime);
}
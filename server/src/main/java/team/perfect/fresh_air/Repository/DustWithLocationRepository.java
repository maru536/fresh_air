package team.perfect.fresh_air.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import team.perfect.fresh_air.DAO.DustPK;
import team.perfect.fresh_air.DAO.DustWithLocationDAO;

public interface DustWithLocationRepository extends CrudRepository<DustWithLocationDAO, DustPK> {
    Optional<DustWithLocationDAO> findFirstByUserIdOrderByTimeDesc(String userId);

    List<DustWithLocationDAO> findByUserIdAndTimeBetween(String userId, long startTime, long endTime);

    List<DustWithLocationDAO> findByUserIdAndTimeGreaterThanEqual(String userId, long startTime);
}
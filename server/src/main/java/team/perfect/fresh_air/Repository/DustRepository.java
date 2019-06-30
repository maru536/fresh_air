package team.perfect.fresh_air.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import team.perfect.fresh_air.DAO.LatestDust;
import team.perfect.fresh_air.DAO.DustPK;

public interface DustRepository extends CrudRepository<LatestDust, DustPK> {
    Optional<LatestDust> findFirstByUserIdOrderByTimeDesc(String userId);
}
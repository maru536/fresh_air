package team.perfect.fresh_air.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import team.perfect.fresh_air.DAO.Dust;
import team.perfect.fresh_air.DAO.DustPK;

public interface DustRepository extends CrudRepository<Dust, DustPK> {
    Optional<Dust> findFirstByUserIdOrderByTimeDesc(String userId);
}
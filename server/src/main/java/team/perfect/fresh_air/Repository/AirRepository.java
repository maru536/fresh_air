package team.perfect.fresh_air.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.Air;
import team.perfect.fresh_air.DAO.DustPK;

public interface AirRepository extends CrudRepository<Air, AddressPK> {
    @Modifying
    @Query("UPDATE air SET date_time = ?2, pm100 = ?3 WHERE address_level_one = ?1")
    void updatePM100(String addressLevelOne, String dataTime, int pm100);

    @Modifying
    @Query("UPDATE air SET date_time = ?2, pm25 = ?3 WHERE address_level_one = ?1")
    void updatePM25(String addressLevelOne, String dataTime, int pm25);
}
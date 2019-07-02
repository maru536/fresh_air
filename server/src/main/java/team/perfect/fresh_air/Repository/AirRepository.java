package team.perfect.fresh_air.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.Air;
import team.perfect.fresh_air.DAO.DustPK;

public interface AirRepository extends CrudRepository<Air, AddressPK> {
    /*@Modifying
    @Query("UPDATE air SET date_time = ?2, pm100 = ?3 WHERE address_level_one = ?1")
    void updatePM100(String addressLevelOne, String dataTime, int pm100);

    @Modifying
    @Query("UPDATE air SET date_time = ?2, pm25 = ?3 WHERE address_level_one = ?1")
    void updatePM25(String addressLevelOne, String dataTime, int pm25);*/
    @Modifying
    @Transactional
    @Query(value="UPDATE air a SET a.date_time = :date_time, a.pm100 = :pm100 WHERE a.address_level_one = :address_level_one AND a.address_level_two = ''", nativeQuery=true)
    Integer updatePM100(@Param("address_level_one") String address_level_one, @Param("date_time") String date_time, @Param("pm100") int pm100);

    @Modifying
    @Transactional
    @Query(value="UPDATE air a SET a.date_time = :date_time, a.pm25 = :pm25 WHERE a.address_level_one = :address_level_one AND a.address_level_two = ''", nativeQuery=true)
    Integer updatePM25(@Param("address_level_one") String address_level_one, @Param("date_time") String date_time, @Param("pm25") int pm25);
}
package team.perfect.fresh_air.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.PublicDust;

public interface PublicDustRepository extends CrudRepository<PublicDust, AddressPK> {
    @Modifying
    @Transactional
    @Query(value="INSERT air_test(address_level_one, address_level_two, date_time, pm100) VALUES (:address_level_one, '', :date_time, :pm100) ON DUPLICATE KEY UPDATE date_time = :date_time, pm100 = :pm100", nativeQuery=true)
    Integer upsertPM100(@Param("address_level_one") String address_level_one, @Param("date_time") String date_time, @Param("pm100") int pm100);

    @Modifying
    @Transactional
    @Query(value="INSERT air_test(address_level_one, address_level_two, date_time, pm25) VALUES (:address_level_one, '', :date_time, :pm25) ON DUPLICATE KEY UPDATE date_time = :date_time, pm25 = :pm25", nativeQuery=true)
    Integer upsertPM25(@Param("address_level_one") String address_level_one, @Param("date_time") String date_time, @Param("pm25") int pm25);

    @Modifying
    @Transactional
    @Query(value="INSERT INTO air_test(address_level_one, address_level_two, date_time, pm100, pm25) VALUES (:address_level_one, :address_level_two, :date_time, :pm100, :pm25) ON DUPLICATE KEY UPDATE date_time = :date_time, pm100 = :pm100, pm25 = :pm25", nativeQuery=true)
    Integer upsertAir(@Param("address_level_one") String address_level_one, @Param("address_level_two") String address_level_two, @Param("date_time") String date_time, @Param("pm100") int pm100, @Param("pm25") int pm25);
}
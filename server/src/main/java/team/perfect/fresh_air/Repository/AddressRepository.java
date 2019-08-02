package team.perfect.fresh_air.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import team.perfect.fresh_air.DAO.AddressDAO;
import team.perfect.fresh_air.DAO.PositionPK;

public interface AddressRepository extends CrudRepository<AddressDAO, PositionPK> {
    @Query(value="SELECT * FROM address WHERE ABS(latitude - :latitude) < 1e-4 AND ABS(longitude - :longitude) < 1e-4 LIMIT 1", nativeQuery=true)
    Optional<AddressDAO> findAddress(@Param("latitude") float latitude, @Param("longitude") float longitude);
}
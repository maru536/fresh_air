package team.perfect.fresh_air.Repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.Air;
import team.perfect.fresh_air.DAO.DustPK;

public interface AirRepository extends CrudRepository<Air, AddressPK> {
}
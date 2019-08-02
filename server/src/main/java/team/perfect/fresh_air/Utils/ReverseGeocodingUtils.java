package team.perfect.fresh_air.Utils;

import java.util.Optional;

import team.perfect.fresh_air.DAO.AddressDAO;
import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.Models.Position;
import team.perfect.fresh_air.Repository.AddressRepository;

public class ReverseGeocodingUtils {
    private static final int startLatitude = 32;
    private static final int endLatitude = 39;
    private static final int startLongitude = 123;
    private static final int endLongitude = 133;
    private static final int divide = 100;

    public static AddressPK getAddressFromPosition(Position position, AddressRepository addressRepository) {
        if (isInOfBound(position)) {
            float latitude = ((float)Math.round(position.getLatitude()*divide))/divide;
            float longitude = ((float)Math.round(position.getLongitude()*divide))/divide;
            Optional<AddressDAO> queryedAddressDAO = 
                    addressRepository.findAddress(latitude, longitude);
        
            if (queryedAddressDAO.isPresent()) 
                return new AddressPK(queryedAddressDAO.get().getAddressLevelOne(), queryedAddressDAO.get().getAddressLevelTwo());
        }

        return null;
    }

    private static boolean isInOfBound(Position position) {
        return (position.getLatitude() >= startLatitude && position.getLatitude() < endLatitude
                && position.getLongitude() >= startLongitude && position.getLongitude() < endLongitude);
    }
}
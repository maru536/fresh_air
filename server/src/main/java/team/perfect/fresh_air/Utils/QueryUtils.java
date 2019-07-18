package team.perfect.fresh_air.Utils;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.PublicDust;
import team.perfect.fresh_air.Models.Position;
import team.perfect.fresh_air.Repository.PublicDustRepository;

public class QueryUtils {
    @Autowired
    private static PublicDustRepository publicDustRepository;

    public static PublicDust queryPublicDustByPosition(Position position) {
        AddressPK address = ReverseGeocodingUtils.getAddressFromPosition(position);
        Optional<PublicDust> publicDust = publicDustRepository.findById(address);

        if (publicDust.isPresent())
            return publicDust.get();
        else {
            publicDust = publicDustRepository.findById(new AddressPK(address.getAddressLevelOne(), ""));

            if (publicDust.isPresent())
                return publicDust.get();
            else
                return null;
        }
    }
}
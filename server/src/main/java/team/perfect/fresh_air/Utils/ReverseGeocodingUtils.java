package team.perfect.fresh_air.Utils;

import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;

import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.Geocoding.Address;
import team.perfect.fresh_air.Geocoding.NominatimReverseGeocodingJAPI;
import team.perfect.fresh_air.Models.Position;

public class ReverseGeocodingUtils {
    public static AddressPK getAddressFromPosition(Position position) {
        NominatimReverseGeocodingJAPI reverseGeocoding = new NominatimReverseGeocodingJAPI();

        Address address = reverseGeocoding.getAdress(position.getLatitude(), position.getLongitude());
        return getAddressPKFromDisplayName(address.getDisplayName());
    }

    private static AddressPK getAddressPKFromDisplayName(String displayName) {
        String[] addressLevel;

        try {
            addressLevel = displayName.split(", ");
        } catch (PatternSyntaxException e) {
            return null;
        }

        if (addressLevel.length >= 3 && getCountry(addressLevel).equals("대한민국"))
            return getAddressPK(addressLevel);
        else
            return null;
    }

    private static String getCountry(String[] addressLevel) {
        return addressLevel[addressLevel.length - 1];
    }

    private static AddressPK getAddressPK(String[] addressLevel) {
        return new AddressPK(getAddressLevelOne(addressLevel), getAddressLevelTwo(addressLevel));
    }

    private static String getAddressLevelOne(String[] addressLevel) {
        if (StringUtils.isNumeric(addressLevel[addressLevel.length - 2]))
            return addressLevel[addressLevel.length - 3];
        else
            return addressLevel[addressLevel.length - 2];
    }

    private static String getAddressLevelTwo(String[] addressLevel) {
        if (StringUtils.isNumeric(addressLevel[addressLevel.length - 2]))
            return addressLevel[addressLevel.length - 4];
        else
            return addressLevel[addressLevel.length - 3];
    }

}
package team.perfect.fresh_air.Utils;

import java.util.regex.PatternSyntaxException;

import eu.bitm.NominatimReverseGeocoding.Address;
import eu.bitm.NominatimReverseGeocoding.NominatimReverseGeocodingJAPI;
import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.Models.Position;

public class ReverseGeocodingUtils {
    public static AddressPK getAddressFromPosition(Position position) {
        NominatimReverseGeocodingJAPI reverseGeocoding = new NominatimReverseGeocodingJAPI();

        Address address = reverseGeocoding.getAdress(position.getLatitude(), position.getLongitude());
        String test = "";
        return new AddressPK("", "");
    }

    private static AddressPK getAddressPKFromDisplayName(String displayName) {
        String[] addressLevel;

        try {
            addressLevel = displayName.split(", ");
        } catch (PatternSyntaxException e) {
            return null;
        }

        if (addressLevel.length >= 4 && getCountry(addressLevel).equals("대한민국"))
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
        return addressLevel[addressLevel.length - 3];
    }

    private static String getAddressLevelTwo(String[] addressLevel) {
        return addressLevel[addressLevel.length - 4];
    }
}
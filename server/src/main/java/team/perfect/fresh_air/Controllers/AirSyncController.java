package team.perfect.fresh_air.Controllers;

import java.util.Calendar;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import team.perfect.fresh_air.Api.AirServerInterface;
import team.perfect.fresh_air.Contract.AddressLevelOneContract;
import team.perfect.fresh_air.Contract.AirItemCodeContract;
import team.perfect.fresh_air.Contract.TestLocationContract;
import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.DustWithLocationDAO;
import team.perfect.fresh_air.DAO.PublicDust;
import team.perfect.fresh_air.Models.Position;
import team.perfect.fresh_air.Repository.PublicDustRepository;
import team.perfect.fresh_air.Utils.ReverseGeocodingUtils;
import team.perfect.fresh_air.Repository.AddressRepository;
import team.perfect.fresh_air.Repository.DustWithLocationRepository;

@Component
public class AirSyncController {
    private static final long API_CALL_INTERVAL = 5000;

    @Autowired
    private PublicDustRepository airRepository;
    @Autowired
    private DustWithLocationRepository dustWithLocationRepository;
    @Autowired
    private PublicDustRepository publicDustRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Scheduled(cron = "0 30 * * * *")
    public void syncLevelTwoAir() {
        AirServerInterface airServer = new AirServerInterface(airRepository);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (AddressLevelOneContract address : AddressLevelOneContract.values()) {
                    airServer.getLevelTwoAirData(address);
                    try {
                        Thread.sleep(API_CALL_INTERVAL);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }

    @Scheduled(cron = "0 35 * * * *")
    public void syncLevelOneAir() {
        AirServerInterface airServer = new AirServerInterface(airRepository);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (AirItemCodeContract itemCode : AirItemCodeContract.values()) {
                    airServer.getLevelOneAirData(itemCode);
                    try {
                        Thread.sleep(API_CALL_INTERVAL);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void postDustForTestUser() {
        Random random = new Random();
        long currentTime = System.currentTimeMillis();
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTimeInMillis(currentTime);
        int hourOfDay = currentDate.get(Calendar.HOUR_OF_DAY);
        TestLocationContract location;

        if (hourOfDay < 8)
            location = TestLocationContract.Home;
        else if (hourOfDay < 16)
            location = TestLocationContract.SCHOOL;
        else
            location = TestLocationContract.WORK;

        DustWithLocationDAO dust = new DustWithLocationDAO("testuser", currentTime, random.nextInt(100),
                random.nextInt(150), "GPS", 10.0f, location.getLatitude(), location.getLongitude());
        dust.setPublicDust(
                queryPublicDustByPosition(new Position(location.getLatitude(), location.getLongitude())));
        this.dustWithLocationRepository.save(dust);
    }

    private PublicDust queryPublicDustByPosition(Position position) {
        AddressPK address = ReverseGeocodingUtils.getAddressFromPosition(position, addressRepository);
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
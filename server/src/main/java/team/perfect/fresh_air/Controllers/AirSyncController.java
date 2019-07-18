package team.perfect.fresh_air.Controllers;

import java.util.Calendar;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import team.perfect.fresh_air.Api.AirServerInterface;
import team.perfect.fresh_air.Contract.AddressLevelOneContract;
import team.perfect.fresh_air.Contract.AirItemCodeContract;
import team.perfect.fresh_air.Contract.TestLocationContract;
import team.perfect.fresh_air.DAO.DustWithLocationDAO;
import team.perfect.fresh_air.Models.Position;
import team.perfect.fresh_air.Repository.PublicDustRepository;
import team.perfect.fresh_air.Utils.QueryUtils;
import team.perfect.fresh_air.Repository.DustWithLocationRepository;

@Component
public class AirSyncController {
    private static final long API_CALL_INTERVAL = 5000;

    @Autowired
    private PublicDustRepository airRepository;
    @Autowired
    private DustWithLocationRepository dustWithLocationRepository;

    @Scheduled(cron = "0 30 * * * *")
    public void syncLevelTwoAir() {
        AirServerInterface airServer = new AirServerInterface();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (AddressLevelOneContract address : AddressLevelOneContract.values()) {
                    airServer.getLevelTwoAirData(address, airRepository);
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
        AirServerInterface airServer = new AirServerInterface();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (AirItemCodeContract itemCode : AirItemCodeContract.values()) {
                    airServer.getLevelOneAirData(itemCode, airRepository);
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
                QueryUtils.queryPublicDustByPosition(new Position(location.getLatitude(), location.getLongitude())));
        this.dustWithLocationRepository.save(dust);
    }
}
package team.perfect.fresh_air.Controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import team.perfect.fresh_air.Api.AirServerInterface;
import team.perfect.fresh_air.Contract.AddressLevelOneContract;
import team.perfect.fresh_air.Contract.AirItemCodeContract;
import team.perfect.fresh_air.DAO.LatestDust;
import team.perfect.fresh_air.Repository.AirRepository;
import team.perfect.fresh_air.Repository.DustRepository;

@Component
public class AirSyncController {
    
    @Autowired
    private AirRepository airRepository;
    @Autowired
    private DustRepository dustRepository;
    
    @Scheduled(cron = "0 30 * * * *")
    public void syncLevelTwoAir() {
        AirServerInterface airServer = new AirServerInterface();

        for (AddressLevelOneContract address : AddressLevelOneContract.values()) {
            airServer.getLevelTwoAirData(address, airRepository);
        }
    }

    @Scheduled(cron = "0 35 * * * *")
    public void syncLevelOneAir() {
        AirServerInterface airServer = new AirServerInterface();
 
        for (AirItemCodeContract itemCode : AirItemCodeContract.values())
            airServer.getLevelOneAirData(itemCode, airRepository);
    }

    @Scheduled(cron = "0 0/5 * * * *")
    public void postDustForTestUser() {
        Random random = new Random();
        LatestDust dust = new LatestDust("testuser", System.currentTimeMillis(), random.nextInt(100), random.nextInt(150));
        this.dustRepository.save(dust);
    }
}
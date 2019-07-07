package team.perfect.fresh_air.Controllers;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import team.perfect.fresh_air.Api.AirServerInterface;
import team.perfect.fresh_air.Contract.AddressLevelOneContract;
import team.perfect.fresh_air.Contract.AirItemCodeContract;
import team.perfect.fresh_air.DAO.DustWithLocationDAO;
import team.perfect.fresh_air.Repository.AirRepository;
import team.perfect.fresh_air.Repository.DustWithLocationRepository;

@Component
public class AirSyncController {
    private static final long API_CALL_INTERVAL = 5000;
    
    @Autowired
    private AirRepository airRepository;
    @Autowired
    private DustWithLocationRepository dustWithLocationRepository;
    
    @Scheduled(cron = "0 30 * * * *")
    public void syncLevelTwoAir() {
        AirServerInterface airServer = new AirServerInterface();

        new Thread(new Runnable(){
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
 
        new Thread(new Runnable(){
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
        DustWithLocationDAO dust = new DustWithLocationDAO("testuser", System.currentTimeMillis(), 
            random.nextInt(100), random.nextInt(150), "GPS", 37.4781327, 126.9493137);
        this.dustWithLocationRepository.save(dust);
    }
}
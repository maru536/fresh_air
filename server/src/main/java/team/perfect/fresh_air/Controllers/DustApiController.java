package team.perfect.fresh_air.Controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import team.perfect.fresh_air.Api.AirServerInterface;
import team.perfect.fresh_air.Contract.AddressLevelOneContract;
import team.perfect.fresh_air.Contract.TimeContract;
import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.Air;
import team.perfect.fresh_air.DAO.Dust;
import team.perfect.fresh_air.DAO.DustWithLocationDAO;
import team.perfect.fresh_air.DAO.UserDust;
import team.perfect.fresh_air.Models.Response;
import team.perfect.fresh_air.Models.ResponseAir;
import team.perfect.fresh_air.Models.ResponseCoaching;
import team.perfect.fresh_air.Models.ResponseDust;
import team.perfect.fresh_air.Models.ResponseDustList;
import team.perfect.fresh_air.Models.ResponseUserDust;
import team.perfect.fresh_air.Repository.AirRepository;
import team.perfect.fresh_air.Repository.DustWithLocationRepository;
import team.perfect.fresh_air.Utils.ChartUtils;
import team.perfect.fresh_air.Utils.CoachingUtils;

@RestController
public class DustApiController {
    @Autowired
    private AirRepository airRepository;
    @Autowired
    private DustWithLocationRepository dustWithLocationRepository;

    @PostMapping("1.0/dust")
    public Response postDust(@RequestHeader String userId, @RequestBody JsonObject dustObject) {
        DustWithLocationDAO dustWithLocation = new DustWithLocationDAO(dustObject);
        dustWithLocation.setUserId(userId);

        if (this.dustWithLocationRepository.save(dustWithLocation) != null)
            return new Response(200, "Save dust Success");
        else
            return new Response(500, "Save dust fail");
    }

    @GetMapping("1.0/lastestDust")
    public Response latestDust(@RequestHeader String userId) {
        Optional<DustWithLocationDAO> latestDust = this.dustWithLocationRepository.findFirstByUserIdOrderByTimeDesc(userId);

        if (latestDust.isPresent())
            return new ResponseUserDust(200, "Success", new UserDust(userId, latestDust.get().getPm100(), latestDust.get().getPm25()));
        else
            return new Response(404, "There is no dust data");
    }

    @PostMapping("1.0/publicDust")
    public Response publicDust(@RequestBody JsonObject address) {
        Air publicAir = queryAirByAddress(new AddressPK(address));

        if (publicAir != null)
            return new ResponseDust(200, "Success", new Dust(publicAir.getPm100(), publicAir.getPm25()));
        else
            return new Response(404, "There is no public dust data");
    }

    @PostMapping("1.0/coachingDust")
    public Response air(@RequestHeader String userId, @RequestBody JsonObject address) {
        Air publicAir = queryAirByAddress(new AddressPK(address));
        Optional<DustWithLocationDAO> latestDust = this.dustWithLocationRepository.findFirstByUserIdOrderByTimeDesc(userId);
        String coachingMessage = "";

        if (latestDust.isPresent()) {
            if (publicAir != null) {
                coachingMessage = CoachingUtils.makeDiffCoachingMessage(
                    new Dust(latestDust.get().getPm100(), latestDust.get().getPm25()), publicAir);
                coachingMessage += CoachingUtils.makeLatestDustCoachingMessage(new Dust(latestDust.get().getPm100(), latestDust.get().getPm25()));
                return new ResponseCoaching(200, "Success", coachingMessage, latestDust.get(), publicAir);
            } else {
                coachingMessage = CoachingUtils.makeLatestDustCoachingMessage(new Dust(latestDust.get().getPm100(), latestDust.get().getPm25()));
                return new ResponseCoaching(201, "Success, but there is no air data", coachingMessage, latestDust.get(),
                        new Air());
            }
        } else {
            if (publicAir != null) {
                return new ResponseAir(202, "Only air data", publicAir);
            } else {
                return new Response(404, "There is no data");
            }
        }
    }

    @GetMapping("1.0/todayDust")
    public Response todayDust(@RequestHeader String userId) {
        List<DustWithLocationDAO> dustList = queryTodayDustByUserId(System.currentTimeMillis(), userId);
        if (dustList != null && dustList.size() > 0) {
            int sumPm100 = 0;
            int countPm100 = 0;
            int sumPm25 = 0;
            int countPm25 = 0;

            for (DustWithLocationDAO dust : dustList) {
                if (dust.getPm100() >= 0) {
                    sumPm100 += dust.getPm100();
                    countPm100++;
                }

                if (dust.getPm25() >= 0) {
                    sumPm25 += dust.getPm25();
                    countPm25++;
                }
            }

            return new ResponseUserDust(200, "Success",
                    new UserDust(userId, sumPm100 / countPm100, sumPm25 / countPm25));
        } else
            return new Response(404, "There is no dust data");
    }

    @GetMapping("1.0/yesterdayDust")
    public Response yesterdayDust(@RequestHeader String userId) {
        List<DustWithLocationDAO> dustList = queryDayDustByUserId(System.currentTimeMillis() - TimeContract.A_DAY, userId);

        if (dustList != null && dustList.size() > 0) {
            int sumPm100 = 0;
            int countPm100 = 0;
            int sumPm25 = 0;
            int countPm25 = 0;

            for (DustWithLocationDAO dust : dustList) {
                if (dust.getPm100() >= 0) {
                    sumPm100 += dust.getPm100();
                    countPm100++;
                }

                if (dust.getPm25() >= 0) {
                    sumPm25 += dust.getPm25();
                    countPm25++;
                }
            }

            int avgPm25 = -1;
            int avgPm100 = -1;

            if (countPm100 > 0)
                avgPm100 = sumPm100 / countPm100;

            if (countPm25 > 0)
                avgPm25 = sumPm25 / countPm25;

            if (avgPm25 > 0 || avgPm100 > 0) {
                return new ResponseUserDust(200, "Success", new UserDust(userId, avgPm100, avgPm25));
            }
        }

        return new Response(404, "There is no dust data");
    }

    @GetMapping(path = "1.0/todayChart/{userId}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] showTodayDustChart(@PathVariable String userId) {
        long currentTime = System.currentTimeMillis();
        List<DustWithLocationDAO> dustList = queryTodayDustByUserId(currentTime, userId);

        List<Integer> pm100List = new ArrayList<>();
        List<Integer> pm25List = new ArrayList<>();
        List<Integer> hourXAxis = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        setChartData(currentTime, currentHour, dustList, pm100List, pm25List, hourXAxis);

        return ChartUtils.lineChart(pm100List, pm25List, hourXAxis);
    }

    @GetMapping(path = "1.0/yesterdayChart/{userId}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] showYesterdayChart(@PathVariable String userId) {
        long currentTime = System.currentTimeMillis();
        List<DustWithLocationDAO> dustList = queryDayDustByUserId(currentTime - TimeContract.A_DAY, userId);

        List<Integer> pm100List = new ArrayList<>();
        List<Integer> pm25List = new ArrayList<>();
        List<Integer> hourXAxis = new ArrayList<>();

        int endHour = 23;
        setChartData(currentTime - TimeContract.A_DAY, endHour, dustList, pm100List, pm25List, hourXAxis);

        return ChartUtils.lineChart(pm100List, pm25List, hourXAxis);
    }

    @GetMapping("1.0/syncUserDust")
    public Response syncUserDust(@RequestHeader String userId) {
        List<DustWithLocationDAO> userDustList = this.dustWithLocationRepository.findByUserIdAndTimeGreaterThanEqual(userId,
                getDayStartTime(System.currentTimeMillis() - TimeContract.A_DAY));

        if (userDustList != null && userDustList.size() > 0) {
            JsonArray dustArray = new JsonArray();

            for (DustWithLocationDAO exploreDust : userDustList)
                dustArray.add(exploreDust.toJsonObject());

            return new ResponseDustList(200, "Success", dustArray);
        }

        return new Response(404, "There is no dust data");
    }

    private void setChartData(long dayTime, int endHour, List<DustWithLocationDAO> dustList, List<Integer> pm100List,
            List<Integer> pm25List, List<Integer> hourXAxis) {
        int sumPm100 = 0;
        int sumPm25 = 0;
        int countPm100 = 0;
        int countPm25 = 0;
        Iterator<DustWithLocationDAO> dustIterator = dustList.iterator();
        long exploreTime = getDayStartTime(dayTime) + TimeContract.A_HOUR;
        int exploreHour;
        DustWithLocationDAO exploreDust = null;

        for (exploreHour = 0; exploreHour <= endHour; exploreHour++, exploreTime += TimeContract.A_HOUR) {
            try {
                while ((exploreDust = dustIterator.next()).getTime() < exploreTime) {
                    if (exploreDust.getPm100() >= 0) {
                        sumPm100 += exploreDust.getPm100();
                        countPm100++;
                    }

                    if (exploreDust.getPm25() >= 0) {
                        sumPm25 += exploreDust.getPm25();
                        countPm25++;
                    }
                }
            } catch (NoSuchElementException e) {

            }

            hourXAxis.add(exploreHour);
            if (countPm100 > 0)
                pm100List.add(sumPm100 / countPm100);
            else
                pm100List.add(0);

            if (countPm25 > 0)
                pm25List.add(sumPm25 / countPm25);
            else
                pm25List.add(0);

            sumPm100 = 0;
            sumPm25 = 0;
            countPm100 = 0;
            countPm25 = 0;

            if (exploreDust != null) {
                if (exploreDust.getPm100() >= 0) {
                    sumPm100 += exploreDust.getPm100();
                    countPm100++;
                }

                if (exploreDust.getPm25() >= 0) {
                    sumPm25 += exploreDust.getPm25();
                    countPm25++;
                }
            }
            exploreDust = null;
        }
    }

    private Air queryAirByAddress(AddressPK address) {
        Optional<Air> publicAir = this.airRepository.findById(address);

        if (publicAir.isPresent())
            return publicAir.get();
        else {
            publicAir = this.airRepository.findById(new AddressPK(address.getAddressLevelOne(), ""));

            if (publicAir.isPresent())
                return publicAir.get();
            else
                return null;
        }
    }

    private long getDayStartTime(long dayTime) {
        Calendar dayStartDate = Calendar.getInstance();
        dayStartDate.setTimeInMillis(dayTime);

        dayStartDate.set(Calendar.HOUR_OF_DAY, 0);
        dayStartDate.set(Calendar.MINUTE, 0);
        dayStartDate.set(Calendar.SECOND, 0);
        dayStartDate.set(Calendar.MILLISECOND, 0);
        long time = dayStartDate.getTimeInMillis();

        return dayStartDate.getTimeInMillis();
    }

    private long getDayEndTime(long dayTime) {
        Calendar dayEndDate = Calendar.getInstance();
        dayEndDate.setTimeInMillis(dayTime);

        dayEndDate.set(Calendar.HOUR_OF_DAY, 23);
        dayEndDate.set(Calendar.MINUTE, 59);
        dayEndDate.set(Calendar.SECOND, 59);
        dayEndDate.set(Calendar.MILLISECOND, 999);
        long time = dayEndDate.getTimeInMillis();

        return dayEndDate.getTimeInMillis();
    }

    private List<DustWithLocationDAO> queryTodayDustByUserId(long todayTime, String userId) {
        long dayStartTime = getDayStartTime(todayTime);

        return this.dustWithLocationRepository.findByUserIdAndTimeBetween(userId, dayStartTime, todayTime);
    }

    private List<DustWithLocationDAO> queryDayDustByUserId(long yesterdayTime, String userId) {
        return this.dustWithLocationRepository.findByUserIdAndTimeBetween(userId, getDayStartTime(yesterdayTime),
                getDayEndTime(yesterdayTime));
    }
}
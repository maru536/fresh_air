package team.perfect.fresh_air.Controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

import team.perfect.fresh_air.Contract.TimeContract;
import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.PublicDust;
import team.perfect.fresh_air.DAO.Dust;
import team.perfect.fresh_air.DAO.DustWithLocationDAO;
import team.perfect.fresh_air.DAO.UserLatestDust;
import team.perfect.fresh_air.Models.Position;
import team.perfect.fresh_air.Models.RepresentDustWithLocation;
import team.perfect.fresh_air.Models.Response;
import team.perfect.fresh_air.Models.ResponseCoaching;
import team.perfect.fresh_air.Models.ResponsePublicDust;
import team.perfect.fresh_air.Models.ResponseDustList;
import team.perfect.fresh_air.Models.ResponseRepresentDustWithLocation;
import team.perfect.fresh_air.Models.ResponseUserLatestDust;
import team.perfect.fresh_air.Repository.DustWithLocationRepository;
import team.perfect.fresh_air.Repository.PublicDustRepository;
import team.perfect.fresh_air.Utils.ChartUtils;
import team.perfect.fresh_air.Utils.CoachingUtils;
import team.perfect.fresh_air.Utils.DustWithLocationUtils;
import team.perfect.fresh_air.Utils.ReverseGeocodingUtils;

@RestController
public class DustApiController {
    @Autowired
    private PublicDustRepository publicDustRepository;
    @Autowired
    private DustWithLocationRepository dustWithLocationRepository;

    @PostMapping("1.0/dust")
    public Response postDust(@RequestHeader String userId, @RequestBody JsonObject dustObject) {
        try {
            DustWithLocationDAO dustWithLocation = new DustWithLocationDAO(dustObject);
            PublicDust publicDust = queryPublicDustByPosition(new Position(dustWithLocation));

            dustWithLocation.setUserId(userId);
            dustWithLocation.setPublicDust(publicDust);

            if (this.dustWithLocationRepository.save(dustWithLocation) != null)
                return new Response(200, "Save dust Success");
            else
                return new Response(500, "Save dust fail");
        } catch (NullPointerException | ClassCastException | IllegalStateException e) {
            return new Response(400, "Malfrom request body");
        }
    }

    @GetMapping("1.0/lastestDust")
    public Response latestDust(@RequestHeader String userId) {
        Optional<DustWithLocationDAO> latestDust = this.dustWithLocationRepository.findLatestDust(userId);

        if (latestDust.isPresent())
            return new ResponseUserLatestDust(200, "Success", new UserLatestDust(userId, latestDust.get().getTime(),
                    latestDust.get().getPm100(), latestDust.get().getPm25()));
        else
            return new Response(404, "There is no dust data");
    }

    @PostMapping("1.0/publicDust")
    public Response publicDust(@RequestBody JsonObject position) {
        try {
            PublicDust publicDust = queryPublicDustByPosition(new Position(position));

            if (publicDust != null)
                return new ResponsePublicDust(200, "Success", publicDust);
            else
                return new Response(404, "There is no public dust data");
        } catch (NullPointerException | ClassCastException | IllegalStateException e) {
            return new Response(400, "Malform request body");
        }
    }

    @PostMapping("1.0/coachingDust")
    public Response coachingDust(@RequestHeader String userId, @RequestBody JsonObject position) {
        PublicDust publicDust = queryPublicDustByPosition(new Position(position));
        Optional<DustWithLocationDAO> latestDust = this.dustWithLocationRepository.findLatestDust(userId);
        String coachingMessage = "";

        if (latestDust.isPresent()) {
            if (publicDust != null) {
                coachingMessage = CoachingUtils.makeDiffCoachingMessage(
                        new Dust(latestDust.get().getPm100(), latestDust.get().getPm25()), publicDust);
                coachingMessage += CoachingUtils.makeLatestDustCoachingMessage(
                        new Dust(latestDust.get().getPm100(), latestDust.get().getPm25()));
                return new ResponseCoaching(200, "Success", coachingMessage, latestDust.get(), publicDust);
            } else {
                coachingMessage = CoachingUtils.makeLatestDustCoachingMessage(
                        new Dust(latestDust.get().getPm100(), latestDust.get().getPm25()));
                return new ResponseCoaching(201, "Success, but there is no public dust", coachingMessage,
                        latestDust.get(), new PublicDust());
            }
        } else {
            if (publicDust != null) {
                return new ResponsePublicDust(202, "Only public dust", publicDust);
            } else {
                return new Response(404, "There is no data");
            }
        }
    }

    private Dust calculateAvgPublicDust(List<DustWithLocationDAO> dustList) {
        int sumPm100 = 0;
        int countPm100 = 0;
        int sumPm25 = 0;
        int countPm25 = 0;
        int avgPm25 = -1;
        int avgPm100 = -1;

        if (dustList != null && dustList.size() > 0) {
            for (DustWithLocationDAO dust : dustList) {
                int pm100, pm25;

                pm100 = dust.getPublicPm100();
                pm25 = dust.getPublicPm25();

                if (pm100 >= 0) {
                    sumPm100 += pm100;
                    countPm100++;
                }

                if (pm25 >= 0) {
                    sumPm25 += pm25;
                    countPm25++;
                }
            }

            if (countPm100 > 0)
                avgPm100 = sumPm100 / countPm100;

            if (countPm25 > 0)
                avgPm25 = sumPm25 / countPm25;
        }

        return new Dust(avgPm100, avgPm25);
    }

    private Dust calculateAvgMeasuredDust(List<DustWithLocationDAO> dustList) {
        int sumPm100 = 0;
        int countPm100 = 0;
        int sumPm25 = 0;
        int countPm25 = 0;
        int avgPm25 = -1;
        int avgPm100 = -1;

        if (dustList != null && dustList.size() > 0) {
            for (DustWithLocationDAO dust : dustList) {
                int pm100, pm25;

                pm100 = dust.getPm100();
                pm25 = dust.getPm25();

                if (pm100 >= 0) {
                    sumPm100 += pm100;
                    countPm100++;
                }

                if (pm25 >= 0) {
                    sumPm25 += pm25;
                    countPm25++;
                }
            }

            if (countPm100 > 0)
                avgPm100 = sumPm100 / countPm100;

            if (countPm25 > 0)
                avgPm25 = sumPm25 / countPm25;
        }

        return new Dust(avgPm100, avgPm25);
    }

    @GetMapping(path = "public/todayChart/{userId}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] showTodayPublicDustChart(@PathVariable String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<DustWithLocationDAO> dustList = queryTodayAllDustByUserId(currentTimeMillis, userId);

        return makeTodayPublicDustLineChart(currentTimeMillis, dustList);
    }

    @GetMapping(path = "measure/todayChart/{userId}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] showTodayMeasuredDustChart(@PathVariable String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<DustWithLocationDAO> dustList = queryTodayMeasuredDustByUserId(currentTimeMillis, userId);

        return makeTodayMeasuredDustLineChart(currentTimeMillis, dustList);
    }

    private byte[] makeTodayPublicDustLineChart(long currentTimeMillis, List<DustWithLocationDAO> dustList) {
        List<Integer> pm100List = new ArrayList<>();
        List<Integer> pm25List = new ArrayList<>();
        List<Integer> hourXAxis = new ArrayList<>();

        int currentHour = getCurrentHour(currentTimeMillis);

        setAllChartData(currentTimeMillis, currentHour, dustList, pm100List, pm25List, hourXAxis);

        return ChartUtils.lineChart(pm100List, pm25List, hourXAxis);
    }

    private byte[] makeTodayMeasuredDustLineChart(long currentTimeMillis, List<DustWithLocationDAO> dustList) {
        List<Integer> pm100List = new ArrayList<>();
        List<Integer> pm25List = new ArrayList<>();
        List<Integer> hourXAxis = new ArrayList<>();

        int currentHour = getCurrentHour(currentTimeMillis);

        setMeasuredChartData(currentTimeMillis, currentHour, dustList, pm100List, pm25List, hourXAxis);

        return ChartUtils.lineChart(pm100List, pm25List, hourXAxis);
    }

    private int getCurrentHour(long currentTimeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    @GetMapping(path = "public/yesterdayChart/{userId}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] showYesterdayPublicDustChart(@PathVariable String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<DustWithLocationDAO> dustList = queryDayAllDustByUserId(currentTimeMillis - TimeContract.A_DAY, userId);
        List<Integer> pm100List = new ArrayList<>();
        List<Integer> pm25List = new ArrayList<>();
        List<Integer> hourXAxis = new ArrayList<>();
        int endHour = 23;

        setAllChartData(currentTimeMillis - TimeContract.A_DAY, endHour, dustList, pm100List, pm25List, hourXAxis);

        return ChartUtils.lineChart(pm100List, pm25List, hourXAxis);
    }

    @GetMapping(path = "measure/yesterdayChart/{userId}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] showYesterdayMeasuredDustChart(@PathVariable String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<DustWithLocationDAO> dustList = queryDayMeasuredDustByUserId(currentTimeMillis - TimeContract.A_DAY,
                userId);
        List<Integer> pm100List = new ArrayList<>();
        List<Integer> pm25List = new ArrayList<>();
        List<Integer> hourXAxis = new ArrayList<>();
        int endHour = 23;

        setMeasuredChartData(currentTimeMillis - TimeContract.A_DAY, endHour, dustList, pm100List, pm25List, hourXAxis);

        return ChartUtils.lineChart(pm100List, pm25List, hourXAxis);
    }

    @GetMapping("public/todayDust/hour")
    public Response showTodayPublicDustByHour(@RequestHeader String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<Integer> pm100List = new ArrayList<>();
        List<Integer> pm25List = new ArrayList<>();
        List<Integer> hourXAxis = new ArrayList<>();
        List<DustWithLocationDAO> dustList = queryTodayAllDustByUserId(currentTimeMillis, userId);

        int currentHour = getCurrentHour(currentTimeMillis);

        if (dustList != null && dustList.size() > 0) {
            setAllChartData(currentTimeMillis, currentHour, dustList, pm100List, pm25List, hourXAxis);
            return makeResponseDustListFromDustList(pm100List, pm25List);
        } else
            return new Response(404, "There is no dust data");
    }

    @GetMapping("measure/todayDust/hour")
    public Response showTodayMeasureDustByHour(@RequestHeader String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<Integer> pm100List = new ArrayList<>();
        List<Integer> pm25List = new ArrayList<>();
        List<Integer> hourXAxis = new ArrayList<>();
        List<DustWithLocationDAO> dustList = queryTodayMeasuredDustByUserId(currentTimeMillis, userId);

        int currentHour = getCurrentHour(currentTimeMillis);

        if (dustList != null && dustList.size() > 0) {
            setMeasuredChartData(currentTimeMillis, currentHour, dustList, pm100List, pm25List, hourXAxis);
            return makeResponseDustListFromDustList(pm100List, pm25List);
        } else
            return new Response(404, "There is no dust data");
    }

    @GetMapping("public/yesterdayDust/hour")
    public Response showYesterdayPublicDustByHour(@RequestHeader String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<Integer> pm100List = new ArrayList<>();
        List<Integer> pm25List = new ArrayList<>();
        List<Integer> hourXAxis = new ArrayList<>();
        List<DustWithLocationDAO> dustList = queryDayAllDustByUserId(currentTimeMillis - TimeContract.A_DAY, userId);
        int endHour = 23;

        if (dustList != null && dustList.size() > 0) {
            setAllChartData(currentTimeMillis - TimeContract.A_DAY, endHour, dustList, pm100List, pm25List, hourXAxis);
            return makeResponseDustListFromDustList(pm100List, pm25List);
        } else
            return new Response(404, "There is no dust data");
    }

    @GetMapping("measure/yesterdayDust/hour")
    public Response showYesterdayMeasuredDustByHour(@RequestHeader String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<Integer> pm100List = new ArrayList<>();
        List<Integer> pm25List = new ArrayList<>();
        List<Integer> hourXAxis = new ArrayList<>();
        List<DustWithLocationDAO> dustList = queryDayMeasuredDustByUserId(currentTimeMillis - TimeContract.A_DAY,
                userId);
        int endHour = 23;

        if (dustList != null && dustList.size() > 0) {
            setMeasuredChartData(currentTimeMillis - TimeContract.A_DAY, endHour, dustList, pm100List, pm25List,
                    hourXAxis);
            return makeResponseDustListFromDustList(pm100List, pm25List);
        } else
            return new Response(404, "There is no dust data");
    }

    private ResponseDustList makeResponseDustListFromDustList(List<Integer> pm100List, List<Integer> pm25List) {
        JsonArray pm100Array = new JsonArray();
        for (int pm100 : pm100List)
            pm100Array.add(pm100);

        JsonArray pm25Array = new JsonArray();
        for (int pm25 : pm25List)
            pm25Array.add(pm25);

        return new ResponseDustList(200, "Success", pm100Array, pm25Array);
    }

    @GetMapping("public/todayDustMap")
    public Response todayPublicDustMap(@RequestHeader String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<DustWithLocationDAO> dustLocationList = queryTodayAllDustByUserId(currentTimeMillis, userId);
        List<RepresentDustWithLocation> allRepresentDustLocation = DustWithLocationUtils
                .representAllDustWithLocation(dustLocationList);

        if (allRepresentDustLocation.size() > 0)
            return new ResponseRepresentDustWithLocation(200, "Success", 
                    userId, calculateAvgPublicDust(dustLocationList), allRepresentDustLocation);

        else
            return new Response(404, "There is no dust data");
    }

    @GetMapping("measure/todayDustMap")
    public Response todayMeasuredDustMap(@RequestHeader String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<DustWithLocationDAO> dustLocationList = queryTodayMeasuredDustByUserId(currentTimeMillis, userId);
        List<RepresentDustWithLocation> allRepresentDustLocation = DustWithLocationUtils
                .representMeasuredDustWithLocation(dustLocationList);

        if (allRepresentDustLocation.size() > 0)
            return new ResponseRepresentDustWithLocation(200, "Success", 
                    userId, calculateAvgMeasuredDust(dustLocationList), allRepresentDustLocation);

        else
            return new Response(404, "There is no dust data");
    }

    @GetMapping("public/yesterdayDustMap")
    public Response yesterdayPublicDustMap(@RequestHeader String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<DustWithLocationDAO> dustLocationList = queryDayAllDustByUserId(currentTimeMillis - TimeContract.A_DAY, userId);
        List<RepresentDustWithLocation> allRepresentDustLocation = DustWithLocationUtils
                .representAllDustWithLocation(dustLocationList);

        if (allRepresentDustLocation.size() > 0)
            return new ResponseRepresentDustWithLocation(200, "Success", 
                    userId, calculateAvgPublicDust(dustLocationList), allRepresentDustLocation);
        else
            return new Response(404, "There is no dust data");
    }

    @GetMapping("measure/yesterdayDustMap")
    public Response yesterdayMeasuredDustMap(@RequestHeader String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        List<DustWithLocationDAO> dustLocationList = queryDayMeasuredDustByUserId(currentTimeMillis - TimeContract.A_DAY, userId);
        List<RepresentDustWithLocation> allRepresentDustLocation = DustWithLocationUtils
                .representMeasuredDustWithLocation(dustLocationList);

        if (allRepresentDustLocation.size() > 0)
            return new ResponseRepresentDustWithLocation(200, "Success", 
                    userId, calculateAvgMeasuredDust(dustLocationList), allRepresentDustLocation);
        else
            return new Response(404, "There is no dust data");
    }

    private void setMeasuredChartData(long dayTime, int endHour, List<DustWithLocationDAO> dustList,
            List<Integer> pm100List, List<Integer> pm25List, List<Integer> hourXAxis) {
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
                exploreDust = null;
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
                int hour;
                for (hour = 1; hour < 24; hour++) {
                    if (exploreDust.getTime() >= exploreTime + hour*TimeContract.A_HOUR) {
                        hourXAxis.add(exploreHour + hour);
                        pm100List.add(0);
                        pm25List.add(0);
                    }
                    else
                        break; 
                }

                exploreHour += hour - 1;
                exploreTime += (hour - 1) * TimeContract.A_HOUR;
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

    private void setAllChartData(long dayTime, int endHour, List<DustWithLocationDAO> dustList, List<Integer> pm100List,
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
                    if (exploreDust.getPublicPm100() >= 0) {
                        sumPm100 += exploreDust.getPublicPm100();
                        countPm100++;
                    }

                    if (exploreDust.getPublicPm25() >= 0) {
                        sumPm25 += exploreDust.getPublicPm25();
                        countPm25++;
                    }
                }
            } catch (NoSuchElementException e) {
                exploreDust = null;
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
                int hour;
                for (hour = 1; hour < 24; hour++) {
                    if (exploreDust.getTime() >= exploreTime + hour*TimeContract.A_HOUR) {
                        hourXAxis.add(exploreHour + hour);
                        pm100List.add(0);
                        pm25List.add(0);
                    }
                    else
                        break; 
                }

                exploreHour += hour - 1;
                exploreTime += (hour - 1) * TimeContract.A_HOUR;
                if (exploreDust.getPublicPm100() >= 0) {
                    sumPm100 += exploreDust.getPublicPm100();
                    countPm100++;
                }

                if (exploreDust.getPublicPm25() >= 0) {
                    sumPm25 += exploreDust.getPublicPm25();
                    countPm25++;
                }
            }
            exploreDust = null;
        }
    }

    private PublicDust queryPublicDustByPosition(Position position) {
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

    private long getDayStartTime(long dayTime) {
        Calendar dayStartDate = Calendar.getInstance();
        dayStartDate.setTimeInMillis(dayTime);

        dayStartDate.set(Calendar.HOUR_OF_DAY, 0);
        dayStartDate.set(Calendar.MINUTE, 0);
        dayStartDate.set(Calendar.SECOND, 0);
        dayStartDate.set(Calendar.MILLISECOND, 0);

        return dayStartDate.getTimeInMillis();
    }

    private long getDayEndTime(long dayTime) {
        Calendar dayEndDate = Calendar.getInstance();
        dayEndDate.setTimeInMillis(dayTime);

        dayEndDate.set(Calendar.HOUR_OF_DAY, 23);
        dayEndDate.set(Calendar.MINUTE, 59);
        dayEndDate.set(Calendar.SECOND, 59);
        dayEndDate.set(Calendar.MILLISECOND, 999);

        return dayEndDate.getTimeInMillis();
    }

    private List<DustWithLocationDAO> queryTodayMeasuredDustByUserId(long todayTime, String userId) {
        long dayStartTime = getDayStartTime(todayTime);

        return this.dustWithLocationRepository.findMeasuredDust(userId, dayStartTime, todayTime);
    }

    private List<DustWithLocationDAO> queryDayMeasuredDustByUserId(long yesterdayTime, String userId) {
        return this.dustWithLocationRepository.findMeasuredDust(userId, getDayStartTime(yesterdayTime),
                getDayEndTime(yesterdayTime));
    }

    private List<DustWithLocationDAO> queryTodayAllDustByUserId(long todayTime, String userId) {
        long dayStartTime = getDayStartTime(todayTime);

        return this.dustWithLocationRepository.findAllDust(userId, dayStartTime, todayTime);
    }

    private List<DustWithLocationDAO> queryDayAllDustByUserId(long yesterdayTime, String userId) {
        return this.dustWithLocationRepository.findAllDust(userId, getDayStartTime(yesterdayTime),
                getDayEndTime(yesterdayTime));
    }
}
package team.perfect.fresh_air.Controllers;


import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import team.perfect.fresh_air.Api.AirServerInterface;
import team.perfect.fresh_air.Contract.AddressLevelOneContract;
import team.perfect.fresh_air.Contract.AirContract;
import team.perfect.fresh_air.Contract.ApiContract;
import team.perfect.fresh_air.Contract.CityContract;
import team.perfect.fresh_air.Contract.DustStandard;
import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.Air;
import team.perfect.fresh_air.DAO.Dust;
import team.perfect.fresh_air.DAO.User;
import team.perfect.fresh_air.Model.Response;
import team.perfect.fresh_air.Model.ResponseAir;
import team.perfect.fresh_air.Model.ResponseCoaching;
import team.perfect.fresh_air.Model.ResponseDust;
import team.perfect.fresh_air.Repository.DustRepository;
import team.perfect.fresh_air.Repository.UserRepository;
import team.perfect.fresh_air.Repository.AirRepository;

@RestController
public class RestApiController {

    @Autowired
    private DustRepository dustRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AirRepository airRepository;

    @Bean
    public CommonsRequestLoggingFilter commonsRequestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeHeaders(true);
        filter.setIncludePayload(true);
        filter.setIncludeQueryString(true);
        filter.setMaxPayloadLength(1000);
        return filter;
    }

    @PostMapping("1.0/signUp")
    public Response signUp(@RequestBody User newUser) {
        Optional<User> user = this.userRepository.findById(newUser.getId());
        if (user.isPresent())
            return new Response(302, "Already registed user");
        else {
            if (this.userRepository.save(newUser) != null)
                return new Response(201, "Register new user");
            else
                return new Response(500, "Save new user fail");
        }
    }

    @GetMapping("1.0/signIn")
    public Response signIn(@RequestHeader String id, @RequestHeader String passwd) {
        Optional<User> user = this.userRepository.findById(id);
        
        if (user.isPresent()) {
            if (user.get().getPasswd().equals(passwd))
                return new Response(200, "SignIn Success");
            else
                return new Response(401, "Passwd incorrect");
        }
        else
            return new Response(404, "Not registered user");
    }

    @PostMapping("1.0/dust")
    public Response postDust(@RequestHeader String userId, @RequestBody JsonObject dustObject) {
        Dust dust = new Dust(dustObject);
        dust.setUserId(userId);
        dust.setCo2(0.3f);
        if (this.dustRepository.save(dust) != null)
            return new Response(200, "Save dust Success");
        else 
            return new Response(500, "Save dust fail");
    }
    
    @GetMapping("1.0/lastestDust")
    public Response latestDust(@RequestHeader String userId) {
        Optional<Dust> latestDust = this.dustRepository.findFirstByUserIdOrderByTimeDesc(userId);

        if (latestDust.isPresent())
            return new ResponseDust(200, "Success", latestDust.get());
        else
            return new Response(404, "There is no dust data");
    }

    @PostMapping("1.0/coachingDust")
    public Response air(@RequestHeader String userId, @RequestBody JsonObject address) {
        AddressPK id = new AddressPK(address.get("levelOne").getAsString(), address.get("levelTwo").getAsString());
        //AddressPK id = new AddressPK(addressLevelOne, addressLevelTwo);
        Optional<Air> airData = this.airRepository.findById(id);
        Optional<Dust> latestDust = this.dustRepository.findFirstByUserIdOrderByTimeDesc(userId);
        String coachingMessage = "";
        
        if (latestDust.isPresent()) {
            if (airData.isPresent()) {
                coachingMessage = makeDiffCoachingMessage(latestDust.get(), airData.get());
                coachingMessage += makeLatestDustCoachingMessage(latestDust.get());
                return new ResponseCoaching(200, "Success", coachingMessage, latestDust.get(), airData.get());
            }
            else {
                coachingMessage = makeLatestDustCoachingMessage(latestDust.get());
                return new ResponseCoaching(201, "Success, but there is no air data", 
                    coachingMessage, latestDust.get(), new Air());
            }
        }
        else {
            if (airData.isPresent()) {
                return new ResponseAir(202, "Only air data", airData.get());
            }
            else {
                return new Response(404, "There is no data");
            }
        }
    }

    private static String makeLatestDustCoachingMessage(Dust dust) {
        String message;

        if (dust.getPm100() < DustStandard.goodPm100)
            message = "미세먼지가 좋아요! 산책하시는건 어떤가요?\n";
        else if (dust.getPm100() < DustStandard.normalPm100) 
            message = "미세먼지가 보통이네요. 일상을 즐겨주세요.\n";
        else if (dust.getPm100() < DustStandard.badPm100) 
            message = "미세먼지가 나빠요... 마스크를 착용하세요.\n";
        else 
            message = "미세먼지가 매우 나빠요. 외출을 자제해주세요!\n";

        if (dust.getPm25() < DustStandard.goodPm25)
            message += "초미세먼지가 좋아요! 산책하시는건 어떤가요?";
        else if (dust.getPm25() < DustStandard.normalPm25) 
            message += "초미세먼지가 보통이네요. 일상을 즐겨주세요.";
        else if (dust.getPm25() < DustStandard.badPm25) 
            message += "초미세먼지가 나빠요... 마스크를 착용하세요.";
        else 
            message += "초미세먼지가 매우 나빠요. 외출을 자제해주세요!";
        
        return message;
    }

    private static String makeDiffCoachingMessage(Dust dust, Air air) {
        String message = "";
        int diffPm100 = dust.getPm100() - air.getPm100();
        int diffPm25 = dust.getPm25() - air.getPm25();

        if (air.getPm100() > 0 && Math.abs(diffPm100) > 50) {
            if (diffPm100 > 0) {
                message += "여기는 주변보다 미세먼지 농도가 높아요.\n";
            }
            else {
                message += "여기는 주변보다 미세먼지 농도가 낮아요.\n";
            }
        }

        if (air.getPm25() > 0 && Math.abs(diffPm25) > 50) {
            if (diffPm25 > 0) {
                message += "여기는 주변보다 초미세먼지 농도가 높아요.\n";
            }
            else {
                message += "여기는 주변보다 초미세먼지 농도가 낮아요.\n";
            }
        }
        
        return message;
    }

    @PostMapping("1.0/test")
    public void Test(@RequestHeader String address) {
        AirServerInterface airServer = new AirServerInterface();

        AddressLevelOneContract addressLevelOne;

        try {
            addressLevelOne = AddressLevelOneContract.valueOf(address);
            airServer.getAirData(AddressLevelOneContract.valueOf(address), this.airRepository);
        }
        catch (IllegalArgumentException iae) {
            System.out.println("Invaild address: "+address);
        }
    }
}
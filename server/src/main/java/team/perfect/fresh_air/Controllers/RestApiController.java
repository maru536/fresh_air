package team.perfect.fresh_air.Controllers;


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
import team.perfect.fresh_air.DAO.AddressPK;
import team.perfect.fresh_air.DAO.Air;
import team.perfect.fresh_air.DAO.Dust;
import team.perfect.fresh_air.DAO.User;
import team.perfect.fresh_air.Model.Response;
import team.perfect.fresh_air.Model.ResponseAir;
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

    @PostMapping("1.0/air")
    public Response air(@RequestBody JsonObject address) {
        AddressPK id = new AddressPK(address.get("levelOne").getAsString(), address.get("levelTwo").getAsString());
        Optional<Air> airData = this.airRepository.findById(id);

        if (airData.isPresent())
            return new ResponseAir(200, "Success", airData.get().getPm100(), airData.get().getPm25());
        else
            return new Response(404, "There is no air data");
    }

    /*
    @PostMapping("1.0/test")
    public void Test() {
        AirServerInterface airServer = new AirServerInterface();

        for (AddressLevelOneContract address : AddressLevelOneContract.values()) {
            airServer.getAirData(address.getKey(), this.airRepository);
        }
    }
    */
}
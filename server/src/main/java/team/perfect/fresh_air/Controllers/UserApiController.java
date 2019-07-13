package team.perfect.fresh_air.Controllers;

import java.util.Optional;

import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import team.perfect.fresh_air.DAO.User;
import team.perfect.fresh_air.Models.Response;
import team.perfect.fresh_air.Repository.UserRepository;

@RestController
public class UserApiController {
    @Autowired
    private UserRepository userRepository;

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
    public Response signUp(@RequestBody JsonObject signUpBody) {
        User signUpUser = null;
        try {
            signUpUser = new User(signUpBody);
        } catch (NullPointerException | ClassCastException | IllegalStateException e) {
            return new Response(400, "Malformed body");
        }

        Optional<User> user = this.userRepository.findById(signUpUser.getUserId());
        if (user.isPresent())
            return new Response(302, "Already registed user");
        else {
            if (this.userRepository.save(signUpUser) != null)
                return new Response(201, "Register new user");
            else
                return new Response(500, "Save new user fail");
        }
    }

    @GetMapping("1.0/signIn")
    public Response signIn(@RequestHeader String userId, @RequestHeader String passwd) {
        Optional<User> signInUser = this.userRepository.findById(userId);

        if (signInUser.isPresent()) {
            if (signInUser.get().getPasswd().equals(passwd))
                return new Response(200, "SignIn Success");
            else
                return new Response(401, "Passwd incorrect");
        } else
            return new Response(404, "Not registered user");
    }

    @PutMapping("1.0/updateDustType")
    public Response updateDustType(@RequestHeader String userId, @RequestHeader boolean usingMeasuredDust) {
        Optional<User> targetUser = this.userRepository.findById(userId);

        if (targetUser.isPresent()) {
            if (userRepository.updateDustType(userId, usingMeasuredDust) > 0)
                return new Response(200, "Update Success");
            else
                return new Response(500, "Update fail");
        } else
            return new Response(404, "Not registered user");
    }
}
package team.perfect.fresh_air.Controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        } else
            return new Response(404, "Not registered user");
    }
}
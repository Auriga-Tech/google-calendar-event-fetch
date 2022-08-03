package com.aurigait.googleCalendar.controller;

import com.aurigait.googleCalendar.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/oauth", method = RequestMethod.GET)
    public RedirectView googleConnectionStatus(HttpServletRequest request) throws Exception {
        return new RedirectView(authService.authorize());
    }
    @RequestMapping(value = "/oauth", method = RequestMethod.GET, params = "code")
    public String oauth2Callback(@RequestParam(value = "code") String code) {
        return authService.extractAccessToken(code);
    }
    @RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
    public ResponseEntity<String> refreshToken() {
        String response = authService.getNewAccessTokenUsingRefreshToken();
        return new ResponseEntity<>("Refreshed", HttpStatus.OK);
    }
}

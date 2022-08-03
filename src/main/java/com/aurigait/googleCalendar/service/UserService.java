package com.aurigait.googleCalendar.service;


import com.aurigait.googleCalendar.entity.User;
import com.aurigait.googleCalendar.repository.UserRepository;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.oauth2.model.Userinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Save user information in DB
     * @param tokenResponse
     * @param userInfo
     * @return
     */
    public User registerUser(TokenResponse tokenResponse, Userinfo userInfo) {
        try {
            User user  = userRepository.getUserByEmail(userInfo.getEmail());
            if(user == null) {
                user= new User();
            }
            user.setAccessToken(tokenResponse.getAccessToken());
            user.setRefreshToken(tokenResponse.getRefreshToken());
            user.setEmail(userInfo.getEmail());
            user.setName(userInfo.getName());
            user.setPhoto(userInfo.getPicture());
            userRepository.save(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getCurrentUser() {
        try {
            return userRepository.findTopByOrderByIdDesc();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User saveUser(User user){
        return userRepository.save(user);
    }

}

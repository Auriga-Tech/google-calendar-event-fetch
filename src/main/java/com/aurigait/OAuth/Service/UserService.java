package com.aurigait.OAuth.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aurigait.OAuth.Entity.User;
import com.aurigait.OAuth.Repository.UserRepository;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.oauth2.model.Userinfo;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	public User saveUserData(TokenResponse tokenResponse, Userinfo userInfo) {
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
			
						
			return null;
		} catch (Exception e) {
			System.out
				.println(e);
			return null;
		}
	}
	
	public User getLastSavedToken() {
		try {
			
			return userRepository.findTopByOrderByIdDesc();
			
		} catch (Exception e) {
			System.out
				.println(e);
			return null;
		}
	}
	
	public List<User> getAllUser() {
		try {
			return userRepository.findAll();
		} catch(Exception e) {
			return null;
		}
	}
	
	public User saveUpdatedData(User user){
		return userRepository.save(user);
	}
	
}

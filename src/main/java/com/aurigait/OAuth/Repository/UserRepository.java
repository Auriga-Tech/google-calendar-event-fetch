package com.aurigait.OAuth.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aurigait.OAuth.Entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

//	@Query(value = "SELECT token FROM token ORDER BY id DESC")
	public User findTopByOrderByIdDesc();
	public User getUserByEmail(String email);
	
	public List<User> findAll();
}

package com.aurigait.googleCalendar.repository;

import com.aurigait.googleCalendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findTopByOrderByIdDesc();
    public User getUserByEmail(String email);
}

package com.yash.repository;

import java.util.List;
import java.util.Optional;

import com.yash.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface UserRepository extends JpaRepository<User, Long>{

    @Query(value = "Select * from users", nativeQuery = true)
    List<User> findAllAdmin();

    @Query(value = "SELECT * FROM users WHERE username = :username", nativeQuery = true)
    User findByUsername(@Param("username") String username);

    User findUserByMobileNumber(String mobileNumber);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByMobileNumber(String mobileNumber);
}

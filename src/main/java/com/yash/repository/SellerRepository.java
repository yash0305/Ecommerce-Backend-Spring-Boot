package com.yash.repository;

import com.yash.dto.SellerInfoDTO;
import com.yash.entity.SellerProfile;
import org.hibernate.query.NativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SellerRepository extends JpaRepository<SellerProfile ,Long> {
//
//    @Query(value = """
//SELECT
//  u.username AS username,
//  u.mobile_number AS mobileNumber,
//  u.email AS email,
//  s.status AS status,
//  s.email_verified AS emailVerified,
//  s.mobile_verified AS mobileVerified
//FROM users u
//JOIN seller_profile s
//  ON u.id = s.user_id
//WHERE u.role = 'SELLER'
//""", nativeQuery = true)
//    List<SellerInfoDTO> fetchSellerInfo();

    @Query("SELECT new com.yash.dto.SellerInfoDTO(s.id, u.username, u.mobileNumber, u.email, " +
            "s.status, s.emailVerified, s.mobileVerified) " +
            "FROM User u JOIN u.sellerProfile s WHERE u.role = 'SELLER'")
    List<SellerInfoDTO> fetchSellerInfo();

}

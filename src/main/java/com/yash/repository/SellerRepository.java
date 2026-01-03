package com.yash.repository;

import com.yash.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<SellerProfile ,Long> {
}

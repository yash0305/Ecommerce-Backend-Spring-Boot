package com.yash.service;

import com.yash.entity.SellerProfile;
import com.yash.repository.SellerProfileRepository;
import org.springframework.stereotype.Service;


@Service
public class SellerProfileService {

    private final SellerProfileRepository sellerProfileRepository;

    public SellerProfileService(SellerProfileRepository sellerProfileRepository) {
        this.sellerProfileRepository = sellerProfileRepository;
    }

    public SellerProfile saveSeller(SellerProfile seller) {
        return sellerProfileRepository.save(seller);
    }

}

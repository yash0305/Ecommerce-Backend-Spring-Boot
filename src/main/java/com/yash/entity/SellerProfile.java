package com.yash.entity;


import com.yash.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seller_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerProfile {

    @Id
    private Long id; // same as User ID

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @OneToMany(
            mappedBy = "seller",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Products> products = new ArrayList<>();

    @Column(name = "email_verified",nullable = false)
    private boolean emailVerified = false;

    @Column(name="mobile_verified", nullable = false)
    private boolean mobileVerified = false;

}

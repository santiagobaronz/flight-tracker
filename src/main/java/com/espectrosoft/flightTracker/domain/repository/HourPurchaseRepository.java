package com.espectrosoft.flightTracker.domain.repository;

import com.espectrosoft.flightTracker.domain.model.Aircraft;
import com.espectrosoft.flightTracker.domain.model.Academy;
import com.espectrosoft.flightTracker.domain.model.HourPurchase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HourPurchaseRepository extends JpaRepository<HourPurchase, Long> {
    boolean existsByReceiptNumberAndAircraft(String receiptNumber, Aircraft aircraft);
    List<HourPurchase> findByAcademyAndReceiptNumber(Academy academy, String receiptNumber);
}

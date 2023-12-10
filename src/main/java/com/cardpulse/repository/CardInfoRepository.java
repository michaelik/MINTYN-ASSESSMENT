package com.cardpulse.repository;

import com.cardpulse.model.CardInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {
    CardInfo findByBin(Integer binNumber);
    @Query("SELECT c FROM CardInfo c ORDER BY c.id")
    List<CardInfo> findCardInfoByPagination(Pageable pageable);
}

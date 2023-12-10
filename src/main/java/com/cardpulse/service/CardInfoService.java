package com.cardpulse.service;

import com.cardpulse.payload.response.StatsResponse;
import com.cardpulse.payload.response.VerifyBinResponse;
import org.springframework.stereotype.Component;

@Component
public interface CardInfoService {

    VerifyBinResponse VerifyBin(Integer issuerNumber);
    StatsResponse getStats(int start, int limit);


}

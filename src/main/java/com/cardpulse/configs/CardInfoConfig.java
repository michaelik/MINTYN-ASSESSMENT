package com.cardpulse.configs;

import com.cardpulse.model.CardInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CardInfoConfig {

    @Bean
    public CardInfo cardInfo() {
        return new CardInfo();
    }
}

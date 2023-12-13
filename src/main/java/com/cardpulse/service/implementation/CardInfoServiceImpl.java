package com.cardpulse.service.implementation;

import com.cardpulse.exception.BinLookupException;
import com.cardpulse.exception.InvalidLimitException;
import com.cardpulse.model.CardInfo;
import com.cardpulse.payload.response.BinLookupResponse;
import com.cardpulse.payload.response.PayloadResponse;
import com.cardpulse.payload.response.StatsResponse;
import com.cardpulse.payload.response.VerifyBinResponse;
import com.cardpulse.repository.CardInfoRepository;
import com.cardpulse.service.CardInfoService;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@Data
@NoArgsConstructor
@Slf4j
public class CardInfoServiceImpl implements CardInfoService {

    static final int HITS_INCREMENT = 1;
    @Autowired
    WebClient.Builder webClient;
    @Autowired
    CardInfoRepository cardInfoRepository;
    @Autowired
    CardInfo cardInfo;

    @Override
    public VerifyBinResponse verifyBin(Integer bin) {
        CardInfo foundBinInfo = cardInfoRepository
                .findByBin(bin);
        if (Objects.nonNull(foundBinInfo)) {
            foundBinInfo.setNumberOfHits(foundBinInfo.getNumberOfHits() + HITS_INCREMENT);
            cardInfoRepository.save(foundBinInfo);
            return buildVerifyBinResponse(foundBinInfo);
        }
        BinLookupResponse binLookupResponse = getBinData(bin);
        // Save the data only
        CardInfo newCardInfo = CardInfo.builder()
                //user request
                .bin(bin)
                .scheme(binLookupResponse.getScheme())
                .type(binLookupResponse.getType())
                .bank(binLookupResponse.getBank().getName())
                // static constant
                .numberOfHits(HITS_INCREMENT)
                .build();
        cardInfoRepository.save(newCardInfo);
        return buildVerifyBinResponse(newCardInfo);
    }

    @Override
    public StatsResponse getStats(int start, int limit) {
        long totalRecords = cardInfoRepository.count();

        if (start > totalRecords) throw new InvalidLimitException(
                    "Start position is beyond the total number of records"
            );

        Pageable pageable = PageRequest.of(start - 1, limit);
        List<CardInfo> cardStatsEntities = cardInfoRepository
                .findCardInfoByPagination(pageable);

        Map<String, String> payload = cardStatsEntities.stream()
                .filter(cardInfo -> isValidInteger(String.valueOf(cardInfo.getBin())) &&
                        isValidInteger(String.valueOf(cardInfo.getNumberOfHits())))
                .collect(Collectors.toMap(
                        cardInfo -> String.valueOf(cardInfo.getBin()),
                        cardInfo -> String.valueOf(cardInfo.getNumberOfHits()),
                        (existing, replacement) -> existing
                ));

        return buildStatsResponse(start, limit, payload);
    }

    private VerifyBinResponse buildVerifyBinResponse(CardInfo cardInfo){
        return new VerifyBinResponse(
                true,
                new PayloadResponse(
                        cardInfo.getScheme(),
                        cardInfo.getType(),
                        cardInfo.getBank()
                ));
    }

    private boolean isValidInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
}

    private static StatsResponse buildStatsResponse(int start,
                                                    int limit,
                                                    Map<String, String> payload){
        return new StatsResponse(
                true,
                start,
                limit,
                payload.size(),
                payload
        );
    }

    public BinLookupResponse getBinData(Integer bin) {
        HttpClient httpClient = HttpClient
                .create()
                .resolver(DefaultAddressResolverGroup.INSTANCE);
        try {
            return webClient
                    .baseUrl("https://lookup.binlist.net")
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build()
                    .method(HttpMethod.GET)
                    .uri("/" + bin)
                    .header("Accept-Version", "3")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError,
                            clientResponse -> {
                                throw new BinLookupException(
                                        "Client error while looking up Bank Identification Number [%s]. Status code: %d"
                                                .formatted(bin, clientResponse.rawStatusCode()));
                            })
                    .onStatus(HttpStatus::is5xxServerError,
                            clientResponse -> {
                                throw new BinLookupException(
                                        "Server error while looking up Bank Identification Number [%s]. Status code: %d"
                                                .formatted(bin, clientResponse.rawStatusCode()));
                            })
                    .bodyToMono(BinLookupResponse.class)
                    .retry(2)
                    .block();
        } catch (WebClientResponseException e) {
            throw new BinLookupException(
                    "Error while looking up Bank Identification Number [%s]. Status code: %d"
                            .formatted(bin, e.getRawStatusCode()));
        } catch (WebClientRequestException e) {
            if (e.getCause() instanceof UnknownHostException) {
                throw new BinLookupException(
                        "Failed to lookup Bank Identification Number [%s]. Host resolution error."
                                .formatted(bin)
                );
            } else {
                throw new BinLookupException(
                        "Error while looking up Bank Identification Number [%s]"
                                .formatted(bin)
                );
            }
        }
    }
}

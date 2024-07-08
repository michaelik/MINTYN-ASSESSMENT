package com.cardpulse.service;

import com.cardpulse.data.BankInfo;
import com.cardpulse.exception.InvalidLimitException;
import com.cardpulse.model.CardInfo;
import com.cardpulse.payload.response.BinLookupResponse;
import com.cardpulse.payload.response.StatsResponse;
import com.cardpulse.payload.response.VerifyBinResponse;
import com.cardpulse.repository.CardInfoRepository;
import com.cardpulse.service.implementation.CardInfoServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.resolver.DefaultAddressResolverGroup;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardInfoServiceImplTest {

    @Mock
    private CardInfoRepository cardInfoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private CardInfoServiceImpl cardInfoService;

    public MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();

        // Initialize WebClient with the mock server URL
        webClientBuilder = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)))
                .baseUrl(baseUrl);

        cardInfoService.setWebClient(webClientBuilder);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void verifyBin_existingBin_shouldReturnVerifyBinResponse() {
        // Given(Arrange)
        int bin = 12345678;
        CardInfo existingCardInfo = CardInfo.builder()
                .bin(bin)
                .scheme("visa")
                .type("debit")
                .bank("Test Bank")
                .numberOfHits(1)
                .build();
        when(cardInfoRepository.findByBin(bin)).thenReturn(existingCardInfo);

        // When(Act)
        VerifyBinResponse verifyBinResponse = assertDoesNotThrow(() -> cardInfoService.verifyBin(bin));

        // Then(Assert)
        assertNotNull(verifyBinResponse);
        assertTrue(verifyBinResponse.isSuccess());
        assertNotNull(verifyBinResponse.getPayload());
        assertEquals(existingCardInfo.getScheme(), verifyBinResponse.getPayload().getScheme());
        assertEquals(existingCardInfo.getType(), verifyBinResponse.getPayload().getType());
        assertEquals(existingCardInfo.getBank(), verifyBinResponse.getPayload().getBank());
    }

    @Test
    void verifyBin_nonExistingBin_shouldReturnVerifyBinResponse() throws JsonProcessingException {
        // Given(Arrange)
        BankInfo name = new BankInfo();
        name.setName("Jyske Bank");
        BinLookupResponse binLookupResponse = new BinLookupResponse();
        binLookupResponse.setScheme("visa");
        binLookupResponse.setType("debit");
        binLookupResponse.setBank(name);

        // Convert binLookupResponse to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = objectMapper.writeValueAsString(binLookupResponse);

        // Set up a mock response for the given request
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody));

        int bin = 45717360;
        CardInfo.builder()
                .bin(bin)
                .scheme(binLookupResponse.getScheme())
                .type(binLookupResponse.getType())
                .bank(binLookupResponse.getBank().toString())
                .numberOfHits(1)
                .build();

        when(cardInfoRepository.findByBin(bin)).thenReturn(null);

        // When(Act)
        VerifyBinResponse verifyBinResponse = assertDoesNotThrow(() -> cardInfoService.verifyBin(bin));
        assertDoesNotThrow(() -> cardInfoService.getBinData(bin));

        // Then(Assert)
        ArgumentCaptor<CardInfo> cardInfoCaptor = ArgumentCaptor.forClass(CardInfo.class);
        verify(cardInfoRepository, times(1)).save(cardInfoCaptor.capture());
        CardInfo capturedCardInfo = cardInfoCaptor.getValue();
        assertTrue(verifyBinResponse.isSuccess());
        assertEquals(capturedCardInfo.getScheme(), verifyBinResponse.getPayload().getScheme());
        assertEquals(capturedCardInfo.getType(), verifyBinResponse.getPayload().getType());
        assertEquals(capturedCardInfo.getBank(), verifyBinResponse.getPayload().getBank());
    }

    @Test
    void testGetStats_Successful() {
        // Given(Arrange)
        int start = 1;
        int limit = 2;
        long totalRecords = 2L;

        CardInfo cardInfo1 = new CardInfo(1L, 45717360, "visa", "debit", "Jyske Bank", 1);
        CardInfo cardInfo2 = new CardInfo(2L, 45777360,"MasterCard", "credit", "First Bank", 1);

        List<CardInfo> cardInfoList = Arrays.asList(cardInfo1, cardInfo2);
        Pageable pageable = PageRequest.of(start - 1, limit);

        when(cardInfoRepository.count()).thenReturn(totalRecords);
        when(cardInfoRepository.findCardInfoByPagination(pageable)).thenReturn(cardInfoList);

        // When(Act)
        StatsResponse statsResponse = cardInfoService.getStats(start, limit);

        // Then(Assert)
        assertTrue(statsResponse.isSuccess());
        assertEquals(start, statsResponse.getStart());
        assertEquals(limit, statsResponse.getLimit());
        assertEquals(cardInfoList.size(), statsResponse.getSize());
        assertEquals(buildPayload(cardInfoList), statsResponse.getPayload());

        // Verify that repository methods were called with the correct parameters
        Mockito.verify(cardInfoRepository).count();
        Mockito.verify(cardInfoRepository).findCardInfoByPagination(Mockito.argThat(
                arg -> arg.getPageNumber() == start - 1 && arg.getPageSize() == limit
           )
        );
    }

    private Map<String, String> buildPayload(List<CardInfo> cardInfoList) {
        return cardInfoList.stream()
                .filter(cardInfo -> isValidInteger(String.valueOf(cardInfo.getBin())) &&
                        isValidInteger(String.valueOf(cardInfo.getNumberOfHits())))
                .collect(Collectors.toMap(
                        cardInfo -> String.valueOf(cardInfo.getBin()),
                        cardInfo -> String.valueOf(cardInfo.getNumberOfHits()),
                        (existing, replacement) -> existing
                ));
    }

    private boolean isValidInteger(String s) {
        return s != null;
    }

    @Test
    void testGetStats_StartPositionBeyondTotalRecords_ThrowsException() {
        // Given(Arrange)
        int start = 3;
        int limit = 2;
        long totalRecords = 2L;

        when(cardInfoRepository.count()).thenReturn(totalRecords);

        /// When(Act)
        InvalidLimitException exception = assertThrows(InvalidLimitException.class,
                () -> cardInfoService.getStats(start, limit));

        // Then(Assert)
        assertEquals("Start position is beyond the total number of records", exception.getMessage());
    }

    @Test
    void testGetBinData() throws JsonProcessingException {
        // Given(Arrange)
        BankInfo name = new BankInfo();
        name.setName("Jyske Bank");
        BinLookupResponse binLookupResponse = new BinLookupResponse();
        binLookupResponse.setScheme("visa");
        binLookupResponse.setType("debit");
        binLookupResponse.setBank(name);

        // Convert binLookupResponse to JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        String responseBody = objectMapper.writeValueAsString(binLookupResponse);

        // Set up a mock response for the given request
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody));

        // When(Act)
        BinLookupResponse actualResponse = cardInfoService.getBinData(45717360);

        // Then(Assert)
        assertEquals(binLookupResponse.getScheme(), actualResponse.getScheme());
        assertEquals(binLookupResponse.getType(), actualResponse.getType());
        // assertEquals(binLookupResponse.getBank(), actualResponse.getBank());
    }
}

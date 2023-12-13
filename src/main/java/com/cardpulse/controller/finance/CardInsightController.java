package com.cardpulse.controller.finance;

import com.cardpulse.payload.response.StatsResponse;
import com.cardpulse.payload.response.UserDetailResponse;
import com.cardpulse.payload.response.VerifyBinResponse;
import com.cardpulse.service.CardInfoService;
import com.cardpulse.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/card-scheme")
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardInsightController {

    @Autowired
    UserService userService;
    @Autowired
    CardInfoService cardInfoService;

    @GetMapping(path = "/user-detail/{id}",
            consumes = {MediaType.ALL_VALUE},
            produces = "application/json")
    public ResponseEntity<UserDetailResponse> getUserDetail(
            @PathVariable("id") Integer userId)
    {
        return new ResponseEntity<>(
                userService.getUserDetail(userId),
                HttpStatus.OK);
    }

    @GetMapping(path = "/verify/{bin}",
            consumes = {MediaType.ALL_VALUE},
            produces = "application/json")
    public ResponseEntity<VerifyBinResponse> VerifyBin(
            @PathVariable Integer bin
    )
    {
        return new ResponseEntity<>(
                cardInfoService.verifyBin(bin),
                HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getCardStats(
            @RequestParam
            @Min(value = 1, message = "Please enter a valid positive integer")
            int start,
            @RequestParam
            @Min(value = 1, message = "Please enter a valid positive integer")
            int limit
    )
    {
        return new ResponseEntity<>(
                cardInfoService.getStats(start, limit),
                HttpStatus.OK);
    }

}

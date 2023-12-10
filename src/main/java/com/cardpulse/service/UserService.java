package com.cardpulse.service;

import com.cardpulse.payload.request.SignInRequest;
import com.cardpulse.payload.request.SignUpRequest;
import com.cardpulse.payload.response.SignInResponse;
import com.cardpulse.payload.response.UserDetailResponse;
import org.springframework.stereotype.Component;

@Component
public interface UserService {
    void signUp(SignUpRequest request);

    SignInResponse signIn(SignInRequest request);

    UserDetailResponse getUserDetail(Integer id);
}

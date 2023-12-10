package com.cardpulse.service.implementation;

import com.cardpulse.enums.Gender;
import com.cardpulse.exception.DuplicateResourceException;
import com.cardpulse.exception.ResourceNotFoundException;
import com.cardpulse.jwt.JWTUtilService;
import com.cardpulse.model.User;
import com.cardpulse.payload.request.SignInRequest;
import com.cardpulse.payload.request.SignUpRequest;
import com.cardpulse.payload.response.SignInResponse;
import com.cardpulse.payload.response.UserDetailResponse;
import com.cardpulse.repository.UserRepository;
import com.cardpulse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTUtilService jwtUtilService;

    @Override
    public void signUp(SignUpRequest request) {
        boolean userEmailExist = userRepository
                .findByEmail(request.getEmail())
                .isPresent();
        if (userEmailExist) throw new DuplicateResourceException("Email Already Taken");
        Gender gender = Gender.valueOf(request.getGender());
        String password = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(password)
                .age(request.getAge())
                .gender(gender)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
    }

    @Override
    public SignInResponse signIn(SignInRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User principal = (User) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String token = jwtUtilService.issueToken(principal.getUsername(), roles);
        return new SignInResponse(
                token,
                principal.getId(),
                principal.getName(),
                principal.getEmail(),
                principal.getGender(),
                principal.getAge(),
                roles,
                principal.getUsername()
        );
    }

    @Override
    public UserDetailResponse getUserDetail(Integer id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "User with id [%s] not Found".formatted(id)
                ));
        return UserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .gender(user.getGender())
                .build();
    }

}

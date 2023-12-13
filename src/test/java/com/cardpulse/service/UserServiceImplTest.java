package com.cardpulse.service;

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
import com.cardpulse.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTUtilService jwtUtilService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp_Successful() {
        // Given(Arrange)
        String testEmail = "john@example.com";
        doReturn(Optional.empty()).when(userRepository).findByEmail(testEmail);

        SignUpRequest signUpRequest = new SignUpRequest("John Doe",
                "john@example.com",
                "password",
                20,
                "M");

        String passwordHash = "¢5554ml;f;lsd";
        doReturn(passwordHash).when(passwordEncoder).encode(signUpRequest.getPassword());

        // When(Act)
        assertDoesNotThrow(() -> userService.signUp(signUpRequest));

        // Then(Assert)
        // Verify that save method was called with the expected User object
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());
        // Additional assertions based on the captured User object
        User capturedUser = userCaptor.getValue();
        assertNotNull(capturedUser);
        assertThat(capturedUser.getId()).isNull();
        assertEquals(signUpRequest.getName(), capturedUser.getName());
        assertEquals(signUpRequest.getEmail(), capturedUser.getEmail());
        assertEquals(passwordHash, capturedUser.getPassword());
        assertEquals(signUpRequest.getAge(), capturedUser.getAge());
    }



    @Test
    void signUp_DuplicateEmail_ThrowsDuplicateResourceException() {
        // Given(Arrange)
        SignUpRequest signUpRequest = new SignUpRequest("John Doe",
                "john@example.com",
                "password",
                20,
                "MALE");
        doReturn(Optional.of(new User()))
                .when(userRepository)
                .findByEmail(signUpRequest.getEmail());

        // When(Act)
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> userService.signUp(signUpRequest)
        );

        // Then(Assert)
        assertEquals("Email Already Taken", exception.getMessage());
    }

    @Test
    void signIn_Successful() {
        // Given(Arrange)
        SignInRequest signInRequest = new SignInRequest(
                "john@example.com",
                "password"
        );

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        "john@example.com",
                        "password")
        )).thenReturn(
                authentication
        );
        String passwordHash = "¢5554ml;f;lsd";
        User user = new User(
                1,
                "John Doe",
                "john@example.com",
                passwordHash,
                20,
                Gender.M,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(authentication.getPrincipal()).thenReturn(user);
        List<String> roles = Collections.singletonList("ROLE_USER");
        when(jwtUtilService.issueToken("john@example.com", roles))
                .thenReturn(
                        "eyJhbGciOiJIUzI1NiJ9.eyJzY29wZXMiOlsiUk9MRV9VU0VSIl0sInN1YiI6ImpvaG5AZXhhbXBsZS5jb20iLCJpYXQiOjE3MDIyNjI2MDksImV4cCI6MTcwMzU1ODYwOX0.eUNKqg2k4sBdr4wDQnMBwNji2ib7Z-lscuRrgopBH1M"
                );

        // When(Act)
        SignInResponse signInResponse = assertDoesNotThrow(() -> userService.signIn(signInRequest));

        // Then(Assert)
        assertNotNull(signInResponse);
        assertEquals("eyJhbGciOiJIUzI1NiJ9.eyJzY29wZXMiOlsiUk9MRV9VU0VSIl0sInN1YiI6ImpvaG5AZXhhbXBsZS5jb20iLCJpYXQiOjE3MDIyNjI2MDksImV4cCI6MTcwMzU1ODYwOX0.eUNKqg2k4sBdr4wDQnMBwNji2ib7Z-lscuRrgopBH1M",
                signInResponse.getToken());
        assertEquals(1, signInResponse.getId());
        assertEquals("John Doe", signInResponse.getName());
        assertEquals("john@example.com", signInResponse.getEmail());
        assertEquals(Gender.M, signInResponse.getGender());
        assertEquals(20, signInResponse.getAge());
        assertEquals(roles, signInResponse.getRoles());
        assertEquals("john@example.com", signInResponse.getUsername());

        // Verify that authenticationManager.authenticate was called with the expected UsernamePasswordAuthenticationToken
        ArgumentCaptor<UsernamePasswordAuthenticationToken>
                authenticationTokenCaptor = ArgumentCaptor
                .forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authenticationTokenCaptor.capture());

        UsernamePasswordAuthenticationToken capturedAuthenticationToken = authenticationTokenCaptor.getValue();
        assertEquals("john@example.com", capturedAuthenticationToken.getPrincipal());
        assertEquals("password", capturedAuthenticationToken.getCredentials());
    }

    @Test
    void getUserDetail_Successful() {
        // Given(Arrange)
        Integer userId = 1;
        String passwordHash = "¢5554ml;f;lsd";
        User user = new User(
                userId,
                "John Doe",
                "john@example.com",
                passwordHash,
                20, Gender.M,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When(Act)
        UserDetailResponse userDetailResponse = assertDoesNotThrow(() -> userService.getUserDetail(userId));

        // Then(Assert)
        assertNotNull(userDetailResponse);
        assertEquals(userId, userDetailResponse.getId());
        assertEquals("John Doe", userDetailResponse.getName());
        assertEquals("john@example.com", userDetailResponse.getEmail());
        assertEquals(20, userDetailResponse.getAge());
        assertEquals(Gender.M, userDetailResponse.getGender());
    }

    @Test
    void getUserDetail_UserNotFound_ThrowsResourceNotFoundException() {
        // Given(Arrange)
        Integer userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When(Act)
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserDetail(userId)
        );

        // Then(Assert)
        assertEquals("User with id [1] not Found", exception.getMessage());
    }
}

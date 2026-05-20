package com.stackly.auth.controller;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stackly.auth.repository.UserRepository;
import com.stackly.auth.service.EmailService;
import com.stackly.auth.service.IAuthService;
import com.stackly.auth.service.OtpService;
import com.stackly.common.dto.ResetPasswordRequestDTO;
import com.stackly.common.entity.*;
import com.stackly.common.exception.InvalidOtpException;
import com.stackly.common.exception.PasswordMismatchException;
import com.stackly.common.exception.UserNotFoundException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    // ---------------- SIGNUP ----------------

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody com.stackly.common.dto.SignupRequestDTO request) {
        String response = authService.signup(request);

        if (response.equals("OTP_SENT")) {
            return ResponseEntity.ok("OTP sent to your email. Please verify to complete registration.");
        }

        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/signup-verify-otp")
    public ResponseEntity<String> verifySignupOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        String result = authService.verifyAndRegister(email, otp);

        if (result.equals("SUCCESS")) {
            return ResponseEntity.ok("Registration successful! You can now log in.");
        } else if (result.equals("INVALID_OTP")) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        } else {
            return ResponseEntity.badRequest().body("Registration session expired. Please sign up again.");
        }
    }

    // ---------------- LOGIN ----------------

    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String email = request.get("email");
        String password = request.get("password");
        authService.login(email, password, httpRequest, httpResponse);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/dashboard")
                .build();
    }

    // ---------------- GUEST LOGIN (NO DB) ----------------

    @PostMapping("/guest/request-otp")
    public ResponseEntity<String> requestGuestOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String result = authService.requestGuestOtp(email);
        if ("EMAIL_REQUIRED".equals(result)) {
            return ResponseEntity.badRequest().body("Email is required.");
        }
        return ResponseEntity.ok("OTP sent to your email for guest login.");
    }

    @PostMapping("/guest/verify-otp")
    public ResponseEntity<Void> verifyGuestOtp(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        String email = request.get("email");
        String otp = request.get("otp");
        authService.verifyGuestOtp(email, otp, httpRequest, httpResponse);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/dashboard")
                .build();
    }

    // ---------------- FORGOT PASSWORD ----------------

    @PostMapping("/forgotpassword")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String result = authService.forgotPassword(email);
        if ("EMAIL_REQUIRED".equals(result)) {
            return ResponseEntity.badRequest().body("Email is required.");
        }
        return ResponseEntity.ok("OTP sent to your email.");
    }

    // ---------------- VERIFY OTP ----------------

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String result = authService.verifyOtp(email, otp);
        if ("EMAIL_OTP_REQUIRED".equals(result)) {
            return ResponseEntity.badRequest().body("Email and OTP are required.");
        }
        return ResponseEntity.ok("OTP verified successfully.");
    }

    // ---------------- RESET PASSWORD ----------------

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password reset successful.");
    }
 // ---------------- RESEND OTP ----------------

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String result = authService.resendOTP(email);
        if ("EMAIL_REQUIRED".equals(result)) {
            return ResponseEntity.badRequest().body("Email is required.");
        }
        if ("OTP_NOT_EXPIRED".equals(result)) {
            return ResponseEntity.badRequest()
                    .body("OTP already sent. Please wait until it expires.");
        }
        return ResponseEntity.ok("New OTP sent successfully.");
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.currentUser(authentication));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletRequest request,
            HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            authService.logout(session);
        }

        Cookie cookie = new Cookie("MICROSAAS_SESSION", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        Map<String, String> body = new java.util.LinkedHashMap<>();
        body.put("message", "Logged out successfully");
        return ResponseEntity.ok(body);
    }
    
}
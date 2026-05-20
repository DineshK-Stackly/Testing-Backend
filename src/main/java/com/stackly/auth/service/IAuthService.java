package com.stackly.auth.service;

import java.util.Map;

import org.springframework.security.core.Authentication;

import com.stackly.common.dto.ResetPasswordRequestDTO;
import com.stackly.common.dto.SignupRequestDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public interface IAuthService {
	String signup(SignupRequestDTO request);
    String verifyAndRegister(String email, String otp);
    void login(String email, String password, HttpServletRequest httpRequest, HttpServletResponse httpResponse);
    String requestGuestOtp(String email);
    void verifyGuestOtp(String email, String otp, HttpServletRequest httpRequest, HttpServletResponse httpResponse);
    String forgotPassword(String email);
    String verifyOtp(String email, String otp);
    String resetPassword(ResetPasswordRequestDTO request);
    String resendOTP(String email);
    Map<String, String> currentUser(Authentication authentication);
    String logout(HttpSession session);
}
package com.stackly.auth.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import com.stackly.auth.repository.UserRepository;
import com.stackly.common.dto.ResetPasswordRequestDTO;
import com.stackly.common.dto.SignupRequestDTO;
import com.stackly.common.entity.*;
import com.stackly.common.exception.EmailAlreadyExistException;
import com.stackly.common.exception.InvalidOtpException;
import com.stackly.common.exception.PasswordMismatchException;
import com.stackly.common.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    @Override
    public String signup(SignupRequestDTO request) {
        // 1. Check if Passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match..!");
        }

        // 2. Check if Email or Username already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException(" Email is already taken!");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new EmailAlreadyExistException(" Username is already taken!");
        }

        // 2. Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // 3. Save User Data + OTP to Redis (Using your updated OtpService)
        otpService.saveSignupData(request.getEmail(), request, otp);

        // 4. Send the Email
        emailService.sendOtp(request.getEmail(), otp);

        return "OTP_SENT";
    }

    @Override
    public String verifyAndRegister(String email, String otp) {
        // 1. Verify OTP using your existing method
        if (!otpService.verifyOtp(email, otp)) {
            return "INVALID_OTP";
        }

        // 2. Retrieve the user details from Redis
        SignupRequestDTO cachedData = otpService.getSignupData(email);
        if (cachedData == null) {
            return "SESSION_EXPIRED";
        }

        // 3. OTP is valid! Now save the user to MySQL
        User user = User.builder()
                .username(cachedData.getUsername())
                .email(cachedData.getEmail())
                .password(passwordEncoder.encode(cachedData.getPassword()))
                .build();

        userRepository.save(user);

        // 4. Cleanup Redis (Remove OTP and Data)
        otpService.clearSignupData(email);

        return "SUCCESS";
    }

	@Override
	public String requestGuestOtp(String email) {
		if (email == null || email.isBlank()) {
            return "EMAIL_REQUIRED";
        }

        String otp = emailService.generateOTP();
        otpService.storeOtp(email, otp);
        emailService.sendOtp(email, otp);
		return "OTP_SENT";
	}

	@Override
	public void verifyGuestOtp(String email, String otp, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		if (email == null || email.isBlank() || otp == null || otp.isBlank()) {
            throw new InvalidOtpException("Email and OTP are required.");
        }
		
		boolean valid = otpService.verifyOtp(email, otp);
        if (!valid) {
            throw new InvalidOtpException("Invalid or Expired OTP...");
        }

        Authentication guestAuth = new UsernamePasswordAuthenticationToken(
                email,
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_GUEST")));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(guestAuth);
        SecurityContextHolder.setContext(context);

        new HttpSessionSecurityContextRepository().saveContext(context, httpRequest, httpResponse);
        otpService.clearOtp(email);
	}

	@Override
	public String forgotPassword(String email) {
        if (email == null || email.isBlank()) {
            return "EMAIL_REQUIRED";
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not found With this Email"));

        if (user == null) {
            return "USER_NOT_FOUND";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        otpService.storeOtp(email, otp);
        emailService.sendOtp(email, otp);

        return "OTP_SENT";
	}

	@Override
	public String verifyOtp(String email, String otp) {
        if (email == null || email.isBlank() || otp == null || otp.isBlank()) {
            return "EMAIL_OTP_REQUIRED";
        }

        boolean valid = otpService.verifyOtp(email, otp);
        if (!valid) {
            throw new InvalidOtpException("Invalid or Expired OTP...");
        }

        return "OTP_VERIFIED";
	}

	@Override
	public String resetPassword(ResetPasswordRequestDTO request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Password do not Match");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User Not Found This email"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        otpService.clearOtp(request.getEmail());

        return "PASSWORD_RESET_SUCCESS";
	}

	@Override
	public String resendOTP(String email) {
        if (email == null || email.isBlank()) {
            return "EMAIL_REQUIRED";
        }

        if (userRepository.findByEmail(email).isEmpty()) {
            throw new UserNotFoundException("User Not found With this Email");
        }

        boolean expired = otpService.isOtpExpired(email);
        if (!expired) {
            return "OTP_NOT_EXPIRED";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        otpService.storeOtp(email, otp);
        emailService.sendOtp(email, otp);
        return "OTP_RESENT";
	}

	@Override
	public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return "LOGOUT_SUCCESS";
	}

    @Override
    public void login(String email, String password, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        new HttpSessionSecurityContextRepository().saveContext(context, httpRequest, httpResponse);
    }

    @Override
    public Map<String, String> currentUser(Authentication authentication) {
        Map<String, String> response = new LinkedHashMap<>();
        response.put("email", authentication.getName());
        response.put("role", authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse("ROLE_USER"));
        response.put("authenticated", "true");
        return response;
    }
}


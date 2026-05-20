package com.stackly.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.stackly.common.dto.SignupRequestDTO;

import tools.jackson.databind.ObjectMapper;

@Service
public class OtpService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    
    private static final int OTP_EXPIRY = 300;

    public void storeOtp(String email,String otp) {

       // String otp1 = String.format("%06d", new Random().nextInt(999999));

        redisTemplate.opsForValue().set(
                "OTP:" + email,
                otp,
                OTP_EXPIRY,
                TimeUnit.SECONDS
        );

    }
    public void saveSignupData(String email, SignupRequestDTO request, String otp) {
        try {
            // 1. Convert Java Object to JSON String
            String jsonRequest = objectMapper.writeValueAsString(request);

            // 2. Store the OTP (using your existing method)
            storeOtp(email, otp);

            // 3. Store the User Data with "DATA:" prefix
            redisTemplate.opsForValue().set(
                "DATA:" + email, 
                jsonRequest, 
                OTP_EXPIRY, 
                TimeUnit.SECONDS
            );
        } catch (Exception e) {
            // Log the error and throw a custom exception if needed
            throw new RuntimeException("Could not save signup data to Redis", e);
        }
    }
    public SignupRequestDTO getSignupData(String email) {
        try {
            String jsonRequest = redisTemplate.opsForValue().get("DATA:" + email);
            if (jsonRequest == null) {
                return null;
            }
            // Convert JSON String back to SignupRequest Object
            return objectMapper.readValue(jsonRequest, SignupRequestDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not read signup data from Redis", e);
        }
    }

    public boolean verifyOtp(String email, String otp) {

        String storedOtp = redisTemplate.opsForValue().get("OTP:" + email);

        if (storedOtp == null) {
            return false;
        }

        return storedOtp.equals(otp);
    } 

    public void clearSignupData(String email) {
        clearOtp(email); // Clears "OTP:email"
        redisTemplate.delete("DATA:" + email); // Clears "DATA:email"
    }
    
    public void clearOtp(String email) {
        redisTemplate.delete("OTP:" + email);
    }

	public boolean isOtpVerified(String email) {
		String verified = redisTemplate.opsForValue().get("OTP_VERIFIED:" + email);

        return verified != null && verified.equals("true");		
        }
	public boolean isOtpExpired(String email) {

	    String storedOtp = redisTemplate.opsForValue().get("OTP:" + email);

	    return storedOtp == null;
	}
	
	
}
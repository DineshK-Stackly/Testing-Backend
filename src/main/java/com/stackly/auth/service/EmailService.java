package com.stackly.auth.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${BREVO_API_KEY}")
    private String apiKey;

    public String generateOTP() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP is: " + otp + "\nValid for 5 minutes.");

        mailSender.send(message);
    }
    
    public void sendEmail(String toEmail, String otp) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        String body = """
        {
          "sender": {
            "name": "Stackly",
            "email": "your_verified_email@gmail.com"
          },
          "to": [
            {
              "email": "%s"
            }
          ],
          "subject": "OTP Verification",
          "htmlContent": "<h3>Your OTP is: %s Valid for 5 minutes.</h3>"
        }
        """.formatted(toEmail, otp);

        HttpEntity<String> request =
                new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }
}

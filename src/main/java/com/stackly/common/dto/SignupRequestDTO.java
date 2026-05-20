package com.stackly.common.dto;

import lombok.Data;

@Data
public class SignupRequestDTO {

	private String username;
	private String email;
	private String password;
	private String confirmPassword;
}

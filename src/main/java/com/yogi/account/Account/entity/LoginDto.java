package com.yogi.account.Account.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDto {
	private String username;
	private String password;
}

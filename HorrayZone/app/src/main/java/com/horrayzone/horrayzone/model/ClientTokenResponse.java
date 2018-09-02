package com.horrayzone.horrayzone.model;

import com.google.gson.annotations.SerializedName;

public class ClientTokenResponse {
	@SerializedName("status")
	private String status;
	@SerializedName("client_token")
	private String token;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status= status;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	

}

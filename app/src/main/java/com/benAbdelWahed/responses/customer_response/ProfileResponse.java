package com.benAbdelWahed.responses.customer_response;

import javax.annotation.Generated;

import com.benAbdelWahed.responses.haraj_responses.Customer;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class ProfileResponse{

	@SerializedName("data")
	private Customer data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public Customer getData() {
		return data;
	}

	public void setData(Customer data) {
		this.data = data;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}
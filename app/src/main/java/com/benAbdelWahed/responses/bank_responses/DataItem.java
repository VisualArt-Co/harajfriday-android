package com.benAbdelWahed.responses.bank_responses;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class DataItem{

	@SerializedName("name")
	private String name;

	@SerializedName("accountNumber")
	private String accountNumber;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setAccountNumber(String accountNumber){
		this.accountNumber = accountNumber;
	}

	public String getAccountNumber(){
		return accountNumber;
	}
}
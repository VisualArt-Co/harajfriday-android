package com.benAbdelWahed.responses.haraj_responses;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("com.robohorse.robopojogenerator")
public class AllRatesItem implements Serializable {

	@SerializedName("rate_owner")
	private Customer rateOwner;

	@SerializedName("rate")
	private String rate;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("comment")
	private String comment;

	@SerializedName("id")
	private int id;

	public void setRateOwner(Customer rateOwner){
		this.rateOwner = rateOwner;
	}

	public Customer getRateOwner(){
		return rateOwner;
	}

	public void setRate(String rate){
		this.rate = rate;
	}

	public String getRate(){
		return rate;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setComment(String comment){
		this.comment = comment;
	}

	public String getComment(){
		return comment;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}
package com.benAbdelWahed.responses.mazads_response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("com.robohorse.robopojogenerator")
public class User implements Serializable {

	@SerializedName("image")
	private String image;

	@SerializedName("social_id")
	private Object socialId;

	@SerializedName("full_name")
	private String fullName;

	@SerializedName("lng")
	private Object lng;

	@SerializedName("phone")
	private String phone;

	@SerializedName("id")
	private int id;

	@SerializedName("state_id")
	private String stateId;

	@SerializedName("email")
	private String email;

	@SerializedName("lat")
	private Object lat;

	@SerializedName("city_id")
	private String cityId;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setSocialId(Object socialId){
		this.socialId = socialId;
	}

	public Object getSocialId(){
		return socialId;
	}

	public void setFullName(String fullName){
		this.fullName = fullName;
	}

	public String getFullName(){
		return fullName;
	}

	public void setLng(Object lng){
		this.lng = lng;
	}

	public Object getLng(){
		return lng;
	}

	public void setPhone(String phone){
		this.phone = phone;
	}

	public String getPhone(){
		return phone;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setStateId(String stateId){
		this.stateId = stateId;
	}

	public String getStateId(){
		return stateId;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	public void setLat(Object lat){
		this.lat = lat;
	}

	public Object getLat(){
		return lat;
	}

	public void setCityId(String cityId){
		this.cityId = cityId;
	}

	public String getCityId(){
		return cityId;
	}
}
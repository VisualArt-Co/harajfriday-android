package com.benAbdelWahed.responses.notification_responses;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class NotificationItem {

	@SerializedName("mazad_id")
	private String mazadId;

	@SerializedName("product_id")
	private String productId;

	@SerializedName("notificationType")
	private String notificationType;

	@SerializedName("title")
	private String title;

	@SerializedName("message")
	private String message;
	@SerializedName("created_at")
	private String createdAt;

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public void setMazadId(String mazadId){
		this.mazadId = mazadId;
	}

	public String getMazadId(){
		return mazadId;
	}

	public void setProductId(String productId){
		this.productId = productId;
	}

	public String getProductId(){
		return productId;
	}

	public void setNotificationType(String notificationType){
		this.notificationType = notificationType;
	}

	public String getNotificationType(){
		return notificationType;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}
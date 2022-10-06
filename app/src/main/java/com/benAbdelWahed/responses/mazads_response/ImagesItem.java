package com.benAbdelWahed.responses.mazads_response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Generated("com.robohorse.robopojogenerator")
public class ImagesItem implements Serializable {

	@SerializedName("mazad_id")
	private String mazadId;

	@SerializedName("image")
	private String image;

	@SerializedName("id")
	private int id;

	public void setMazadId(String mazadId){
		this.mazadId = mazadId;
	}

	public String getMazadId(){
		return mazadId;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}
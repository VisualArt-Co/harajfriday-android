package com.benAbdelWahed.responses.categories_response;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class SubCategory implements Serializable {

	@SerializedName("image")
	private String image;

	@SerializedName("category_id")
	private String categoryId;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("subsubs")
	private List<SubSubCategory> subsubs;

	@SerializedName("translations")
	private List<Translation> translations;

	public List<Translation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<Translation> translations) {
		this.translations = translations;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setCategoryId(String categoryId){
		this.categoryId = categoryId;
	}

	public String getCategoryId(){
		return categoryId;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setSubsubs(List<SubSubCategory> subsubs){
		this.subsubs = subsubs;
	}

	public List<SubSubCategory> getSubsubs(){
		return subsubs;
	}
}
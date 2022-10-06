package com.benAbdelWahed.responses.categories_response;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Generated("com.robohorse.robopojogenerator")
public class SubSubCategory implements Serializable {

	@SerializedName("subcategory_id")
	private String subcategoryId;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;


	@SerializedName("translations")
	private List<Translation> translations;

	public List<Translation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<Translation> translations) {
		this.translations = translations;
	}

	public void setSubcategoryId(String subcategoryId){
		this.subcategoryId = subcategoryId;
	}

	public String getSubcategoryId(){
		return subcategoryId;
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
}
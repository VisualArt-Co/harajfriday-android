package com.benAbdelWahed.responses.categories_response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Translation implements Serializable {

    @SerializedName("subcategory_id")
    private String subcategoryId;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    @SerializedName("locale")
    private String locale;

    @SerializedName("subsub_id")
    private String subsub_id;

    @SerializedName("category_id")
    private String category_id;


    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSubsub_id() {
        return subsub_id;
    }

    public void setSubsub_id(String subsub_id) {
        this.subsub_id = subsub_id;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }
}
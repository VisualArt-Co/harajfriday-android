package com.benAbdelWahed.responses.categories_response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Category implements Serializable {

    @SerializedName("name")
    private String name;

    @SerializedName("active")
    private String active;

    @SerializedName("has_models")
    private String has_models;

    @SerializedName("id")
    private int id;

    @SerializedName("subcategories")
    private List<SubCategory> subcategories;

    @SerializedName("translations")
    private List<Translation> translations;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getHas_models() {
        return has_models;
    }

    public void setHas_models(String has_models) {
        this.has_models = has_models;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
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

    public void setSubcategories(List<SubCategory> subcategories) {
        this.subcategories = subcategories;
    }

    public List<SubCategory> getSubcategories() {
        return subcategories;
    }
}
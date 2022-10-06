package com.benAbdelWahed.responses.error_response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Data {

    @SerializedName("password")
    private List<String> password;
    @SerializedName("user_name")
    private List<String> username;
    @SerializedName("phone")
    private List<String> phone;
    @SerializedName("price")
    private List<String> price;
    @SerializedName("mazad_id")
    private List<String> mazad_id;
    @SerializedName("product_id")
    private List<String> product_id;

    public List<String> getUsername() {
        return username;
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

    public List<String> getMazad_id() {
        return mazad_id;
    }

    public void setMazad_id(List<String> mazad_id) {
        this.mazad_id = mazad_id;
    }

    public List<String> getPrice() {
        return price;
    }

    public void setPrice(List<String> price) {
        this.price = price;
    }

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    public void setPassword(List<String> password) {
        this.password = password;
    }

    public List<String> getPassword() {
        return password;
    }

    public List<String> getProduct_id() {
        return product_id;
    }

    public void setProduct_id(List<String> product_id) {
        this.product_id = product_id;
    }
}
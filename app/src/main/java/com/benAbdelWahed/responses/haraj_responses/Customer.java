package com.benAbdelWahed.responses.haraj_responses;

import com.benAbdelWahed.utils.StaticMembers;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class Customer implements Serializable {

    @SerializedName("image")
    private String image;

    @SerializedName("lng")
    private String lng;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("email_verified_at")
    private Object emailVerifiedAt;

    @SerializedName("social_id")
    private Object socialId;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("phone")
    private String phone;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("id")
    private int id;

    @SerializedName("countOfLike")
    private int countOfLike;

    @SerializedName("countOfDisLike")
    private int countOfDisLike;

    @SerializedName("averageRate")
    private double averageRate;

    @SerializedName("userType")
    private String userType;

    @SerializedName("state_id")
    private String stateId;

    @SerializedName("email")
    private String email;

    @SerializedName("lat")
    private String lat;

    @SerializedName("city_id")
    private String cityId;

    @SerializedName("user_name")
    private String user_name;

    @SerializedName("checkPremuimStatus")
    private String checkPremuimStatus;

    @SerializedName("allRates")
    private List<AllRatesItem> allRates;

    @SerializedName("products")
    private List<ProductsItem> products;

    public String getCheckPremuimStatus() {
        return checkPremuimStatus;
    }

    public void setCheckPremuimStatus(String checkPremuimStatus) {
        this.checkPremuimStatus = checkPremuimStatus;
    }

    public int getCountOfLike() {
        return countOfLike;
    }

    public void setCountOfLike(int countOfLike) {
        this.countOfLike = countOfLike;
    }

    public int getCountOfDisLike() {
        return countOfDisLike;
    }

    public void setCountOfDisLike(int countOfDisLike) {
        this.countOfDisLike = countOfDisLike;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public List<ProductsItem> getProducts() {
        return products;
    }

    public void setProducts(List<ProductsItem> products) {
        this.products = products;
    }

    public void setAverageRate(double averageRate) {
        this.averageRate = averageRate;
    }

    public double getAverageRate() {
        return averageRate;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLng() {
        return lng;
    }

    public void setSocialId(Object socialId) {
        this.socialId = socialId;
    }

    public Object getSocialId() {
        return socialId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getStateId() {
        return stateId;
    }

    public void setAllRates(List<AllRatesItem> allRates) {
        this.allRates = allRates;
    }

    public List<AllRatesItem> getAllRates() {
        return allRates;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {
        return lat;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setEmailVerifiedAt(Object emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public Object getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isPremium() {
        return StaticMembers.PREMIUM.equals(userType);
    }
    public boolean isBlocked() {
        return StaticMembers.INACTIVE.equals(userType);
    }
}
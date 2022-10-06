package com.benAbdelWahed.responses.mazads_response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class DataItem implements Serializable {

    @SerializedName("owner")
    private User owner;

    @SerializedName("images")
    private List<ImagesItem> images;

    @SerializedName("payment_method_allowed")
    private ArrayList<String> payment_method_allowed;

    @SerializedName("description")
    private String description;

    @SerializedName("start_price")
    private String startPrice;

    @SerializedName("least_price")
    private String leastPrice;

    @SerializedName("most_price")
    private String mostPrice;


    @SerializedName("guarantee_price")
    private int guaranteePrice;

    @SerializedName("closeing_price")
    private String closeingPrice;

    @SerializedName("min_price")
    private String minPrice;

    @SerializedName("max_price")
    private String maxPrice;

    @SerializedName("subscribedInMazad")
    private boolean subscribedInMazad;

    @SerializedName("winner")
    private User winner;

    @SerializedName("start_increasing_time")
    private String startIncreasingTime;

    @SerializedName("end_increasing_time")
    private String endIncreasingTime;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("timeRemainingToEndtMazad")
    private int timeRemainingToEndtMazad;

    @SerializedName("name")
    private String name;

    @SerializedName("id")
    private int id;

    @SerializedName("state_id")
    private String stateId;

    @SerializedName("start_date_time")
    private String startDateTime;

    @SerializedName("status")
    private String status;

    @SerializedName("mazadType")
    private String mazadType;

    @SerializedName("city_id")
    private String cityId;

    @SerializedName("is_require_the_start_price_in_wallet")
    private int isRequireTheStartPriceInWallet;

    public ArrayList<String> getPayment_method_allowed() {
        return payment_method_allowed;
    }

    public void setPayment_method_allowed(ArrayList<String> payment_method_allowed) {
        this.payment_method_allowed = payment_method_allowed;
    }

    public int getIsRequireTheStartPriceInWallet() {
        return isRequireTheStartPriceInWallet;
    }

    public void setIsRequireTheStartPriceInWallet(int isRequireTheStartPriceInWallet) {
        this.isRequireTheStartPriceInWallet = isRequireTheStartPriceInWallet;
    }

    public int getGuaranteePrice() {
        return guaranteePrice;
    }

    public void setGuaranteePrice(int guaranteePrice) {
        this.guaranteePrice = guaranteePrice;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public boolean isPending() {
        return paymentStatus == "pendding";
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    public void setImages(List<ImagesItem> images) {
        this.images = images;
    }

    public List<ImagesItem> getImages() {
        return images;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setStartPrice(String startPrice) {
        this.startPrice = startPrice;
    }

    public String getStartPrice() {
        return startPrice;
    }

    public void setLeastPrice(String leastPrice) {
        this.leastPrice = leastPrice;
    }

    public String getLeastPrice() {
        return leastPrice;
    }

    public void setMostPrice(String mostPrice) {
        this.mostPrice = mostPrice;
    }

    public String getMostPrice() {
        return mostPrice;
    }

    public void setCloseingPrice(String closeingPrice) {
        this.closeingPrice = closeingPrice;
    }

    public String getCloseingPrice() {
        return closeingPrice;
    }

    public void setSubscribedInMazad(boolean subscribedInMazad) {
        this.subscribedInMazad = subscribedInMazad;
    }

    public boolean isSubscribedInMazad() {
        return subscribedInMazad;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public void setStartIncreasingTime(String startIncreasingTime) {
        this.startIncreasingTime = startIncreasingTime;
    }

    public String getStartIncreasingTime() {
        return startIncreasingTime;
    }

    public void setEndIncreasingTime(String endIncreasingTime) {
        this.endIncreasingTime = endIncreasingTime;
    }

    public String getEndIncreasingTime() {
        return endIncreasingTime;
    }

    public void setTimeRemainingToEndtMazad(int timeRemainingToEndtMazad) {
        this.timeRemainingToEndtMazad = timeRemainingToEndtMazad;
    }

    public int getTimeRemainingToEndtMazad() {
        return timeRemainingToEndtMazad;
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

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setMazadType(String mazadType) {
        this.mazadType = mazadType;
    }

    public String getMazadType() {
        return mazadType;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityId() {
        return cityId;
    }
}
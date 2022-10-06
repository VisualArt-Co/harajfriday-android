package com.benAbdelWahed.responses.haraj_responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class ProductsItem implements Serializable {

    @SerializedName("featured")
    private String featured;

    @SerializedName("comments")
    private List<Comment> comments;

    @SerializedName("description")
    private String description;

    @SerializedName("active")
    private String active;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("title")
    private String title;

    @SerializedName("images")
    private List<ImagesItem> images;

    @SerializedName("is_product_favoirte")
    private boolean isProductFavoirte;

    @SerializedName("is_product_follow")
    private boolean isProductFollow;

    @SerializedName("subcategory_id")
    private String subcategoryId;

    @SerializedName("category_id")
    private String categoryId;

    @SerializedName("price")
    private String price;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("model")
    private String model;

    @SerializedName("id")
    private int id;

    @SerializedName("state_id")
    private String stateId;

    @SerializedName("subsub_id")
    private String subsubId;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("city_id")
    private String cityId;

    @SerializedName("commentable")
    private String commentable;

    public boolean isProductFavoirte() {
        return isProductFavoirte;
    }

    public void setProductFavoirte(boolean productFavoirte) {
        isProductFavoirte = productFavoirte;
    }

    public boolean isProductFollow() {
        return isProductFollow;
    }

    public void setProductFollow(boolean productFollow) {
        isProductFollow = productFollow;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public String getFeatured() {
        return featured;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getActive() {
        return active;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public List<ImagesItem> getImages() {
        return images;
    }

    public void setImages(List<ImagesItem> images) {
        this.images = images;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
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

    public void setSubsubId(String subsubId) {
        this.subsubId = subsubId;
    }

    public String getSubsubId() {
        return subsubId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCommentable() {
        return commentable;
    }

    public void setCommentable(String commentable) {
        this.commentable = commentable;
    }
}
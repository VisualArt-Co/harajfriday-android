package com.benAbdelWahed.responses.haraj_responses;

import com.benAbdelWahed.responses.categories_response.Category;
import com.benAbdelWahed.responses.categories_response.SubCategory;
import com.benAbdelWahed.responses.categories_response.SubSubCategory;
import com.benAbdelWahed.responses.cities_response.City;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class DataItem implements Serializable {

    @SerializedName("images")
    private List<ImagesItem> images;

    @SerializedName("comments")
    private List<Comment> comments;

    @SerializedName("relatedProducts")
    private List<DataItem> relatedProducts;

    @SerializedName("city")
    private City city;

    @SerializedName("description")
    private String description;

    @SerializedName("active")
    private String active;

    @SerializedName("commentable")
    private String commentable;

    @SerializedName("title")
    private String title;

    @SerializedName("subcategory_id")
    private SubCategory subcategoryId;

    @SerializedName("IsProductFavoirte")
    private boolean isProductFavoirte;

    @SerializedName("isProductFollow")
    private boolean isProductFollow;

    @SerializedName("price")
    private String price;

    @SerializedName("model")
    private String model;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("id")
    private int id;

    @SerializedName("state")
    private City state;

    @SerializedName("category")
    private Category category;

    @SerializedName("subsub_id")
    private SubSubCategory subsubId;

    @SerializedName("customer")
    private Customer customer;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("phone")
    private String phone;

    //////////////////////////////


    public String getCommentable() {
        return commentable;
    }

    public void setCommentable(String commentable) {
        this.commentable = commentable;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<DataItem> getRelatedProducts() {
        return relatedProducts;
    }

    public void setRelatedProducts(List<DataItem> relatedProducts) {
        this.relatedProducts = relatedProducts;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setImages(List<ImagesItem> images) {
        this.images = images;
    }

    public List<ImagesItem> getImages() {
        return images;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public SubCategory getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(SubCategory subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

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

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
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

    public void setState(City state) {
        this.state = state;
    }

    public City getState() {
        return state;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setSubsubId(SubSubCategory subsubId) {
        this.subsubId = subsubId;
    }

    public SubSubCategory getSubsubId() {
        return subsubId;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }
}
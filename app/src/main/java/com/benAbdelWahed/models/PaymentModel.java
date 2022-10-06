package com.benAbdelWahed.models;

public class PaymentModel {

    private String user_name, name, phone, message, accountNumber,amount, product_id;


    public PaymentModel() {
    }

    public PaymentModel(String user_name, String name, String phone, String message, String accountNumber, String amount, String product_id) {
        this.user_name = user_name;
        this.name = name;
        this.phone = phone;
        this.message = message;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.product_id = product_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }
}

package com.benAbdelWahed.responses.mazads_response;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("com.robohorse.robopojogenerator")
public class MazadDetailsResponse {

    @SerializedName("data")
    private DataItem data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public DataItem getData() {
        return data;
    }

    public void setData(DataItem data) {
        this.data = data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
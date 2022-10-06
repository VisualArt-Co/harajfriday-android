package com.benAbdelWahed.responses.haraj_responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {

    @SerializedName("data")
    private List<DataItem> data;

    @SerializedName("meta")
    private Meta meta;

    public List<DataItem> getData() {
        return data;
    }
    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }


    public void setData(List<DataItem> data) {
        this.data = data;
    }
}

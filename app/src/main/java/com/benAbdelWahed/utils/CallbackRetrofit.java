package com.benAbdelWahed.utils;

import android.content.Context;
import android.util.Log;

import com.benAbdelWahed.R;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class CallbackRetrofit<T> implements Callback<T> {

    private Context context;

    public CallbackRetrofit(Context context) {
        this.context = context;
    }

    @Override
    public abstract void onResponse(@NotNull Call<T> call, @NotNull Response<T> response);

    @Override
    public void onFailure(@NotNull Call<T> call, @NotNull Throwable t) {
        Log.e("network", "onFailure");
        if (context != null)
            StaticMembers.toastMessageShort(context, R.string.connection_errorــ);
    }
}

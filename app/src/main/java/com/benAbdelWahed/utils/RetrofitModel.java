package com.benAbdelWahed.utils;

import android.content.Context;

import com.benAbdelWahed.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitModel {

    public static ApiInterface getApi(Context context) {
        return getAPI(context);
    }

    public static ApiInterface getApi() {
        return getAPI(null);
    }

    public static ApiInterface getFirebaseAPI() {
        Retrofit retrofit;
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.addInterceptor(chain -> {
            Request.Builder requestBuilder = chain.request().newBuilder();
            requestBuilder.addHeader("Authorization", "key=" + BuildConfig.API_KEY).build();
            requestBuilder.addHeader("Content-Type", "application/json");
            requestBuilder.addHeader("Accept", "application/json");
            return chain.proceed(requestBuilder.build());
        });
        retrofit = new Retrofit.Builder().baseUrl(StaticMembers.FIREBASE_BASE_URL).client(builder.build()).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(ApiInterface.class);
    }

    private static ApiInterface getAPI(Context context) {
        Retrofit retrofit;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel((HttpLoggingInterceptor.Level.BODY));

        OkHttpClient.Builder builder = new OkHttpClient()
                .newBuilder()
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    requestBuilder.addHeader("Content-Type", "application/json");
                    requestBuilder.addHeader("Accept", "application/json");
                    requestBuilder.addHeader("X-localization", StaticMembers.getLanguage(context)).build();
                    if (context != null && PrefManager.getInstance(context).getAPIToken() != null &&
                            !PrefManager.getInstance(context).getAPIToken().isEmpty())
                        requestBuilder.addHeader("Authorization", "Bearer " + PrefManager.getInstance(context).getAPIToken()).build();
                    return chain.proceed(requestBuilder.build());
                });

        retrofit = new Retrofit.Builder().baseUrl(StaticMembers.getBaseAPIURL()).client(builder.build()).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(ApiInterface.class);
    }
}


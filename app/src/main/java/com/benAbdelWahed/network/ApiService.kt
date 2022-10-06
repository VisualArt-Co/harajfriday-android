package com.benAbdelWahed.network

import android.content.Context
import com.benAbdelWahed.models.BuyModel
import com.benAbdelWahed.responses.ErrorResponse
import com.benAbdelWahed.responses.address.Address
import com.benAbdelWahed.responses.address.AddressResponse
import com.benAbdelWahed.responses.auth.balance.BalanceResponse
import com.benAbdelWahed.responses.auth.coupon.CouponResponse
import com.benAbdelWahed.responses.auth.methods.MethodsResponse
import com.benAbdelWahed.responses.auth.wallet.WalletResponse
import com.benAbdelWahed.responses.customer_response.ProfileResponse
import com.benAbdelWahed.responses.deals.Data
import com.benAbdelWahed.responses.deals.DealsResponse
import com.benAbdelWahed.responses.order.OrderResponse
import com.benAbdelWahed.responses.payment.BuyResponse
import com.benAbdelWahed.responses.subscription.SubscriptionResponse
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiService {

    @GET("ActiveDelas")
    suspend fun deals(
            @Query("perPage") perPage: Int = 10,
            @Query("page") page: Int,
            @Query("global") global: Boolean,
    ): DealsResponse

    @GET("getAddresses")
    suspend fun getAddresses(
            @Query("perPage") perPage: Int = 10,
            @Query("page") page: Int,
    ): AddressResponse

    @GET("myOrders")
    suspend fun getOrders(
            @Query("perPage") perPage: Int = 10,
            @Query("page") page: Int,
    ): OrderResponse

    @POST("createAdress")
    suspend fun createAdress(
            @Body address: Address
    ): ErrorResponse

    @POST("updateAdress/{id}")
    suspend fun updateAdress(
            @Path("id") dealId: Int,
            @Body address: Address
    ): ErrorResponse

    @DELETE("deleteAddresses/{id}")
    suspend fun deleteAddresses(
            @Path("id") dealId: Int
    ): ErrorResponse

    @GET("DealInfo/{id}")
    suspend fun getDealDetails(
            @Path("id") dealId: Int
    ): Data

    @GET("LastDeals")
    suspend fun pastDeals(
            @Query("perPage") perPage: Int = 10,
            @Query("page") page: Int,
            @Query("global") global: Boolean,
    ): DealsResponse

    @GET("userBalance")
    suspend fun userBalance(): BalanceResponse

    @GET("userUsableBalance")
    suspend fun userUsableBalance(): BalanceResponse

    @GET("WalletSummery")
    suspend fun walletSummery(
            @Query("perPage") perPage: Int = 10,
            @Query("page") page: Int,
    ): WalletResponse

    @GET("ApplayCoupon")
    suspend fun applyCoupon(@Query("coupon_name") coupon_name: String): CouponResponse

    @GET("ChargeCredit")
    suspend fun getAvailableMethodsForWallet(): MethodsResponse

    @POST("BuyDeal/{id}")
    suspend fun buyDeal(
            @Path("id") dealId: Int,
            @Body buyModel: BuyModel
    ): BuyResponse

    @POST("ChargeCredit")
    suspend fun chargeCredit(
            @Query("payment_method") payment_method: String,
            @Query("amount") amount: Int,
    ): BuyResponse

    @POST("UpgardeToPremium")
    @Headers(
            "Content-Type:application/json",
            "Accept:application/json"
    )
    suspend fun upgradeToPremium(@Query("payment_method") payment_method: String): BuyResponse

    @GET("SubscriptionStatus")
    @Headers(
            "Content-Type:application/json",
            "Accept:application/json"
    )
    suspend fun subscriptionStatus(): SubscriptionResponse

    @POST("profile")
    suspend fun getUserProfile(): ProfileResponse

    companion object {
        operator fun invoke(context: Context? = null): ApiService {
            val requestInterceptor = Interceptor {
                val url = it.request()
                        .url
                        .newBuilder()
                        .build()
                val request = it.request()
                        .newBuilder()
                        .run {
                            val token = context?.run { PrefManager(this).getAPIToken() }
                            if (!token.isNullOrEmpty())
                                addHeader(
                                        "Authorization",
                                        "Bearer $token"
                                )
                            addHeader("Content-Type","application/json")
                            addHeader("Accept","application/json")
                            addHeader(
                                    "X-localization",
                                    context?.run { StaticMembers.getLanguage(this) } ?: "en"
                            )
                        }
                        .url(url)
                        .build()
                return@Interceptor it.proceed(request)
            }

            val logging = HttpLoggingInterceptor()
            logging.level = (HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(requestInterceptor)
                    .addInterceptor(logging)
                    .build()
            return Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(StaticMembers.getBaseAPIURL())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
        }
    }
}
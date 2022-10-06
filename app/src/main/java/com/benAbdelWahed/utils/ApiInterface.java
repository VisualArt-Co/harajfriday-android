package com.benAbdelWahed.utils;

import com.benAbdelWahed.models.ChangePasswordForgetModel;
import com.benAbdelWahed.models.ChangePasswordModel;
import com.benAbdelWahed.models.ContactUsModel;
import com.benAbdelWahed.models.FirebaseBody;
import com.benAbdelWahed.models.LoginModel;
import com.benAbdelWahed.models.PaymentModel;
import com.benAbdelWahed.models.PremiumModel;
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse;
import com.benAbdelWahed.responses.bank_responses.BankResponse;
import com.benAbdelWahed.responses.banners_responses.BannersResponse;
import com.benAbdelWahed.responses.categories_response.CategoryResponse;
import com.benAbdelWahed.responses.cities_response.CitiesResponse;
import com.benAbdelWahed.responses.conditions_response.ConditionResponse;
import com.benAbdelWahed.responses.customer_response.ProfileResponse;
import com.benAbdelWahed.responses.firebase_responses.FirebaseResponse;
import com.benAbdelWahed.responses.haraj_responses.HarajPageProductsResponse;
import com.benAbdelWahed.responses.haraj_responses.HarajProductsResponse;
import com.benAbdelWahed.responses.haraj_responses.ProductDetailsResponse;
import com.benAbdelWahed.responses.login_response.LoginResponse;
import com.benAbdelWahed.responses.mazads_response.AllMazadResponse;
import com.benAbdelWahed.responses.mazads_response.MazadDetailsResponse;
import com.benAbdelWahed.responses.notification_responses.CountNotifyResponse;
import com.benAbdelWahed.responses.notification_responses.NotificationsResponse;
import com.benAbdelWahed.responses.other_response.DynamicPagesResponse;
import com.benAbdelWahed.responses.payment.BuyResponse;
import com.benAbdelWahed.responses.payment.BuyWalletResponse;
import com.benAbdelWahed.responses.send_sms.SMSResponse;
import com.benAbdelWahed.responses.settings_response.SettingsResponse;
import com.benAbdelWahed.responses.social.SocialResponse;
import com.benAbdelWahed.responses.subscribers_response.SubscribersResponse;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import static com.benAbdelWahed.utils.StaticMembers.COMMENT_ID;
import static com.benAbdelWahed.utils.StaticMembers.CUSTOMER_ID;
import static com.benAbdelWahed.utils.StaticMembers.MAZAD_ID;
import static com.benAbdelWahed.utils.StaticMembers.PRODUCT_ID;
import static com.benAbdelWahed.utils.StaticMembers.STATUS;

public interface ApiInterface {
    @POST(StaticMembers.login)
    Call<LoginResponse> login(@Body LoginModel logInSendModel);

    @POST(StaticMembers.forgetPassword)
    Call<LoginResponse> forgetPassword(@Body ChangePasswordForgetModel changePasswordModel);

    @POST(StaticMembers.CHANGE_PASSWORD)
    Call<AddIncResponse> changePassword(@Body ChangePasswordModel changePasswordModel);

    @POST(StaticMembers.CONTACT_US)
    Call<AddIncResponse> contactUs(@Body ContactUsModel contactUsModel);

    @POST(StaticMembers.register)
    Call<LoginResponse> register(@Body RequestBody requestBody);

    @POST(StaticMembers.EDIT_PROFILE)
    Call<AddIncResponse> editProfile(@Body RequestBody requestBody);

    @POST(StaticMembers.ADD_Haraj)
    Call<AddIncResponse> addHaraj(@Body RequestBody requestBody);

    @POST(StaticMembers.EDIT_PRODUCT)
    Call<AddIncResponse> editProduct(@Body RequestBody requestBody);

    @FormUrlEncoded
    @POST(StaticMembers.ADD_COMMENT)
    Call<LoginResponse> addComment(@Field(StaticMembers.CUSTOMER_ID) int customerId, @Field(PRODUCT_ID) int productId, @Field(StaticMembers.COMMENT) String comment);

    @FormUrlEncoded
    @POST(StaticMembers.ADD_RATE)
    Call<LoginResponse> addRate(@Field(StaticMembers.CUSTOMER_ID) int customerId, @Field(StaticMembers.COMMENT) String comment, @Field(StaticMembers.RATE) int rate);

    @POST(StaticMembers.CITIES)
    Call<CitiesResponse> cities();

    @POST(StaticMembers.ALL_MAZAD)
    Call<AllMazadResponse> getAllMazads(@Query(STATUS) String status);

    @POST(StaticMembers.MAZAD_DETAILS)
    Call<MazadDetailsResponse> getMazadDetails(@Query(MAZAD_ID) int mazadId);

    @POST(StaticMembers.REMOVE_COMMENT)
    Call<AddIncResponse> removeComment(@Query(COMMENT_ID) int comment_id);

    @POST(StaticMembers.REMOVE_PRODUCT)
    Call<AddIncResponse> removeProduct(@Query(PRODUCT_ID) int product_id);

    @POST(StaticMembers.SEARCH_PRODUCTS)
    Call<HarajProductsResponse> getAllProducts(@QueryMap HashMap<String, String> filters);

    @POST(StaticMembers.SEARCH_PRODUCTS_PAGINATION)
    Call<HarajPageProductsResponse> getAllProductsPagination(@QueryMap HashMap<String, String> filters);

    @POST(StaticMembers.FOLLOWED_PRODUCTS)
    Call<HarajProductsResponse> getFollowedProducts();

    @POST(StaticMembers.COUNT_UNREAD_NOTIFY)
    Call<CountNotifyResponse> getCountUnReadNotifications();

    @POST(StaticMembers.RESET_COUNT_UNREAD_NOTIFICATIONS)
    Call<AddIncResponse> resetCountNuRead();

    @POST(StaticMembers.FAV_HARAJ)
    Call<HarajProductsResponse> getFavHaraj();

    @POST(StaticMembers.PRODUCT_DETAILS)
    Call<ProductDetailsResponse> getProductDetails(@Query(PRODUCT_ID) String productId);

    @POST(StaticMembers.CATEGORIES)
    Call<CategoryResponse> getAllCategories();

    @POST(StaticMembers.BANNERS)
    Call<BannersResponse> getAllBanners();

    @POST(StaticMembers.NOTIFICATION_LIST)
    Call<NotificationsResponse> getAllNotification();

    @POST(StaticMembers.DELETE_NOTI)
    Call<AddIncResponse> deleteAllNotification();


    @POST(StaticMembers.CUSTOMER_BANNERS)
    Call<BannersResponse> getCustomerBanners(@Query(CUSTOMER_ID) int customerId);

    @POST(StaticMembers.PROFILE)
    @Headers("Content-Type: application/json")
    Call<ProfileResponse> getProfile();

    @POST(StaticMembers.DETAILS_BY_ID)
    Call<ProfileResponse> getUserDetailsById(@Query(CUSTOMER_ID) int customerId);

    @FormUrlEncoded
    @POST(StaticMembers.ADD_FAVORITE)
    Call<AddIncResponse> addFavorite(@Field(PRODUCT_ID) int productId);

    @FormUrlEncoded
    @POST(StaticMembers.REMOVE_FAVORITE)
    Call<AddIncResponse> removeFavorite(@Field(PRODUCT_ID) int productId);

    @FormUrlEncoded
    @POST(StaticMembers.ADD_FOLLOW)
    Call<AddIncResponse> addFollow(@Field(PRODUCT_ID) int productId);

    @FormUrlEncoded
    @POST(StaticMembers.REMOVE_FOLLOW)
    Call<AddIncResponse> removeFollow(@Field(PRODUCT_ID) int productId);

    @POST(StaticMembers.UPDATE_PREMIUM)
    Call<AddIncResponse> upgradeToPremium(@Body PremiumModel premiumModel);

    @FormUrlEncoded
    @POST(StaticMembers.REPORT_COMMENT)
    Call<AddIncResponse> reportComment(@Field(COMMENT_ID) int commentId, @Field(StaticMembers.PROBLEM) String problem);

    @FormUrlEncoded
    @POST(StaticMembers.REPORT_PRODUCT)
    Call<AddIncResponse> reportProduct(@Field(PRODUCT_ID) int productId, @Field(StaticMembers.PROBLEM) String problem);

    @POST(StaticMembers.states)
    Call<CitiesResponse> states(@Query("city_id") String cityId);

    @POST(StaticMembers.SEND_CODE)
    Call<SMSResponse> sendSms(@Query("phone") String phone);

    @POST(StaticMembers.SEND_CODE_FORGET)
    Call<SMSResponse> sendCodeForget(@Query("phone") String phone);

    @POST(StaticMembers.CHECK_PHONE_CODE)
    Call<SMSResponse> checkPhone(@Query("phone") String phone, @Query("code") String code);

    @POST(StaticMembers.CHECK_PHONE_CODE_FORGET)
    Call<SMSResponse> checkPhoneForget(@Query("phone") String phone, @Query("code") String code);


    @POST(StaticMembers.SETTINGS)
    Call<SettingsResponse> getSettings();

    @GET(StaticMembers.LOG_OUT)
    Call<SMSResponse> logout(@Query("firebaseToken") String firebaseToken);

    @POST(StaticMembers.ADD_INCREASE)
    Call<AddIncResponse> addIncrement(@Query("mazad_id") int mazadId, @Query("price") int price);

    @POST(StaticMembers.SUBSCRIBE)
    Call<BuyResponse> subscribeToMazad(@Query("payment_method") String paymentMethod, @Query("mazad_id") int mazadId);

    @POST(StaticMembers.SUBSCRIBE)
    Call<BuyWalletResponse> subscribeToMazadWallet(@Query("payment_method") String paymentMethod, @Query("mazad_id") int mazadId);

    @POST(StaticMembers.CONDITIONS)
    Call<ConditionResponse> getConditions();

    @POST(StaticMembers.SOCIAL_LINKS)
    Call<SocialResponse> getSocialLinks();

    @POST(StaticMembers.SUBSCRIBER_MAZAD)
    Call<SubscribersResponse> getSubscribers(@Query("mazad_id") int mazadId);

    @POST(StaticMembers.DYNAMIC_PAGES)
    Call<DynamicPagesResponse> getDynamicPages();

    @POST(StaticMembers.BANK_ACCOUNTS)
    Call<BankResponse> getBankAccounts();

    @POST(StaticMembers.PAYMENT)
    Call<AddIncResponse> sendPayment(@Body PaymentModel paymentModel);


    @POST("send")
    Call<FirebaseResponse> sendMessage(@Body FirebaseBody firebaseBody);
}

package com.benAbdelWahed.network.repos

import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.responses.ErrorResponse
import com.benAbdelWahed.responses.address.Address
import com.benAbdelWahed.responses.address.AddressResponse
import com.benAbdelWahed.responses.customer_response.ProfileResponse
import com.benAbdelWahed.responses.deals.Data
import com.benAbdelWahed.responses.deals.DealsResponse
import com.benAbdelWahed.responses.order.OrderResponse
import com.benAbdelWahed.responses.payment.BuyResponse
import com.benAbdelWahed.responses.subscription.SubscriptionResponse
import kotlinx.coroutines.CoroutineDispatcher

interface DealsRepo {

    val apiService: ApiService
    val dispatcher: CoroutineDispatcher
    suspend fun getDeals(page: Int, perPage: Int = 10): ResultWrapper<DealsResponse>
    suspend fun getAddresses(page: Int, perPage: Int = 10): ResultWrapper<AddressResponse>
    suspend fun getOrders(page: Int, perPage: Int = 10): ResultWrapper<OrderResponse>
    suspend fun addAddress(address: Address): ResultWrapper<ErrorResponse>
    suspend fun editAddress(id: Int, address: Address): ResultWrapper<ErrorResponse>
    suspend fun deleteAddress(id: Int): ResultWrapper<ErrorResponse>
    suspend fun getDealDetails(dealId: Int): ResultWrapper<Data>
    suspend fun getPastDeals(page: Int, perPage: Int = 10): ResultWrapper<DealsResponse>
    suspend fun buyDeal(id: Int, paymentMethod: String, quantity: Int, address: Int): ResultWrapper<BuyResponse>
    suspend fun getSubscriptionDetails(): ResultWrapper<SubscriptionResponse>
    suspend fun getUserProfile(): ResultWrapper<ProfileResponse>
    suspend fun upgradeToPremium(paymentMethod: String): ResultWrapper<BuyResponse>
}
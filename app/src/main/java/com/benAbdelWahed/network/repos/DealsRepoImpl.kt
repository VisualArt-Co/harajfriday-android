package com.benAbdelWahed.network.repos

import com.benAbdelWahed.models.BuyModel
import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.safeApiCall
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
import kotlinx.coroutines.Dispatchers

class DealsRepoImpl constructor(
        override val apiService: ApiService,
        override val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DealsRepo {
    override suspend fun getDeals(page: Int, perPage: Int): ResultWrapper<DealsResponse> {
        return safeApiCall(dispatcher) {
            apiService.deals(perPage, page, true)
        }
    }

    override suspend fun getAddresses(page: Int, perPage: Int): ResultWrapper<AddressResponse> {
        return safeApiCall(dispatcher) {
            apiService.getAddresses(perPage, page)
        }
    }

    override suspend fun getOrders(page: Int, perPage: Int): ResultWrapper<OrderResponse> {
        return safeApiCall(dispatcher) {
            apiService.getOrders(perPage, page)
        }
    }

    override suspend fun addAddress(address: Address): ResultWrapper<ErrorResponse> {
        return safeApiCall(dispatcher) {
            apiService.createAdress(address)
        }
    }

    override suspend fun editAddress(id: Int, address: Address): ResultWrapper<ErrorResponse> {
        return safeApiCall(dispatcher) {
            apiService.updateAdress(id, address)
        }
    }

    override suspend fun deleteAddress(id: Int): ResultWrapper<ErrorResponse> {
        return safeApiCall(dispatcher) {
            apiService.deleteAddresses(id)
        }
    }

    override suspend fun getDealDetails(dealId: Int): ResultWrapper<Data> {
        return safeApiCall(dispatcher) {
            apiService.getDealDetails(dealId)
        }
    }

    override suspend fun getPastDeals(page: Int, perPage: Int): ResultWrapper<DealsResponse> {
        return safeApiCall(dispatcher) {
            apiService.pastDeals(perPage, page, true)
        }
    }

    override suspend fun buyDeal(id: Int, paymentMethod: String, quantity: Int, address: Int): ResultWrapper<BuyResponse> {
        return safeApiCall(dispatcher) {
            apiService.buyDeal(id, BuyModel(paymentMethod, quantity, address))
        }
    }

    override suspend fun upgradeToPremium(paymentMethod: String): ResultWrapper<BuyResponse> {
        return safeApiCall(dispatcher) {
            apiService.upgradeToPremium(paymentMethod)
        }
    }

    override suspend fun getSubscriptionDetails(): ResultWrapper<SubscriptionResponse> {
        return safeApiCall(dispatcher) {
            apiService.subscriptionStatus()
        }
    }

    override suspend fun getUserProfile(): ResultWrapper<ProfileResponse> {
        return safeApiCall(dispatcher) {
            apiService.getUserProfile()
        }
    }
}
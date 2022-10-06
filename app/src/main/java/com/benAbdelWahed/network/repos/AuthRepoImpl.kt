package com.benAbdelWahed.network.repos

import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.network.safeApiCall
import com.benAbdelWahed.responses.auth.balance.BalanceResponse
import com.benAbdelWahed.responses.auth.coupon.CouponResponse
import com.benAbdelWahed.responses.auth.methods.MethodsResponse
import com.benAbdelWahed.responses.auth.wallet.WalletResponse
import com.benAbdelWahed.responses.payment.BuyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AuthRepoImpl(override val apiService: ApiService, override val dispatcher: CoroutineDispatcher = Dispatchers.IO) : AuthRepo {
    override suspend fun getUserBalance(): ResultWrapper<BalanceResponse> {
        return safeApiCall(dispatcher) {
            apiService.userBalance()
        }
    }

    override suspend fun getUserUsableBalance(): ResultWrapper<BalanceResponse> {
        return safeApiCall(dispatcher) {
            apiService.userUsableBalance()
        }
    }

    override suspend fun walletSummery(perPage: Int, page: Int): ResultWrapper<WalletResponse> {
        return safeApiCall(dispatcher) {
            apiService.walletSummery(perPage, page)
        }
    }

    override suspend fun addCoupon(coupon: String): ResultWrapper<CouponResponse> {
        return safeApiCall(dispatcher) {
            apiService.applyCoupon(coupon)
        }
    }

    override suspend fun getAvailableMethodsForWallet(): ResultWrapper<MethodsResponse> {
        return safeApiCall(dispatcher) {
            apiService.getAvailableMethodsForWallet()
        }
    }

    override suspend fun addCreditToWallet(credit: Int, key: String): ResultWrapper<BuyResponse> {
        return safeApiCall(dispatcher) {
            apiService.chargeCredit(key,credit)
        }
    }
}
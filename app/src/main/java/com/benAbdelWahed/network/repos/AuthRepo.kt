package com.benAbdelWahed.network.repos

import com.benAbdelWahed.network.ApiService
import com.benAbdelWahed.network.ResultWrapper
import com.benAbdelWahed.responses.auth.balance.BalanceResponse
import com.benAbdelWahed.responses.auth.coupon.CouponResponse
import com.benAbdelWahed.responses.auth.methods.MethodsResponse
import com.benAbdelWahed.responses.auth.wallet.WalletResponse
import com.benAbdelWahed.responses.payment.BuyResponse
import kotlinx.coroutines.CoroutineDispatcher

interface AuthRepo {
    val apiService: ApiService
    val dispatcher: CoroutineDispatcher

    suspend fun getUserBalance(): ResultWrapper<BalanceResponse>
    suspend fun getUserUsableBalance(): ResultWrapper<BalanceResponse>
    suspend fun walletSummery(perPage: Int = 10,page: Int): ResultWrapper<WalletResponse>
    suspend fun addCoupon(coupon:String): ResultWrapper<CouponResponse>
    suspend fun getAvailableMethodsForWallet(): ResultWrapper<MethodsResponse>
    suspend fun addCreditToWallet(credit: Int, key: String): ResultWrapper<BuyResponse>
}
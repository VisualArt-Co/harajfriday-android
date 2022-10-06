package com.benAbdelWahed.responses.subscription

data class Data(
    val end_active_time: String,
    val methods_allowed_to_pay_subscrption: List<String>,
    val start_active_time: String,
    val subscrption_status: String,
    val time_left_details: TimeLeftDetails,
    val suspended_motivation: String,
    val time_left_in_days: Int
){
    enum class SubscriptionStatus{
        active,suspended,empty
    }
}

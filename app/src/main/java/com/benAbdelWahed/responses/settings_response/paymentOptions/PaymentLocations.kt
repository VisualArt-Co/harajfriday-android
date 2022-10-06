package com.benAbdelWahed.responses.settings_response.paymentOptions

data class PaymentLocations(
    val Pay_mazzad_tax: PayMazzadTax,
    val charge_wallet_redit: ChargeWalletRedit,
    val deals: Deals,
    val upgrade_to_premium: UpgradeToPremium
)
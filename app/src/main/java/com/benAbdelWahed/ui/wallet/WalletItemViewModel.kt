package com.benAbdelWahed.ui.wallet

import com.benAbdelWahed.responses.auth.wallet.PaymentHistoryItem
import com.benAbdelWahed.utils.ItemViewModel

class WalletItemViewModel(
        val item: PaymentHistoryItem,
        override val index: Int,
        val onItemClick: (item: PaymentHistoryItem, Int) -> Unit): ItemViewModel<PaymentHistoryItem>(item, index, onItemClick) {
}
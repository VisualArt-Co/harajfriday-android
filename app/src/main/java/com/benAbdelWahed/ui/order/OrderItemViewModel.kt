package com.benAbdelWahed.ui.order

import android.view.View
import com.benAbdelWahed.responses.order.Order

class OrderItemViewModel(
        val item: Order,
        val index: Int,
        val onItemClick: (item: Order, Int) -> Unit
) {

    fun onClick(view: View) {
        onItemClick(item, index)
    }
}
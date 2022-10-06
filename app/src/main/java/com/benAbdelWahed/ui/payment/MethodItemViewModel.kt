package com.benAbdelWahed.ui.payment

import android.view.View
import com.benAbdelWahed.models.Method

class MethodItemViewModel(
        val item: Method,
        val index: Int,
        var isSelected: Boolean = false,
        val onItemClick: (item: Method, Int) -> Unit
) {

    val listener = { pressed: Boolean, checked: Boolean ->
        onItemClick(item, index)
    }

    fun onClick(view: View) {
        onItemClick(item, index)
    }
}
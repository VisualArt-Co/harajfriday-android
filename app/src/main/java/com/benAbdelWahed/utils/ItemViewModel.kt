package com.benAbdelWahed.utils

import android.view.View

open class ItemViewModel<T>(
    val t: T,
    open val index: Int,
    open val onItemClicked: (T, index: Int) -> Unit
) {
    fun onClick(view: View) {
        onItemClicked(t, index)
    }
}
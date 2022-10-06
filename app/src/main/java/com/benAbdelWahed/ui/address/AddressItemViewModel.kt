package com.benAbdelWahed.ui.address

import android.view.View
import androidx.databinding.ObservableField
import com.benAbdelWahed.responses.address.Address

class AddressItemViewModel(
        val item: Address,
        val index: Int,
        var isSelected: Boolean = false,
        var isEditable: Boolean = false,
        val onItemEditClick: (item: Address, Int) -> Unit,
        val onItemDeleteClick: (item: Address, Int) -> Unit,
        val onItemClick: (item: Address, Int) -> Unit
){

    val details = ObservableField("")
    init {
        details.set((item.street?:"")+" "+(item.label?:""))
    }
    fun onClick(view: View) {
        onItemClick(item, index)
    }


    fun onDelete(view: View) {
        onItemDeleteClick(item, index)
    }

    fun onEdit(view: View) {
        onItemEditClick(item, index)
    }
}
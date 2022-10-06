package com.benAbdelWahed.ui.payment

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ItemPaymentMethodBinding
import com.benAbdelWahed.models.Method
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.Utils
import com.benAbdelWahed.utils.Utils.getBinding

class MethodsAdapter(
        val list: ArrayList<Method>,
        val onItemClick: (item: Method, Int) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPosition = 0

    inner class VerticalHolder(private val itemAddressBinding: ItemPaymentMethodBinding) :
            Holder(itemAddressBinding.root) {
        override fun bind() = with(itemAddressBinding)
        {
            val i = adapterPosition
            val isSelected = i == selectedPosition
            val item = list[i]
            Utils.getMethodBasedOnKey(item)
            viewModel = MethodItemViewModel(list[i], i, isSelected)
            { item, index ->
                val old = if (selectedPosition < list.size) selectedPosition else -1
                selectedPosition = index
                notifyItemChanged(old)
                notifyItemChanged(selectedPosition)
                onItemClick(item, selectedPosition)
            }
        }
    }

    fun selectMethod(key: String) {
        list.forEachIndexed { index, method ->
            if (method.key == key) {
                val old = if (selectedPosition < list.size) selectedPosition else -1
                selectedPosition = index
                notifyItemChanged(old)
                notifyItemChanged(selectedPosition)
                return@forEachIndexed
            }
        }
    }

    abstract inner class Holder(view: View) :
            RecyclerView.ViewHolder(view) {
        abstract fun bind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VerticalHolder(
                parent.getBinding(
                        R.layout.item_payment_method
                )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position != list.size)
            (holder as Holder).bind()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getSelectedItem(): Method {
        return list[selectedPosition]
    }

}
package com.benAbdelWahed.ui.order

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ItemOrderBinding
import com.benAbdelWahed.responses.order.Order
import com.benAbdelWahed.utils.Utils.getBinding

class OrderAdapter(
        val list: ArrayList<Order>,
        val onItemClick: (item: Order, Int) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isLoading: Boolean = false
        set(value) {
            field = value
            notifyItemChanged(list.size)
        }

    inner class VerticalHolder(private val itemOrderBinding: ItemOrderBinding) :
            Holder(itemOrderBinding.root) {
        override fun bind() = with(itemOrderBinding)
        {
            val i = adapterPosition
            viewModel = OrderItemViewModel(list[i], i) { item, index ->
                onItemClick(item, index)
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
                        R.layout.item_order
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

}
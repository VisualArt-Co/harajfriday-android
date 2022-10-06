package com.benAbdelWahed.ui.address

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ItemAddressBinding
import com.benAbdelWahed.responses.address.Address
import com.benAbdelWahed.utils.Utils.getBinding

class AddressAdapter(
        val isEditable : Boolean = true,
        val list: ArrayList<Address>,
        val onItemEditClick: (item: Address, Int) -> Unit = { _, _ -> },
        val onItemDeleteClick: (item: Address, Int) -> Unit = { _, _ -> },
        val onItemClick: (item: Address, Int) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedPosition = -1
    var isLoading: Boolean = false
        set(value) {
            field = value
            notifyItemChanged(list.size)
        }

    fun removeItem(index: Int) {
        list.removeAt(index)
        notifyItemRemoved(index)
        notifyItemRangeChanged(index, list.size)
    }

    inner class VerticalHolder(private val itemAddressBinding: ItemAddressBinding) :
            Holder(itemAddressBinding.root) {
        override fun bind() = with(itemAddressBinding)
        {
            val i = adapterPosition
            val isSelected = i == selectedPosition && isEditable
            viewModel = AddressItemViewModel(list[i], i, isSelected,isEditable, onItemEditClick, onItemDeleteClick)
            { item, index ->
                val old = if (selectedPosition < list.size) selectedPosition else 0
                selectedPosition = index
                notifyItemChanged(old)
                notifyItemChanged(index)
                onItemClick(item, index)
            }
        }
    }

    fun selectAddressById(id: Int) {
        list.forEachIndexed { i, it ->
            if (it.id == id) {
                val old = if (selectedPosition < list.size) selectedPosition else -1
                selectedPosition = i
                notifyItemChanged(old)
                notifyItemChanged(i)
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
                        R.layout.item_address
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
package com.benAbdelWahed.ui.wallet

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ItemWalletBinding
import com.benAbdelWahed.responses.auth.wallet.PaymentHistoryItem
import com.benAbdelWahed.utils.Utils.getBinding

class WalletAdapter(
        val list: ArrayList<PaymentHistoryItem>,
        val onItemClick: (item: PaymentHistoryItem, Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isLoading: Boolean = false
        set(value) {
            field = value
            notifyItemChanged(list.size)
        }


    inner class VerticalHolder(private val itemWalletBinding: ItemWalletBinding) :
            Holder(itemWalletBinding.root) {
        override fun bind() = with(itemWalletBinding)
        {
            val i = adapterPosition
            viewModel = WalletItemViewModel(list[i], i, onItemClick)
        }
    }

    abstract inner class Holder(view: View) :
            RecyclerView.ViewHolder(view) {
        abstract fun bind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VerticalHolder(
                parent.getBinding(
                        R.layout.item_wallet
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
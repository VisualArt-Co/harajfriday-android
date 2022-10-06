package com.benAbdelWahed.ui.deals

import android.os.CountDownTimer
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benAbdelWahed.R
import com.benAbdelWahed.databinding.ItemDealBinding
import com.benAbdelWahed.responses.deals.Deal
import com.benAbdelWahed.utils.BaseAdapter
import com.benAbdelWahed.utils.Utils.getBinding

class DealsAdapter(val isCurrentDeals:Boolean,override val list: ArrayList<Deal>, val onBuyClick: (deal: Deal, index: Int) -> Unit, override val onItemClick: (deal: Deal, index: Int) -> Unit) : BaseAdapter<Deal, DealsAdapter.Holder>(list, onItemClick) {

    inner class Holder(private val binding: ItemDealBinding) : RecyclerView.ViewHolder(binding.root) {

        var countDownTimer: CountDownTimer? = null
        fun bind() {
            binding.viewModel = DealItemViewModel(isCurrentDeals,countDownTimer,list[adapterPosition], adapterPosition, onBuyClick, onItemClick)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
                parent.getBinding(
                        R.layout.item_deal,
                )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
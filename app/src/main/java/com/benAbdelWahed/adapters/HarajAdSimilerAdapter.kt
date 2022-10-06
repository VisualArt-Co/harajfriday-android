package com.benAbdelWahed.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.benAbdelWahed.R
import com.benAbdelWahed.fragments.HarajAllDetailsFragment.Companion.getInstance
import com.benAbdelWahed.responses.haraj_responses.DataItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_haraj_small_ad.view.*

class HarajAdSimilerAdapter(private val activity: FragmentActivity, private val list: ArrayList<DataItem>, private val onEdit: () -> Unit) : RecyclerView.Adapter<HarajAdSimilerAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(activity).inflate(R.layout.item_haraj_small_ad, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind() = with(itemView) {
            val p = adapterPosition
            val item = list[p]
            if (item.images != null && item.images.size > 0) Glide.with(activity).load(item.images[0].image).placeholder(R.drawable.place_holder_logo).fitCenter().into(image!!) else image!!.setImageResource(R.drawable.place_holder_logo)
            itemView.setOnClickListener { v: View? -> getInstance(list, p) { onEdit.invoke() }.show(activity.supportFragmentManager, "harajAll") }
        }
    }

}
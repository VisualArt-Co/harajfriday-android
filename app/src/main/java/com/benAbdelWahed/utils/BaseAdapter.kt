package com.benAbdelWahed.utils

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.benAbdelWahed.R
import com.benAbdelWahed.utils.Utils.getBinding

abstract class BaseAdapter<Item, Holder : RecyclerView.ViewHolder>(open val list: MutableList<Item>, open val onItemClick: (item: Item, index: Int) -> Unit) : RecyclerView.Adapter<Holder>()
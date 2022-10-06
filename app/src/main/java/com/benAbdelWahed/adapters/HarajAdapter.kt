package com.benAbdelWahed.adapters

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.benAbdelWahed.R
import com.benAbdelWahed.fragments.HarajAllDetailsFragment
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.responses.haraj_responses.DataItem
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_haraj.view.*
import retrofit2.Call
import retrofit2.Response

class HarajAdapter : RecyclerView.Adapter<HarajAdapter.Holder> {
    private var activity: FragmentActivity
    private var list: ArrayList<DataItem>
    private var showLoading = false
    private var loadingCount = 5
    private var isFav = false
    private lateinit var prefManager: PrefManager
    private var myId = 0
    private var onEdit: () -> Unit = {}

    constructor(activity: FragmentActivity, list: ArrayList<DataItem>, onEdit: () -> Unit) {
        init(activity)
        this.activity = activity
        this.list = list
        this.onEdit = onEdit
    }

    constructor(activity: FragmentActivity, list: ArrayList<DataItem>, isFav: Boolean, onEdit: () -> Unit) {
        init(activity)
        this.activity = activity
        this.list = list
        this.isFav = isFav
        this.onEdit = onEdit
    }

    private fun init(activity: FragmentActivity) {
        prefManager = PrefManager.getInstance(activity)
        val customer = prefManager.getObject(StaticMembers.PROFILE, Customer::class.java) as Customer?
        if (customer != null) {
            myId = customer.id
        }
    }

    fun isShowLoading(): Boolean {
        return showLoading
    }

    fun setShowLoading(showLoading: Boolean) {
        this.showLoading = showLoading
        loadingCount = if (showLoading) 5 else 0
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(activity).inflate(R.layout.item_haraj, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return list.size + loadingCount
    }

    fun addFavorite(checkBox: CheckBox?, index: Int) {
        val item = list[index]
        val call: Call<AddIncResponse>
        call = if (!checkBox!!.isChecked) RetrofitModel.getApi(activity).removeFavorite(item.id) else RetrofitModel.getApi(activity).addFavorite(item.id)
        call.enqueue(object : CallbackRetrofit<AddIncResponse>(activity) {
            override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    StaticMembers.toastMessageShort(activity, R.string.success)
                    item.isProductFavoirte = checkBox.isChecked
                    if (!checkBox.isChecked && isFav) {
                        list.removeAt(index)
                        notifyItemRemoved(index)
                        notifyItemRangeChanged(index, list.size)
                    }
                } else {
                    checkBox.isChecked = !checkBox.isChecked
                    StaticMembers.checkError(activity, response.errorBody())
                }
            }

            override fun onFailure(call: Call<AddIncResponse>, t: Throwable) {
                super.onFailure(call, t)
                checkBox.isChecked = !checkBox.isChecked
            }
        })
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind() = with(itemView) {
            val p = adapterPosition
            if (p < list.size) {
                itemView.visibility = View.VISIBLE
                shimmerLayout!!.visibility = View.GONE
                detailsLayout!!.visibility = View.VISIBLE
                if (p % 2 == 0) {
                    itemView.setBackgroundColor(activity.resources.getColor(R.color.milky))
                } else {
                    itemView.setBackgroundColor(activity.resources.getColor(R.color.blue_lighter))
                }
                val item = list[p]
                itemView.setOnClickListener { v: View? -> HarajAllDetailsFragment.getInstance(list, p) { onEdit.invoke() }.show(activity.supportFragmentManager, "harajAll") }
                date!!.text = StaticMembers.getDateFromBackendForHaraj(item.createdAt)
                name!!.text = item.title
                userName!!.text = item.customer.user_name
                verifyImage!!.visibility = if (item.customer.isPremium) View.VISIBLE else View.GONE
                city!!.text = item.state.name
                Glide.with(itemView.context).load(if (item.images.size > 0) item.images[0].image else "").centerCrop().placeholder(R.drawable.place_holder_logo_small).error(R.drawable.place_holder_logo_small).into(image!!)
                if (item.images.size == 0 && item.imagePath != null && !item.imagePath.isEmpty()) Glide.with(itemView.context).load(item.imagePath).centerCrop().placeholder(R.drawable.place_holder_logo_small).error(R.drawable.place_holder_logo_small).into(image!!)
                wishListCheckBox!!.isChecked = item.isProductFavoirte
                wishListCheckBox!!.setOnCheckedChangeListener { buttonView: CompoundButton, isChecked: Boolean ->
                    if (buttonView.isPressed) {
                        addFavorite(wishListCheckBox, p)
                    }
                }
                deleteProduct!!.visibility = if (myId == item.customer.id) View.VISIBLE else View.GONE
                deleteProduct!!.setOnClickListener { v: View? ->
                    val alertDialog = AlertDialog.Builder(activity)
                    alertDialog.setTitle(activity.getString(R.string.sure_delete_product))
                    alertDialog.setPositiveButton(activity.getString(R.string.delete)) { dialog: DialogInterface?, which: Int -> deleteProductNow(p) }
                    alertDialog.setNegativeButton(activity.getString(R.string.cancel), null)
                    alertDialog.show()
                }
            } else {
                shimmerLayout!!.visibility = View.VISIBLE
                detailsLayout!!.visibility = View.GONE
                itemView.setBackgroundColor(activity.resources.getColor(R.color.white))
                itemView.visibility = if (showLoading) View.VISIBLE else View.GONE
            }
        }

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    private fun deleteProductNow(index: Int) {
        val productId = list[index].id
        val call = RetrofitModel.getApi(activity).removeProduct(productId)
        call.enqueue(object : CallbackRetrofit<AddIncResponse>(activity) {
            override fun onResponse(call: Call<AddIncResponse>, response: Response<AddIncResponse>) {
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    StaticMembers.toastMessageShort(activity, R.string.success_delete_product)
                    list.removeAt(index)
                    notifyItemRemoved(index)
                    notifyItemRangeChanged(index, itemCount)
                } else {
                    StaticMembers.checkError(activity, response.errorBody())
                }
            }
        })
    }
}
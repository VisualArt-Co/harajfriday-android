package com.benAbdelWahed.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.benAbdelWahed.R
import com.benAbdelWahed.responses.banners_responses.Banner
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.StaticMembers
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_banner_details.*
import kotlinx.android.synthetic.main.activity_haraj_details.toolbar
import java.util.*

class BannerDetailsActivity : AppCompatActivity() {

    private var banner: Banner? = null
    var myUserProfile: Customer? = null
    var number = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_details)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        myUserProfile = PrefManager.getInstance(this).getObject(StaticMembers.PROFILE, Customer::class.java) as Customer?
        banner = intent.getSerializableExtra(StaticMembers.BANNER) as Banner?
        val customer = banner!!.customer
        price.text = String.format(Locale.getDefault(), getString(R.string._rial), banner!!.price)

        userName.text = customer.user_name
        verifyImage.visibility = if (customer.isPremium) VISIBLE else GONE

        bannerId.text = "#${banner!!.id}"

        if (banner!!.image != null && banner!!.image!!.isNotEmpty())
            Glide.with(this).load(banner!!.image).centerCrop().placeholder(R.drawable.place_holder_logo).into(image)
        else
            image.setImageResource(R.drawable.place_holder_logo)

        if (customer.image != null && customer.image!!.isNotEmpty())
            Glide.with(this).load(customer.image).centerCrop().placeholder(R.drawable.place_holder_logo).into(userImage)
        else
            userImage.setImageResource(R.drawable.place_holder_logo)

        //rating.rating = customer.averageRate.toFloat()
        noOfRates.text = String.format(Locale.getDefault(), getString(R.string._f_5_rates), customer.averageRate.toFloat(), customer.allRates.size)

        if (myUserProfile != null)
            if (myUserProfile!!.id != customer!!.id)
                message.visibility = VISIBLE

        userImage.setOnClickListener {
            val intent = Intent(this, BannerOwnerProfileActivity::class.java)
            intent.putExtra(StaticMembers.CUSTOMER, customer)
            startActivity(intent)
        }

        userName.setOnClickListener {
            val intent = Intent(this, BannerOwnerProfileActivity::class.java)
            intent.putExtra(StaticMembers.CUSTOMER, customer)
            startActivity(intent)
        }
        message.setOnClickListener {
            if (myUserProfile != null) {
                val intent = Intent(this, ChatRoomActivity::class.java)
                intent.putExtra(StaticMembers.OTHER_ID, customer.id)
                startActivity(intent)
            } else StaticMembers.showLoginDialog(this)
        }

        number = customer!!.phone
        whatsapp.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=+966$number"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        call.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+966$number")
            startActivity(intent)
        }
    }
}

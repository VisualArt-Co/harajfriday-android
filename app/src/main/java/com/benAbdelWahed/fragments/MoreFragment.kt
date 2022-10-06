package com.benAbdelWahed.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.benAbdelWahed.R
import com.benAbdelWahed.activities.*
import com.benAbdelWahed.responses.haraj_responses.Customer
import com.benAbdelWahed.responses.send_sms.SMSResponse
import com.benAbdelWahed.responses.settings_response.SettingsData
import com.benAbdelWahed.responses.settings_response.SettingsResponse
import com.benAbdelWahed.ui.deals.DealsActivity
import com.benAbdelWahed.ui.membership.MembershipActivity
import com.benAbdelWahed.ui.order.OrdersActivity
import com.benAbdelWahed.ui.wallet.WalletActivity
import com.benAbdelWahed.utils.AlertUtil
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.fragment_more.*
import kotlinx.android.synthetic.main.progress_layout.*
import retrofit2.Call
import retrofit2.Response
import java.util.*

class MoreFragment : Fragment() {
    private lateinit var instanceId: String
    lateinit var prefManager: PrefManager
    var settingsData: SettingsData? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                instanceId = it.result!!.token
            }
        }
        prefManager = PrefManager(context)
        updateUI()
    }

    fun updateUI() {
        getSettings()
        val myUserProfile = prefManager.getObject(PROFILE, Customer::class.java) as Customer?
        if (myUserProfile != null) {
            editProfile.setOnClickListener {
                activity!!.startActivityForResult(Intent(activity, EditProfileActivity::class.java), EDIT_PROFILE_CODE)
            }
            followedProducts.setOnClickListener {
                startActivity(Intent(activity, FollowedHarajActivity::class.java))
            }
            myHaraj.setOnClickListener {
                val intent = Intent(activity, ProductOwnerProfileActivity::class.java)
                intent.putExtra(CUSTOMER, myUserProfile)
                activity!!.startActivity(intent)
            }
            signOut.text = String.format(Locale.getDefault(), getString(R.string.sign_out), (myUserProfile).user_name)
            signOut.setOnClickListener {
                progress.visibility = VISIBLE
                FirebaseDatabase.getInstance().reference.child(USERS).child("${myUserProfile.id}").child(FIREBASE_TOKENS).child(instanceId).removeValue().addOnCompleteListener {
                    PrefManager.getInstance(activity).apiToken = ""
                    PrefManager.getInstance(activity).removeKey(IS_PREMIUM)
                    PrefManager.getInstance(activity).removeKey(PROFILE)
                    PrefManager.getInstance(activity).removeKey(ADDRESS)
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity!!.finish()
                }
                logout()
            }
        } else {
            editProfile.visibility = GONE
            myHaraj.visibility = GONE
            signOut.text = getString(R.string.login__)
            signOut.setTextColor(resources.getColor(R.color.colorAccent))
            signOut.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_user_blue, 0, R.drawable.ic_forward, 0)
            signOut.setOnClickListener {
                startActivityOverAll(activity, LoginActivity::class.java)
            }
        }



        premiumSubscription.setOnClickListener {
            showSubscribePremium()
        }
        mazadRules.setOnClickListener {
            showSubscribeDialog()
        }
        myOrders.setOnClickListener {
            startActivity(Intent(activity, OrdersActivity::class.java))
        }
        pocket.setOnClickListener {
            startActivity(Intent(activity, WalletActivity::class.java))
        }
        deals.setOnClickListener {
            startActivity(Intent(activity, DealsActivity::class.java))
        }
        wishList.setOnClickListener {
            startActivity(Intent(activity, FavHarajActivity::class.java))
        }

        commissionAccount.setOnClickListener {
            CalculateDialog().show(activity!!.supportFragmentManager, "commissionAccount")
        }
        contactUs.setOnClickListener {
            ContactUsDialog().show(activity!!.supportFragmentManager, "contactUs")
        }

        other.setOnClickListener {
            OtherDialog().show(activity!!.supportFragmentManager, "OtherDialog")
        }

        addProduct.setOnClickListener {
            if (PrefManager.getInstance(context).hasToken())
                activity?.supportFragmentManager?.let { it1 -> AcceptHarajTermsDialog.getInstance().show(it1, "AcceptHarajTermsDialog") }
            else showLoginDialog(activity)
        }

        updateSettingsData()
    }

    private fun updateSettingsData() {
        settingsData = prefManager.getObject(SETTINGS, SettingsData::class.java) as SettingsData?
        settingsData?.also { data ->
            blackList?.setOnClickListener {
                AboutAppDialog.getInstance(data.blackList, getString(R.string.black_list)).show(activity!!.supportFragmentManager, "CalculateDialog")
            }
            discountSystem?.setOnClickListener {
                AboutAppDialog.getInstance(data.discountSystem, getString(R.string.discountSystem)).show(activity!!.supportFragmentManager, "DiscountSystemDialog")
            }
            serviceUseTreaty?.setOnClickListener {
                AboutAppDialog.getInstance(data.serviceUseTreaty, getString(R.string.serviceUseTreaty)).show(activity!!.supportFragmentManager, "ServiceUseTreatyDialog")
            }
            forbiddenProducts?.setOnClickListener {
                AboutAppDialog.getInstance(data.forbiddenProducts, getString(R.string.forbiddenProducts)).show(activity!!.supportFragmentManager, "ForbiddenProductsDialog")
            }
            aboutApp?.setOnClickListener {
                AboutAppDialog.getInstance(data.aboutApp, getString(R.string.about_app)).show(activity!!.supportFragmentManager, "AboutAppDialog")
            }
        }
    }

    fun logout() {
        val call = RetrofitModel.getApi(context).logout(instanceId)
        call.enqueue(object : CallbackRetrofit<SMSResponse>(context) {
            override fun onResponse(call: Call<SMSResponse>, response: Response<SMSResponse>) {
            }
        })
    }

    fun getSettings() {
        val call = RetrofitModel.getApi(context).settings
        call.enqueue(object : CallbackRetrofit<SettingsResponse>(context) {
            override fun onResponse(call: Call<SettingsResponse>, response: Response<SettingsResponse>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!.data != null) {
                        prefManager.setObject(SETTINGS, response.body()!!.data)
                        updateSettingsData()
                    }
                }
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
            }
        })
    }


    private fun showSubscribePremium() {
        settingsData?.run {
            if (subscrptionInPremiumIsActive == "1")
                startActivity(Intent(requireContext(), MembershipActivity::class.java))
            else AlertUtil(requireContext()).apply {
                message(subscrptionInPremiumDesactivationMessage)
                positiveButton(R.string.ok,null)
                show()
            }
        }
    }


    private var subscribeDialog: SubscribeDialog? = null

    private fun showSubscribeDialog() {
        if (subscribeDialog != null && subscribeDialog!!.isVisible)
            subscribeDialog!!.dismiss()
        subscribeDialog = SubscribeDialog(true)
        subscribeDialog!!.show(activity!!.supportFragmentManager, "subscribeDialog")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    fun refresh() {
        updateUI()
    }

}

package com.benAbdelWahed.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benAbdelWahed.R
import com.benAbdelWahed.adapters.HarajAdapter
import com.benAbdelWahed.adapters.HarajSubCatAdapter
import com.benAbdelWahed.fragments.AcceptHarajTermsDialog
import com.benAbdelWahed.fragments.CountryFilterDialog
import com.benAbdelWahed.fragments.SearchFilterDialog
import com.benAbdelWahed.responses.categories_response.CategoryResponse
import com.benAbdelWahed.responses.categories_response.SubCategory
import com.benAbdelWahed.responses.haraj_responses.DataItem
import com.benAbdelWahed.responses.haraj_responses.HarajPageProductsResponse
import com.benAbdelWahed.utils.CallbackRetrofit
import com.benAbdelWahed.utils.PrefManager
import com.benAbdelWahed.utils.RetrofitModel
import com.benAbdelWahed.utils.StaticMembers
import com.benAbdelWahed.utils.StaticMembers.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_haraj.*
import retrofit2.Call
import retrofit2.Response

class HarajActivity : AppCompatActivity() {

    private var isWaitingForData: Boolean = false
    private var carCatId: Int = 1
    lateinit var adapter: HarajAdapter
    lateinit var subCatAdapter: HarajSubCatAdapter
    var list = ArrayList<DataItem>()
    private var subCategoriesList = ArrayList<SubCategory>()
    var categoryResponse: CategoryResponse? = null
    var filters: HashMap<String, String> = HashMap()
    private lateinit var prefManager: PrefManager


    var page = 1
    var isLastPage = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val totalItemCount = recyclerView.layoutManager!!.itemCount
            val visibleItemCount =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastVisibleItemPosition()
            val firstVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()

            if (!isWaitingForData && !isLastPage
                    && visibleItemCount + firstVisibleItemPosition >= totalItemCount - 4
                    && firstVisibleItemPosition >= 0
            ) {
                loadMoreHaraj()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_haraj)
        setSupportActionBar(toolbar)
        backButton.setOnClickListener {
            onBackPressed()
        }
        filters[PER_PAGE]="10"
        prefManager = PrefManager.getInstance(this)
        cityFilter.setOnClickListener {
            CountryFilterDialog.getInstance(filters, object : CountryFilterDialog.OnActionListener {
                override fun onConfirm(filters: HashMap<String, String>, name: String) {
                    this@HarajActivity.filters = filters
                    cityFilter.text = name
                    getAllHaraj()
                }
            }).show(supportFragmentManager, "CountryFilterDialog")
        }
        searchFilterButton.setOnClickListener {
            SearchFilterDialog.getInstance().show(supportFragmentManager, "SearchFilterDialog")
        }
        addHaraj.setOnClickListener {
            if (prefManager.hasToken())
                AcceptHarajTermsDialog.getInstance().show(supportFragmentManager, "AcceptHarajTermsDialog")
            else showLoginDialog(this@HarajActivity)
        }
        adapter = HarajAdapter(this, list)
        {
            getAllHaraj()
        }
        recycler.adapter = adapter
        recycler.addOnScrollListener(scrollListener)

        subCatAdapter = HarajSubCatAdapter(this, subCategoriesList) {
            filters[SUBCATEGORY_ID] = "${it.id}"
            filters[CATEGORY_ID] = "$carCatId"
            filters.remove(SUBSUBCATEGORY_ID)
            subCategoriesTabLayout.visibility = GONE
            subCategoriesTabLayout2.visibility = GONE

            getAllHaraj()
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START, true)
        }
        subCategoryCarRecycler.adapter = subCatAdapter
        swipe.setOnRefreshListener {
            getAllHaraj()
        }
        getAllHaraj()
        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filters["text"] = searchEditText.text.toString()
                getAllHaraj()
            }
            return@setOnEditorActionListener false
        }

        carModelsButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START, true)
        }
        getAllCategories()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == EDIT_CODE)
                getAllHaraj()
    }

    fun updateCateoriesViews() {
        removeTabChildren(categoriesTabLayout)
        removeTabChildren(subCategoriesTabLayout)
        removeTabChildren(subCategoriesTabLayout2)

        var tab = categoriesTabLayout.newTab()
        tab.text = getString(R.string.all)
        categoriesTabLayout.addTab(tab,true)
        for (category in categoryResponse!!.data) {
            tab = categoriesTabLayout.newTab()
            tab.text = category.name
            categoriesTabLayout.addTab(tab,false)
            if ((category.name.contains("سيارات") || category.name.contains("car")) && category.subcategories != null) {
                subCategoriesList.clear()
                subCategoriesList.addAll(category.subcategories)
                subCatAdapter.notifyDataSetChanged()
                carCatId = category.id
            }
        }

        categoriesTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                filters.remove(CATEGORY_ID)
                filters.remove(SUBCATEGORY_ID)
                filters.remove(SUBSUBCATEGORY_ID)
                if (tab.position == 0) {
                    subCategoriesTabLayout.visibility = GONE
                    subCategoriesTabLayout2.visibility = GONE
                } else {
                    subCategoriesTabLayout.visibility = VISIBLE
                    if (categoryResponse!!.data != null && categoryResponse!!.data.isNotEmpty()) {
                        val category = categoryResponse!!.data[tab.position - 1]
                        filters[CATEGORY_ID] = "${category.id}"

                        removeTabChildren(subCategoriesTabLayout)
                        removeTabChildren(subCategoriesTabLayout2)

                        var tab1: TabLayout.Tab
                        tab1 = subCategoriesTabLayout.newTab()
                        tab1.text = getString(R.string.all)
                        subCategoriesTabLayout.addTab(tab1,false)
                        if (category.subcategories != null)
                            for (sub in category.subcategories) {
                                tab1 = subCategoriesTabLayout.newTab()
                                tab1.text = sub.name
                                subCategoriesTabLayout.addTab(tab1,false)
                            }
                    } else {
                        filters.remove(CATEGORY_ID)
                    }
                }
                getAllHaraj()
            }
        })

        subCategoriesTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                filters.remove(SUBSUBCATEGORY_ID)
                if (tab.position == 0) {
                    subCategoriesTabLayout2.visibility = GONE
                    filters.remove(SUBCATEGORY_ID)
                    if (subCategoriesTabLayout.tabCount>0)
                        getAllHaraj()
                } else {
                    subCategoriesTabLayout2.visibility = VISIBLE
                    if (categoriesTabLayout.selectedTabPosition > 0) {
                        val category = categoryResponse!!.data[categoriesTabLayout.selectedTabPosition - 1]
                        if (category.subcategories != null && category.subcategories.isNotEmpty()) {
                            val sub = category.subcategories[tab.position - 1]

                            removeTabChildren(subCategoriesTabLayout2)

                            var tab1: TabLayout.Tab
                            tab1 = subCategoriesTabLayout2.newTab()
                            tab1.text = getString(R.string.all)
                            subCategoriesTabLayout2.addTab(tab1,false)

                            filters[SUBCATEGORY_ID] = "${sub.id}"
                            if (sub.subsubs != null)
                                for (subSub in sub.subsubs) {
                                    tab1 = subCategoriesTabLayout2.newTab()
                                    tab1.text = subSub.name
                                    subCategoriesTabLayout2.addTab(tab1,false)
                                }
                        } else {
                            filters.remove(SUBCATEGORY_ID)
                        }
                    }
                    getAllHaraj()
                }
            }
        })
        subCategoriesTabLayout2.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    filters.remove("subsub_id")
                    if (subCategoriesTabLayout2.tabCount>0)
                        getAllHaraj()
                } else {
                    if (categoriesTabLayout.selectedTabPosition > 0) {
                        val category = categoryResponse!!.data[categoriesTabLayout.selectedTabPosition - 1]
                        if (category.subcategories != null && category.subcategories.isNotEmpty()) {
                            if (subCategoriesTabLayout.selectedTabPosition > 0) {
                                val sub = category.subcategories[subCategoriesTabLayout.selectedTabPosition - 1]
                                if (sub.subsubs != null && sub.subsubs.isNotEmpty()) {
                                    val subSubCat = sub.subsubs[tab.position - 1]
                                    filters["subsub_id"] = "${subSubCat.id}"
                                } else {
                                    filters.remove("subsub_id")
                                }
                            }
                        }
                    }
                    getAllHaraj()
                }
            }
        })
    }

    fun removeTabChildren(tabLayout: TabLayout) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
            }
        })
        tabLayout.removeAllTabs()
    }

    fun getAllHaraj()
    {
        page = 1
        loadMoreHaraj()
    }
    fun loadMoreHaraj() {
        if (!isWaitingForData) {
            filters[PAGE] = "$page"
            adapter.setShowLoading(true)
            swipe.isRefreshing = true
            errorMessage.visibility = GONE
            isWaitingForData = true
            val call = RetrofitModel.getApi(this).getAllProductsPagination(filters)
            call.enqueue(object : CallbackRetrofit<HarajPageProductsResponse>(this) {
                override fun onResponse(call: Call<HarajPageProductsResponse>, response: Response<HarajPageProductsResponse>) {
                    swipe.isRefreshing = false
                    adapter.setShowLoading(false)
                    isWaitingForData = false
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        isLastPage = !result.data.meta.hasMorePages!!
                        if (page == 1)
                            list.clear()
                        list.addAll(result.data.data)
                        adapter.notifyDataSetChanged()
                        page++
                        errorMessage.visibility = if (list.isEmpty()) VISIBLE else GONE
                    } else {
                        toastMessageShort(this@HarajActivity, R.string.connection_error)
                    }

                }

                override fun onFailure(call: Call<HarajPageProductsResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    swipe.isRefreshing = false
                    adapter.setShowLoading(false)
                    isWaitingForData = false
                    getAllHaraj()
                }
            })
        }
    }

    fun getAllCategories() {
        categoryResponse = prefManager.getObject(CATEGORIES, CategoryResponse::class.java) as CategoryResponse?
        if (categoryResponse != null && categoryResponse!!.data != null) {
            updateCateoriesViews()
        }
        adapter.setShowLoading(true)
        val call = RetrofitModel.getApi(this).allCategories
        call.enqueue(object : CallbackRetrofit<CategoryResponse>(this) {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                swipe.isRefreshing = false
                adapter.setShowLoading(false)
                list.clear()
                val result = response.body()
                if (response.isSuccessful && result != null) {
                    categoryResponse = result
                    updateCateoriesViews()
                    prefManager.setObject(CATEGORIES, categoryResponse)
                } else {
                    toastMessageShort(this@HarajActivity, R.string.connection_error)
                }

            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                super.onFailure(call, t)
                swipe.isRefreshing = false
                adapter.setShowLoading(false)
            }
        })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()

    }
}

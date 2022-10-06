package com.benAbdelWahed.utils

import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Html
import android.view.View
import android.view.View.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.benAbdelWahed.adapters.ImagePagerAdapter
import com.chahinem.pageindicator.PageIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("onBackListener")
fun onBackPressed(toolbar: Toolbar, lambda: (() -> Unit)?) {
    toolbar.setNavigationOnClickListener {
        lambda?.invoke()
    }
}

@BindingAdapter(value = ["tabLayout", "tabTitles", "adapter"], requireAll = true)
fun setUpWithViewpager(
        viewPager2: ViewPager2,
        tabLayout: TabLayout,
        titles: List<Int>,
        adapter: FragmentStateAdapter
) {

    viewPager2.adapter = adapter
    TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
        if (titles.size > position)
            tab.text = tabLayout.context.getString(titles[position])
    }.attach()
}

@BindingAdapter("adapter")
fun setRecyclerAdapter(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
) {
    recyclerView.adapter = adapter
}

@BindingAdapter("app:visibleOrGone")
fun visibleOrGone(
        view: View,
        isVisible: Boolean
) {
    view.visibility = if (isVisible) VISIBLE else GONE
}

@BindingAdapter("app:visible")
fun isVisible(
        view: View,
        isVisible: Boolean
) {
    view.visibility = if (isVisible) VISIBLE else INVISIBLE
}

@BindingAdapter("app:errorRes")
fun setRecyclerAdapter(
        textInputLayout: TextInputLayout,
        strId: Int
) {
    if (strId == 0)
        textInputLayout.error = null
    else textInputLayout.error = textInputLayout.context.getString(strId)
}

@BindingAdapter(value = ["app:url", "app:placeHolder", "app:errorHolder"], requireAll = false)
fun setImageUrl(
        imageView: ImageView,
        url: String?,
        placeholder: Drawable?,
        error: Drawable?
) {
    if (!url.isNullOrBlank())
        imageView.load(url) {
            placeholder(placeholder)
            error(error)
        }
    else imageView.setImageDrawable(error)
}

@BindingAdapter("app:resId")
fun setImageRes(
        imageView: ImageView,
        resId: Int,
) {
    imageView.setImageResource(resId)
}

@BindingAdapter("app:textRes")
fun setImageRes(
        textView: TextView,
        textRes: Int,
) {
    if (textRes != 0)
        textView.setText(textRes)
}

@BindingAdapter("recyclerScroll")
fun recyclerScrollListener(
        recyclerView: RecyclerView,
        scrollListener: RecyclerView.OnScrollListener
) {
    recyclerView.addOnScrollListener(scrollListener)
}

@BindingAdapter("nestedScroll")
fun recyclerScrollListener(
        nestedScrollView: NestedScrollView,
        scrollListener: NestedScrollView.OnScrollChangeListener
) {
    nestedScrollView.setOnScrollChangeListener(scrollListener)
}

@BindingAdapter(
        value = ["swipeRefresh", "isRefreshing"],
        requireAll = false
)
fun swipeToRefresh(
        swipeRefreshLayout: SwipeRefreshLayout,
        refreshListener: SwipeRefreshLayout.OnRefreshListener,
        isRefreshing: Boolean = false
) {
    swipeRefreshLayout.setOnRefreshListener(refreshListener)
    swipeRefreshLayout.isRefreshing = isRefreshing
}

@BindingAdapter("editorAction")
fun textEditorAction(
        textView: TextView,
        onEditorActionListener: TextView.OnEditorActionListener
) {
    textView.setOnEditorActionListener(onEditorActionListener)
}

@BindingAdapter("requestFocus")
fun requestFocus(
        textView: TextView,
        isRequestFocus: Boolean
) {
    if (isRequestFocus)
        textView.requestFocus()
}


@BindingAdapter("onCheckChange")
fun onCheckChange(
        compoundButton: CompoundButton,
        listener: ((pressed: Boolean, checked: Boolean) -> Unit)?
) {
    compoundButton.setOnCheckedChangeListener { buttonView, isChecked ->
        listener?.invoke(buttonView.isPressed, isChecked)
    }
}


@BindingAdapter("spinnerAdapter")
fun spinnerTextAdapter(
        spinner: Spinner,
        adapter: ArrayAdapter<String>
) {
    spinner.adapter = adapter
}

@BindingAdapter("onSpinnerItemClick")
fun spinnerTextItemClick(
        spinner: Spinner,
        onItemClickListener: AdapterView.OnItemSelectedListener
) {
    spinner.onItemSelectedListener = onItemClickListener
}

@BindingAdapter("spinnerSelection")
fun spinnerTextAdapter(
        spinner: Spinner,
        index: Int
) {
    spinner.setSelection(index)
}

@BindingAdapter("android:layout_width")
fun setWidth(view: View, isMatchParent: Boolean) {
    val params = view.layoutParams
    params.width = if (isMatchParent) MATCH_PARENT else WRAP_CONTENT
    view.layoutParams = params
}

@BindingAdapter("textWatcher")
fun setTextWatcher(editText: EditText, textListener: (Editable?) -> Unit) {
    editText.addTextChangedListener {
        textListener(it)
    }
}

@BindingAdapter("selection")
fun setTextSelection(editText: EditText, selection: Int) {
    editText.post {
        editText.setSelection(editText.length())
    }
}

@BindingAdapter(value = ["imagePagerAdapter", "indicator"], requireAll = false)
fun setImagePagerAdapter(viewPager: ViewPager, pagerAdapter: ImagePagerAdapter, pageIndicator: PageIndicator?) {
    pagerAdapter.setContext(viewPager.context)
    pagerAdapter.setPreviewPager(viewPager)
    viewPager.adapter = pagerAdapter
    pageIndicator?.attachTo(viewPager)
}

@BindingAdapter(value = ["textHtml", "param"], requireAll = false)
fun textHtml(textView: TextView, format: String, param: String?) {
    textView.text =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                Html.fromHtml(String.format(format, param), Html.FROM_HTML_MODE_COMPACT)
            else Html.fromHtml(String.format(format, param))
}
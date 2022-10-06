package com.benAbdelWahed.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.benAbdelWahed.R;
import com.bumptech.glide.Glide;
import com.stfalcon.imageviewer.StfalconImageViewer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {
    private Context context;
    private List<String> list= new ArrayList<>();
    private ViewPager previewPager;

    public ImagePagerAdapter(Context context, ViewPager previewPager, List<String> list) {
        this.context = context;
        this.list = list;
        this.previewPager = previewPager;
    }

    public ImagePagerAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<String> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setPreviewPager(ViewPager previewPager) {
        this.previewPager = previewPager;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view == ((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_pager, container, false);
        ImageView imageView = view.findViewById(R.id.image);
        Glide.with(container.getContext()).load(list.get(position))
                .placeholder(R.drawable.place_holder_logo).error(R.drawable.place_holder_logo).fitCenter().into(imageView);
        imageView.setOnClickListener(v -> {
            new StfalconImageViewer.Builder<>(context, list,
                    (imageView1, image) -> Glide.with(imageView1.getContext()).load(image).into(imageView1))
                    .withTransitionFrom(imageView)
                    .withHiddenStatusBar(true)
                    .withStartPosition(position)
                    .withImageChangeListener(position1 -> {
                        if (previewPager != null)
                            previewPager.setCurrentItem(position1);
                    })
                    .show();
        });
        container.addView(view);
        return view;
    }

    @Override
    public int getItemPosition(@NotNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }
}

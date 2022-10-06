package com.benAbdelWahed.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.activities.BannerDetailsActivity;
import com.benAbdelWahed.responses.banners_responses.Banner;
import com.benAbdelWahed.utils.StaticMembers;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.Holder> {
    private FragmentActivity activity;
    private List<Banner> list;

    public BannerAdapter(FragmentActivity activity, List<Banner> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_banner, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView image;

        Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind() {
            int p = getAdapterPosition();
            Banner item = list.get(p);
            if (item.getImage() != null)
                Glide.with(activity).load(item.getImage()).centerCrop().placeholder(R.drawable.place_holder_logo).error(R.drawable.place_holder_logo).into(image);

            itemView.setOnClickListener(v -> {
                if (item.getType().equals("internal")) {
                    Intent intent = new Intent(activity, BannerDetailsActivity.class);
                    intent.putExtra(StaticMembers.BANNER, item);
                    activity.startActivity(intent);
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(item.getLink()));
                    activity.startActivity(i);
                }
            });

        }
    }
}

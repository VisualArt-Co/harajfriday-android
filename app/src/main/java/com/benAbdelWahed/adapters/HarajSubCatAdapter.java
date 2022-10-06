package com.benAbdelWahed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.responses.categories_response.SubCategory;
import com.benAbdelWahed.utils.PrefManager;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HarajSubCatAdapter extends RecyclerView.Adapter<HarajSubCatAdapter.Holder> {
    private FragmentActivity activity;
    private List<SubCategory> list;
    private PrefManager prefManager;
    private OnItemClickedListener listener;

    public HarajSubCatAdapter(FragmentActivity activity, List<SubCategory> list, OnItemClickedListener listener) {
        this.activity = activity;
        this.list = list;
        this.listener = listener;
        prefManager = PrefManager.getInstance(activity);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_car_filter, parent, false));
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
        @BindView(R.id.name)
        TextView name;

        Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind() {
            int p = getAdapterPosition();
            SubCategory item = list.get(p);
            itemView.setOnClickListener(v -> {
                listener.onItemClicked(item);
            });
            name.setText(item.getName());
            if (item.getImage() != null)
                Glide.with(activity).load(item.getImage()).centerCrop().placeholder(R.drawable.place_holder_logo).error(R.drawable.place_holder_logo).into(image);
        }
    }

    public interface OnItemClickedListener {
        void onItemClicked(SubCategory item);
    }
}

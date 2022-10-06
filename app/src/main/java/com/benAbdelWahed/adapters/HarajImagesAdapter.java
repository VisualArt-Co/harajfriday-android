package com.benAbdelWahed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.responses.haraj_responses.ImagesItem;
import com.benAbdelWahed.utils.PrefManager;
import com.bumptech.glide.Glide;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HarajImagesAdapter extends RecyclerView.Adapter<HarajImagesAdapter.Holder> {
    private FragmentActivity activity;
    private List<ImagesItem> list;
    private PrefManager prefManager;

    public HarajImagesAdapter(FragmentActivity activity, List<ImagesItem> list) {
        this.activity = activity;
        this.list = list;
        prefManager = PrefManager.getInstance(activity);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_haraj_image, parent, false));
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
            ImagesItem item = list.get(p);
            if (item.getImage() != null && !item.getImage().isEmpty())
                Glide.with(itemView.getContext()).load(item.getImage()).centerCrop().placeholder(R.drawable.place_holder_logo).error(R.drawable.place_holder_logo).into(image);
            itemView.setOnClickListener(v ->
                    new StfalconImageViewer.Builder<>(activity, list,
                            (imageView1, image) -> {
                                if (image != null && !image.getImage().isEmpty())
                                    Glide.with(imageView1.getContext()).load(image.getImage())
                                            .placeholder(R.drawable.place_holder_logo).error(R.drawable.place_holder_logo).into(imageView1);
                            })
                            .withTransitionFrom(image)
                            .withHiddenStatusBar(true)
                            .withStartPosition(p)
                            .show());
        }
    }
}

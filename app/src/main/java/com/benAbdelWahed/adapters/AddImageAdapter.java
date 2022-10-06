package com.benAbdelWahed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.utils.StaticMembers;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddImageAdapter extends RecyclerView.Adapter<AddImageAdapter.Holder> {
    private FragmentActivity activity;
    private List<String> paths;
    private int limit;

    public AddImageAdapter(FragmentActivity activity, List<String> paths, int limit) {
        this.activity = activity;
        this.paths = paths;
        this.limit = limit;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_image_mazad, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.bindView();
    }

    public void remove(int position) {
        paths.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, paths.size() + 1);
    }

    @Override
    public int getItemCount() {
        return limit;
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.cancel)
        ImageView cancel;
        @BindView(R.id.cardView)
        CardView cardView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView() {
            int i = getAdapterPosition();
            cancel.setVisibility(i < paths.size() ? View.VISIBLE : View.GONE);
            if (i < paths.size()) {
                cancel.setOnClickListener(v -> remove(i));
                String path = paths.get(i);
                Picasso.get().load("file://" + path).fit().centerCrop().into(image);
            } else {
                image.setImageResource(R.drawable.ic_add_image);
                image.setScaleType(ImageView.ScaleType.CENTER);
                itemView.setOnClickListener(v -> {
                    Options options = Options.init()
                            .setRequestCode(StaticMembers.PIX_CODE)                                                 //Request code for activity results
                            .setCount(paths.size() < limit ? limit - paths.size() : 0)                                                    //Number of images to restict selection count
                            .setFrontfacing(false)                                                //Front Facing camera on start

                            //.setPreSelectedUrls(returnValue)                                     //Pre selected Image Urls
                            .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)           //Orientaion
                            .setPath(StaticMembers.IMAGES_PATH);                                          //Custom Path For Image Storage
                    if (paths.size() < limit)
                        Pix.start(activity, options);
                });
            }
        }
    }
}

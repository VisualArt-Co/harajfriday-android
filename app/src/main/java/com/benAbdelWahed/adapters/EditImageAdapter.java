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
import com.benAbdelWahed.responses.haraj_responses.ImagesItem;
import com.benAbdelWahed.utils.StaticMembers;
import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditImageAdapter extends RecyclerView.Adapter<EditImageAdapter.Holder> {
    private FragmentActivity activity;
    private List<String> paths;
    private List<ImagesItem> imageList;
    private List<Integer> deletedList;
    private int limit;

    public EditImageAdapter(FragmentActivity activity, List<ImagesItem> imageList, List<String> paths, int limit) {
        this.activity = activity;
        this.paths = paths;
        this.imageList = imageList;
        this.limit = limit;
        deletedList = new ArrayList<>();
    }

    public List<Integer> getDeletedList() {
        return deletedList;
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
            cancel.setVisibility(i < paths.size() + imageList.size() ? View.VISIBLE : View.GONE);

            if (i < paths.size() + imageList.size()) {
                cardView.setOnClickListener(v -> {
                });
                if (i < imageList.size()) {
                    ImagesItem imageItem = imageList.get(i);
                    Glide.with(activity).load(imageItem.getImage()).centerCrop().into(image);
                    cancel.setOnClickListener(v -> {
                        deletedList.add(imageItem.getId());
                        imageList.remove(i);
                        notifyItemRemoved(i);
                        notifyItemRangeChanged(i, getItemCount());
                    });
                } else {

                    int j = Math.abs(imageList.size() - i);
                    String path = paths.get(j);
                    Picasso.get().load("file://" + path).fit().centerCrop().into(image);
                    cancel.setOnClickListener(v -> {
                        paths.remove(j);
                        notifyItemRemoved(j);
                        notifyItemRangeChanged(j, getItemCount());
                    });
                }
            } else {
                cancel.setOnClickListener(v -> {
                });
                image.setImageResource(R.drawable.ic_add_image);
                image.setScaleType(ImageView.ScaleType.CENTER);
                cardView.setOnClickListener(v -> {
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

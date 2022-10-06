package com.benAbdelWahed.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.activities.ProductOwnerProfileActivity;
import com.benAbdelWahed.responses.haraj_responses.AllRatesItem;
import com.benAbdelWahed.utils.StaticMembers;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HarajReviewsAdapter extends RecyclerView.Adapter<HarajReviewsAdapter.Holder> {
    private FragmentActivity activity;
    private List<AllRatesItem> list;

    public HarajReviewsAdapter(FragmentActivity activity, List<AllRatesItem> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_haraj_review, parent, false));
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

        @BindView(R.id.userName)
        TextView userName;
        @BindView(R.id.dateText)
        TextView dateText;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.verifyImage)
        ImageView verifyImage;
        @BindView(R.id.likeCheckBox)
        CheckBox likeCheckBox;

        Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind() {
            int p = getAdapterPosition();
            AllRatesItem item = list.get(p);
            userName.setText(item.getRateOwner().getUser_name());
            verifyImage.setVisibility(item.getRateOwner().isPremium() ? View.VISIBLE : View.GONE);
            dateText.setText(StaticMembers.getDateFromBackend(item.getCreatedAt()));
            description.setText(item.getComment());
            likeCheckBox.setChecked(item.getRate().equals("1"));
            userName.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ProductOwnerProfileActivity.class);
                intent.putExtra(StaticMembers.CUSTOMER, item.getRateOwner());
                activity.startActivity(intent);
            });
        }
    }
}

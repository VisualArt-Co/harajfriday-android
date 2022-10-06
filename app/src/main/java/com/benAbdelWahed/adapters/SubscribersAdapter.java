package com.benAbdelWahed.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.responses.subscribers_response.DataItem;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubscribersAdapter extends RecyclerView.Adapter<SubscribersAdapter.Holder> {
    private FragmentActivity activity;
    private List<DataItem> list;

    public SubscribersAdapter(FragmentActivity activity, List<DataItem> paths) {
        this.activity = activity;
        this.list = paths;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_subscribers, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        holder.bindView();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.price)
        TextView price;
        @BindView(R.id.highestPrice)
        TextView highestPrice;

        public Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView() {
            int i = getAdapterPosition();
            DataItem path = list.get(i);
            if (i == 0) {
                highestPrice.setVisibility(View.VISIBLE);
            } else {
                highestPrice.setVisibility(View.GONE);
            }
            name.setText(path.getFullName());
            price.setText(String.format(Locale.getDefault(), activity.getString(R.string._rial), path.getPrice()));
        }
    }
}

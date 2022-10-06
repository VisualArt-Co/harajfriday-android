package com.benAbdelWahed.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.fragments.AboutAppDialog;
import com.benAbdelWahed.responses.other_response.DataItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherAdapter extends RecyclerView.Adapter<OtherAdapter.Holder> {
    private FragmentActivity activity;
    private List<DataItem> list;

    public OtherAdapter(FragmentActivity activity, List<DataItem> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_other, parent, false));
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

        @BindView(R.id.name)
        TextView name;

        Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind() {
            int i = getAdapterPosition();
            DataItem dataItem = list.get(i);
            name.setText(Html.fromHtml(dataItem.getName()));
            itemView.setOnClickListener(view -> {
                AboutAppDialog.getInstance(dataItem.getDescription(), dataItem.getName()).show(activity.getSupportFragmentManager(), "AboutAppDialog");
            });
        }
    }

    public interface OnActionListener {
        void onRefresh();
    }
}

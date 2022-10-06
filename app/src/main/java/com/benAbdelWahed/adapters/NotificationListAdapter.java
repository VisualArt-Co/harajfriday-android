package com.benAbdelWahed.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.activities.HarajDetailsActivity;
import com.benAbdelWahed.activities.MazadDetailsActivity;
import com.benAbdelWahed.responses.notification_responses.NotificationItem;
import com.benAbdelWahed.utils.PrefManager;
import com.benAbdelWahed.utils.StaticMembers;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.Holder> {
    private FragmentActivity activity;
    private List<NotificationItem> list;

    public NotificationListAdapter(FragmentActivity activity, List<NotificationItem> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_notification, parent, false));
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

        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.date)
        TextView date;

        Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind() {
            int i = getAdapterPosition();
            NotificationItem notificationItem = list.get(i);
            text.setText(notificationItem.getMessage());
            date.setText(StaticMembers.getDateFromBackend(notificationItem.getCreatedAt()));
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent();
                if (notificationItem.getNotificationType().equals("mazad")) {
                    intent = new Intent(activity, MazadDetailsActivity.class);
                    intent.putExtra(StaticMembers.MAZAD_ID, notificationItem.getMazadId());
                } else if (notificationItem.getNotificationType().equals("haraj")) {
                    intent = new Intent(activity, HarajDetailsActivity.class);
                    intent.putExtra(StaticMembers.HARAJ_ID, notificationItem.getProductId());
                }
                activity.startActivity(intent);

            });
          /*  name.setText(notificationItem.getName());
            if (notificationItem.getLastMessage() != null) {
                lastMessage.setText(notificationItem.getLastMessage().getText());
                if (notificationItem.getAvatar() != null && !notificationItem.getAvatar().isEmpty())
                    Picasso.get().load(notificationItem.getAvatar()).placeholder(R.drawable.place_holder_logo).fit().centerCrop().into(userImage);
                else userImage.setImageResource(R.drawable.place_holder_logo);
            }

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(activity, ChatRoomActivity.class);
                intent.putExtra(StaticMembers.OTHER_ID,notificationItem.getId());
                activity.startActivity(intent);
            });*/
        }
    }

    public interface OnActionListener {
        void onRefresh();
    }
}

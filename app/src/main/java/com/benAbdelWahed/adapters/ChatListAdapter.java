package com.benAbdelWahed.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.activities.ChatRoomActivity;
import com.benAbdelWahed.responses.chat_responses.UserItem;
import com.benAbdelWahed.utils.PrefManager;
import com.benAbdelWahed.utils.StaticMembers;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {
    private FragmentActivity activity;
    private List<UserItem> list;
    private PrefManager prefManager;

    public ChatListAdapter(FragmentActivity activity, List<UserItem> list) {
        this.activity = activity;
        this.list = list;
        prefManager = PrefManager.getInstance(activity);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_chat_user, parent, false));
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
        @BindView(R.id.lastMessage)
        TextView lastMessage;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.userImage)
        ImageView userImage;

        Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind() {
            int i = getAdapterPosition();
            UserItem user = list.get(i);
            userName.setText(user.getUser_name());
            if (user.getLastMessage() != null) {
                lastMessage.setText(user.getLastMessage().getText());
                date.setText(StaticMembers.getDateFromBackend(user.getLastMessage().getCreatedAt()));
                if (user.getAvatar() != null && !user.getAvatar().isEmpty())
                    Glide.with(activity).load(user.getAvatar()).centerCrop().placeholder(R.drawable.place_holder_logo_small).error(R.drawable.place_holder_logo_small).into(userImage);
            }

            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(activity, ChatRoomActivity.class);
                intent.putExtra(StaticMembers.OTHER_ID, user.getId());
                activity.startActivity(intent);
            });
        }
    }

    public interface OnActionListener {
        void onRefresh();
    }
}

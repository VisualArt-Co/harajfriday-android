package com.benAbdelWahed.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.responses.chat_responses.Message;
import com.benAbdelWahed.responses.haraj_responses.Customer;
import com.benAbdelWahed.utils.PrefManager;

import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.benAbdelWahed.utils.StaticMembers.PROFILE;
import static com.benAbdelWahed.utils.StaticMembers.getDateFromBackend;
import static com.benAbdelWahed.utils.StaticMembers.toastMessageShort;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.Holder> {
    private FragmentActivity activity;
    private List<Message> list;
    private PrefManager prefManager;
    private Customer customer;
    private ClipboardManager clipboard;

    public ChatRoomAdapter(FragmentActivity activity, List<Message> list) {
        this.activity = activity;
        this.list = list;
        prefManager = PrefManager.getInstance(activity);
        customer = (Customer) prefManager.getObject(PROFILE, Customer.class);
        clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_message, parent, false));
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

        @BindView(R.id.myMessage)
        CardView myMessage;
        @BindView(R.id.otherMessage)
        CardView otherMessage;
        @BindView(R.id.messageBody)
        TextView messageBody;
        @BindView(R.id.messageBodyOther)
        TextView messageBodyOther;
        @BindView(R.id.myDate)
        TextView myDate;
        @BindView(R.id.otherDate)
        TextView otherDate;

        Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind() {
            int i = getAdapterPosition();
            Message message = list.get(i);
            boolean isMe = customer.getId() == message.getUserId();
            myMessage.setVisibility(isMe ? View.VISIBLE : View.GONE);
            otherMessage.setVisibility(isMe ? View.GONE : View.VISIBLE);
            messageBody.setText(message.getText());
            messageBodyOther.setText(message.getText());
            myDate.setText(getDateFromBackend(message.getCreatedAt()));
            otherDate.setText(getDateFromBackend(message.getCreatedAt()));
            myMessage.setOnLongClickListener(v -> {
                ClipData clip = ClipData.newPlainText(messageBody.getText().toString(), messageBody.getText().toString());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                toastMessageShort(activity, R.string.copied_clipboard);
                return true;
            });

            addTheMasks(messageBody);
            addTheMasks(messageBodyOther);
            otherMessage.setOnLongClickListener(v -> {
                ClipData clip = ClipData.newPlainText(messageBodyOther.getText().toString(), messageBodyOther.getText().toString());
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                toastMessageShort(activity, R.string.copied_clipboard);
                return true;
            });
        }


        void addTheMasks(TextView textView) {
            Linkify.TransformFilter filter = (match, url) -> url.replaceAll("/", "");
            Pattern pattern = Pattern.compile("\\+?\\d+");
            Linkify.addLinks(textView, Linkify.WEB_URLS);
            Linkify.addLinks(textView, pattern, "tel:", null, filter);
        }
    }
}

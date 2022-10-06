package com.benAbdelWahed.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.benAbdelWahed.R;
import com.benAbdelWahed.activities.ProductOwnerProfileActivity;
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse;
import com.benAbdelWahed.responses.haraj_responses.Comment;
import com.benAbdelWahed.responses.haraj_responses.Customer;
import com.benAbdelWahed.utils.CallbackRetrofit;
import com.benAbdelWahed.utils.PrefManager;
import com.benAbdelWahed.utils.RetrofitModel;
import com.benAbdelWahed.utils.StaticMembers;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import static com.benAbdelWahed.utils.StaticMembers.CUSTOMER;
import static com.benAbdelWahed.utils.StaticMembers.PROFILE;
import static com.benAbdelWahed.utils.StaticMembers.checkTextInputEditText;
import static com.benAbdelWahed.utils.StaticMembers.toastMessageShort;

public class HarajCommentsAdapter extends RecyclerView.Adapter<HarajCommentsAdapter.Holder> {
    private FragmentActivity activity;
    private List<Comment> list;
    private PrefManager prefManager;
    private int myId, ownerId;

    public HarajCommentsAdapter(FragmentActivity activity, List<Comment> list, int ownerId) {
        this.activity = activity;
        this.list = list;
        prefManager = PrefManager.getInstance(activity);
        Customer customer = (Customer) prefManager.getObject(PROFILE, Customer.class);
        if (customer != null) {
            myId = customer.getId();
        }
        this.ownerId = ownerId;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(activity).inflate(R.layout.item_haraj_comment, parent, false));
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
        @BindView(R.id.reportImageView)
        ImageView reportImageView;
        @BindView(R.id.verifyImage)
        ImageView verifyImage;
        @BindView(R.id.deleteComment)
        ImageView deleteComment;

        Holder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind() {
            int p = getAdapterPosition();
            Comment item = list.get(p);
            reportImageView.setVisibility((myId == item.getCustomer().getId()) ? View.INVISIBLE : View.VISIBLE);
            userName.setText(item.getCustomer().getUser_name());
            verifyImage.setVisibility(item.getCustomer().isPremium()?View.VISIBLE:View.GONE);
            userName.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ProductOwnerProfileActivity.class);
                intent.putExtra(CUSTOMER, item.getCustomer());
                activity.startActivity(intent);
            });
            dateText.setText(StaticMembers.getDateFromBackend(item.getCreatedAt()));
            description.setText(item.getComment());
            reportImageView.setOnClickListener(v -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle(activity.getString(R.string.sure_report_comment));
                View view = LayoutInflater.from(activity).inflate(R.layout.fg_report_text, null, false);
                TextInputEditText editText = view.findViewById(R.id.editText);
                TextInputLayout textInputLayout = view.findViewById(R.id.textInputLayout);
                alertDialog.setView(view);
                alertDialog.setPositiveButton(activity.getString(R.string.report), (dialog, which) -> sendReport(item.getId(), editText, textInputLayout));
                alertDialog.setNegativeButton(activity.getString(R.string.cancel), null);
                alertDialog.show();
            });

            deleteComment.setVisibility((myId == item.getCustomer().getId() || myId == ownerId) ? View.VISIBLE : View.GONE);
            deleteComment.setOnClickListener(v -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
                alertDialog.setTitle(activity.getString(R.string.sure_delete_comment));
                alertDialog.setPositiveButton(activity.getString(R.string.delete), (dialog, which) -> deleteCommentNow(p));
                alertDialog.setNegativeButton(activity.getString(R.string.cancel), null);
                alertDialog.show();
            });
        }
    }

    private void deleteCommentNow(int index) {
        int commentId = list.get(index).getId();
        Call<AddIncResponse> call = RetrofitModel.getApi(activity).removeComment(commentId);
        call.enqueue(new CallbackRetrofit<AddIncResponse>(activity) {
            @Override
            public void onResponse(@NotNull Call<AddIncResponse> call, @NotNull Response<AddIncResponse> response) {
                AddIncResponse result = response.body();
                if (response.isSuccessful() && result != null) {
                    toastMessageShort(activity, R.string.success_delete_comment);
                    activity.setResult(Activity.RESULT_OK);
                    list.remove(index);
                    notifyItemRemoved(index);
                    notifyItemRangeChanged(index, getItemCount());
                } else {
                    StaticMembers.checkError(activity, response.errorBody());
                }

            }

            @Override
            public void onFailure(@NotNull Call<AddIncResponse> call, @NotNull Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    private void sendReport(int id, TextInputEditText s, TextInputLayout textInputLayout) {
        if (checkTextInputEditText(s, textInputLayout, activity.getString(R.string.problem_req))) {
            Call<AddIncResponse> call = RetrofitModel.getApi(activity).reportComment(id, s.getText().toString());
            call.enqueue(new CallbackRetrofit<AddIncResponse>(activity) {
                @Override
                public void onResponse(@NotNull Call<AddIncResponse> call, @NotNull Response<AddIncResponse> response) {
                    AddIncResponse result = response.body();
                    if (response.isSuccessful() && result != null) {
                        activity.setResult(Activity.RESULT_OK);
                        toastMessageShort(activity, R.string.success_report_comment);
                    } else {
                        StaticMembers.checkError(activity, response.errorBody());
                    }

                }

                @Override
                public void onFailure(@NotNull Call<AddIncResponse> call, @NotNull Throwable t) {
                    super.onFailure(call, t);
                }
            });
        }
    }
}

package com.benAbdelWahed.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.benAbdelWahed.R;
import com.benAbdelWahed.models.PremiumModel;
import com.benAbdelWahed.responses.add_inc_response.AddIncResponse;
import com.benAbdelWahed.responses.haraj_responses.Customer;
import com.benAbdelWahed.responses.settings_response.SettingsData;
import com.benAbdelWahed.utils.CallbackRetrofit;
import com.benAbdelWahed.utils.PrefManager;
import com.benAbdelWahed.utils.RetrofitModel;
import com.benAbdelWahed.utils.StaticMembers;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.benAbdelWahed.utils.StaticMembers.IS_PREMIUM;
import static com.benAbdelWahed.utils.StaticMembers.PROFILE;
import static com.benAbdelWahed.utils.StaticMembers.SETTINGS;
import static com.benAbdelWahed.utils.StaticMembers.checkTextInputEditText;
import static com.benAbdelWahed.utils.StaticMembers.showLoginDialog;

public class SubscribePremiumDialog extends DialogFragment {

    public static SubscribePremiumDialog getInstance(OnSubscribeListener onSubscribeListener) {
        SubscribePremiumDialog subscribePremiumDialog = new SubscribePremiumDialog();
        subscribePremiumDialog.onSubscribeListener = onSubscribeListener;
        return subscribePremiumDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTrans);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_membership, container, false);
    }

    @BindView(R.id.subscribe)
    CardView subscribe;
    @BindView(R.id.subscribeText)
    TextView subscribeText;
    @BindView(R.id.rulesText)
    TextView rulesText;
    @BindView(R.id.usernameEditText)
    TextInputEditText usernameEditText;
    @BindView(R.id.bankNoEditText)
    TextInputEditText bankNoEditText;
    @BindView(R.id.usernameLayout)
    TextInputLayout usernameLayout;
    @BindView(R.id.bankNoLayout)
    TextInputLayout bankNoLayout;
    @BindView(R.id.backButton)
    ImageView backButton;
    private PrefManager prefManager;
    private Customer myUser;
    private OnSubscribeListener onSubscribeListener;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        prefManager = PrefManager.getInstance(getContext());
        boolean isPremium = prefManager.getBoolean(IS_PREMIUM);
        myUser = (Customer) prefManager.getObject(PROFILE, Customer.class);
        subscribe.setVisibility(isPremium ? GONE : VISIBLE);
        subscribe.setOnClickListener(this::done);
        if (myUser != null)
            if (myUser.getCheckPremuimStatus() == null) {
                subscribeText.setText(R.string.subscribe);
                subscribe.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
            } else {
                subscribe.setEnabled(false);
                subscribeText.setText(myUser.getCheckPremuimStatus().equals("pendding") ? R.string.pending_review : R.string.done_sub);
                subscribe.setCardBackgroundColor(getResources().getColor(myUser.getCheckPremuimStatus().equals("pendding") ? R.color.red : R.color.green));
            }
        backButton.setOnClickListener(v -> dismiss());
        SettingsData data = (SettingsData) prefManager.getObject(SETTINGS, SettingsData.class);
        rulesText.setText(Html.fromHtml(data.getConditionsParticipationInPremiumMembership()));
    }

    public interface OnSubscribeListener {
        void onSubscribe();
    }

    void done(View v) {
        if (myUser == null) {
            showLoginDialog(requireActivity());
            return;
        }
        checkTextInputEditText(usernameEditText, usernameLayout, getString(R.string.sender_name_req));
        checkTextInputEditText(bankNoEditText, bankNoLayout, getString(R.string.bankNo_req));

        if (
                checkTextInputEditText(usernameEditText, usernameLayout, getString(R.string.sender_name_req)) &&
                        checkTextInputEditText(bankNoEditText, bankNoLayout, getString(R.string.bankNo_req))
        ) {
            PremiumModel premiumModel = new PremiumModel(usernameEditText.getText().toString(), bankNoEditText.getText().toString());
            Call<AddIncResponse> call = RetrofitModel.getApi(getContext()).upgradeToPremium(premiumModel);
            call.enqueue(new CallbackRetrofit<AddIncResponse>(getContext()) {
                @Override
                public void onResponse(@NotNull Call<AddIncResponse> call, @NotNull Response<AddIncResponse> response) {
                    if (response.isSuccessful()) {
                        StaticMembers.toastMessageShort(getActivity(), R.string.done_sending_premium_request);
                        myUser.setCheckPremuimStatus("pendding");
                        prefManager.setObject(PROFILE, myUser);
                        if (onSubscribeListener != null)
                            onSubscribeListener.onSubscribe();
                        dismiss();
                    } else {
                        StaticMembers.checkError(getActivity(), response.errorBody());
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

package com.benAbdelWahed.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.benAbdelWahed.utils.StaticMembers.CONDITIONS;
import static com.benAbdelWahed.utils.StaticMembers.getTextHTML;

import android.os.Bundle;
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
import com.benAbdelWahed.responses.conditions_response.ConditionResponse;
import com.benAbdelWahed.utils.CallbackRetrofit;
import com.benAbdelWahed.utils.PrefManager;
import com.benAbdelWahed.utils.RetrofitModel;
import com.benAbdelWahed.utils.StaticMembers;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class SubscribeDialog extends DialogFragment {
    private int guaranteePrice;
    private boolean hideSubscribe;

    public SubscribeDialog(int guaranteePrice, OnActionListener listener) {
        this.listener = listener;
        this.guaranteePrice = guaranteePrice;
    }

    public SubscribeDialog(boolean hideSubscribe) {
        this.hideSubscribe = hideSubscribe;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTrans);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fg_subscribe, container, false);
    }

    @BindView(R.id.subscribe)
    CardView subscribe;
    @BindView(R.id.insuranceText)
    TextView insuranceText;
    @BindView(R.id.rulesText)
    TextView rulesText;
    @BindView(R.id.backButton)
    ImageView backButton;
    private OnActionListener listener;
    private PrefManager prefManager;
    private ConditionResponse conditionResponse;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        prefManager = PrefManager.getInstance(getContext());
        subscribe.setVisibility(hideSubscribe ? GONE : VISIBLE);
        subscribe.setOnClickListener(this::done);
        backButton.setOnClickListener(v -> dismiss());
        getConditions();
    }

    void changeText(String s) {
        if (guaranteePrice != 0)
            insuranceText.setText(String.format(Locale.getDefault(), " للإشتراك يرجى دفع مبلغ الضمان وهو %d", guaranteePrice));
        rulesText.setText(getTextHTML(s));
    }

    void getConditions() {
        conditionResponse = (ConditionResponse) prefManager.getObject(CONDITIONS, ConditionResponse.class);
        if (conditionResponse != null && conditionResponse.getData() != null)
            changeText(conditionResponse.getData().getDescription());
        Call<ConditionResponse> call = RetrofitModel.getApi(getContext()).getConditions();
        call.enqueue(new CallbackRetrofit<ConditionResponse>(getContext()) {
            @Override
            public void onResponse(@NotNull Call<ConditionResponse> call, @NotNull Response<ConditionResponse> response) {
                ConditionResponse result = response.body();
                if (response.isSuccessful() && result != null) {
                    conditionResponse = result;
                    prefManager.setObject(CONDITIONS, conditionResponse);
                    changeText(conditionResponse.getData().getDescription());
                } else {
                    StaticMembers.checkError(getActivity(), response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<ConditionResponse> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    void done(View v) {
        dismiss();
        listener.onConfirm();
    }

    public interface OnActionListener {

        void onConfirm();

    }
}

package com.benAbdelWahed.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.benAbdelWahed.R;
import com.benAbdelWahed.utils.StaticMembers;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.benAbdelWahed.utils.StaticMembers.checkTextInputEditText;
import static com.benAbdelWahed.utils.StaticMembers.hideKeyboard;
import static com.benAbdelWahed.utils.StaticMembers.showKeyboard;

public class PlaceBidDialog extends DialogFragment {
    public PlaceBidDialog(String bid, OnActionListener listener) {
        this.bid = bid;
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTrans);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fg_place_bid, container, false);
    }

    @BindView(R.id.addBid)
    CardView addBid;
    @BindView(R.id.addBidText)
    TextInputEditText addBidText;
    @BindView(R.id.addBidLayout)
    TextInputLayout addBidLayout;
    @BindView(R.id.highestPrice)
    TextView highestPrice;
    private String bid;
    private OnActionListener listener;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        view.setOnClickListener(v -> dismiss());
        highestPrice.setText(StaticMembers.getTextHTML(String.format(Locale.getDefault(), getString(R.string.highest_price_), bid)));
        addBidText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        addBidText.setText("0");
        addBid.setOnClickListener(this::done);
    }

    void done(View v) {
        if (checkTextInputEditText(addBidText, addBidLayout, getString(R.string.bid_req))) {
            dismiss();
            listener.onConfirm(Integer.parseInt(Objects.requireNonNull(addBidText.getText()).toString()));
        }
    }

    public interface OnActionListener {

        void onConfirm(int bid);

    }
    @Override
    public void onResume()
    {
        super.onResume();
        addBidText.post(() -> {
            addBidText.requestFocus();
            InputMethodManager imm =
                    (InputMethodManager)addBidText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.showSoftInput(addBidText, InputMethodManager.SHOW_IMPLICIT);
        });
    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog) {
        hideKeyboard(addBidText, Objects.requireNonNull(getContext()));
        super.onDismiss(dialog);
    }
}

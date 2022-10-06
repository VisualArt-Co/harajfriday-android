package com.benAbdelWahed.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.benAbdelWahed.R;
import com.benAbdelWahed.responses.settings_response.SettingsData;
import com.benAbdelWahed.utils.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.benAbdelWahed.utils.StaticMembers.SETTINGS;

public class DiscountSystemDialog extends DialogFragment {
    public DiscountSystemDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTrans);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fg_about_app, container, false);
    }

    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.backButton)
    ImageView backButton;
    private PrefManager prefManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        prefManager = PrefManager.getInstance(getContext());
        backButton.setOnClickListener(v -> dismiss());
        titleText.setText(R.string.discountSystem);
        SettingsData data = (SettingsData) prefManager.getObject(SETTINGS, SettingsData.class);
        text.setText(data.getDiscountSystem());
    }

}

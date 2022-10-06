package com.benAbdelWahed.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.benAbdelWahed.R;
import com.benAbdelWahed.responses.social.SocialResponse;
import com.benAbdelWahed.utils.CallbackRetrofit;
import com.benAbdelWahed.utils.PrefManager;
import com.benAbdelWahed.utils.RetrofitModel;
import com.benAbdelWahed.utils.StaticMembers;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

import static com.benAbdelWahed.utils.StaticMembers.SOCIAL_LINKS;

public class AboutAppDialog extends DialogFragment implements Html.ImageGetter {
    public AboutAppDialog() {
    }

    public static AboutAppDialog getInstance(String dataString, String titleString) {
        AboutAppDialog aboutAppDialog = new AboutAppDialog();
        Bundle args = new Bundle();
        args.putString("str", dataString);
        args.putString("titleString", titleString);
        aboutAppDialog.setArguments(args);
        return aboutAppDialog;
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

    private String dataString, titleString;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.backButton)
    ImageView backButton;
    @BindView(R.id.facebook)
    ImageView facebook;
    @BindView(R.id.instagram)
    ImageView instagram;
    @BindView(R.id.linkedin)
    ImageView linkedin;
    @BindView(R.id.twitter)
    ImageView twitter;

    private PrefManager prefManager;
    private SocialResponse socialResponse;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        prefManager = PrefManager.getInstance(getContext());
        assert getArguments() != null;
        dataString = getArguments().getString("str");
        titleString = getArguments().getString("titleString");
        titleText.setText(titleString);
        backButton.setOnClickListener(v -> dismiss());
        text.setText(Html.fromHtml(dataString, this, null));
        getSocial();
        facebook.setOnClickListener(v -> {
            openLink(socialResponse.getData().getFacebook());
        });
        instagram.setOnClickListener(v -> {
            openLink(socialResponse.getData().getInstagram());
        });
        linkedin.setOnClickListener(v -> {
            openLink(socialResponse.getData().getLinkedin());
        });
        twitter.setOnClickListener(v -> {
            openLink(socialResponse.getData().getTwitter());
        });
    }

    private void openLink(String s) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(s));
        startActivity(i);
    }


    void getSocial() {
        socialResponse = (SocialResponse) prefManager.getObject(SOCIAL_LINKS, SocialResponse.class);
        Call<SocialResponse> call = RetrofitModel.getApi(getContext()).getSocialLinks();
        call.enqueue(new CallbackRetrofit<SocialResponse>(getContext()) {
            @Override
            public void onResponse(@NotNull Call<SocialResponse> call, @NotNull Response<SocialResponse> response) {
                SocialResponse result = response.body();
                if (response.isSuccessful() && result != null) {
                    socialResponse = result;
                    prefManager.setObject(SOCIAL_LINKS, socialResponse);
                } else {
                    StaticMembers.checkError(getActivity(), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<SocialResponse> call, Throwable t) {
                super.onFailure(call, t);
            }
        });
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = getResources().getDrawable(R.drawable.image_place_holder);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        new LoadImage().execute(source, d);
        return d;
    }


    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];

            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;
            try {
                stream = getHttpConnection(source);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();
                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return stream;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);
                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                text.setText(text.getText());
            }
        }
    }
}

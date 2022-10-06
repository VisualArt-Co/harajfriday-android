package com.benAbdelWahed.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.benAbdelWahed.R;

import java.util.Locale;

public class Converter {
    //This function to convert the counter that you want to notification when a new notification comes
    //TODO: hint to help: use The Broadcast receiver to receive the change of the notification and change the view

    public static Drawable convertLayoutToImage(Context context, int count, int resIdChecked, int resIdUnChecked) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_number_item, null);
        ImageView imageView = view.findViewById(R.id.icon);
        TextView textView = view.findViewById(R.id.count);
        CardView cardCount = view.findViewById(R.id.cardCount);
        //make sure that the count is not zero or more than 99 to not to overlap
        if (count == 0) {
            imageView.setImageResource(resIdUnChecked);
            cardCount.setVisibility(View.INVISIBLE);
        } else {
            imageView.setImageResource(resIdChecked);
            if (count > 99)
                textView.setText(String.format(Locale.getDefault(), "+%d", 99));
            else
                textView.setText(String.format(Locale.getDefault(), "%d", count));
        }
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}

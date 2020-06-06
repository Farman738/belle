package com.e.belle.ui.OrderSaleActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.e.belle.R;
import com.e.belle.Global.ScaleListener;

import java.io.File;
import java.util.ArrayList;


public class ImageSwipeAdapter extends PagerAdapter {

    private int[] image_resources = {R.drawable.ic_image_blue_100dp};
    private Activity activity;
    ArrayList<Bitmap> bitmap;
    ArrayList<String> fileurls;
    private LayoutInflater layoutInflater;
    ViewPager viewPager;

    public ImageSwipeAdapter(Activity context, ArrayList<Bitmap> mbitmap, ArrayList<String> fileurls, ViewPager viewPager) {
        this.activity = context;
        this.bitmap = mbitmap;
        this.viewPager = viewPager;
        this.fileurls = fileurls;
    }

    @Override
    public int getCount() {
        return this.bitmap.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View pageview = layoutInflater.inflate(R.layout.image_slider, container, false);
        ImageView imageView = (ImageView) pageview.findViewById(R.id.iv_image);
        TextView textView = (TextView) pageview.findViewById(R.id.tv_image_text);
        final ImageButton imgButton = (ImageButton) pageview.findViewById(R.id.delete_image);

        if (bitmap.get(position) == null) {
            textView.setText("Capture image or browse.");
            imageView.setImageResource(image_resources[position]);


        } else {
            int pos = position + 1;
            textView.setText("Image : " + pos);
            imageView.setImageBitmap(this.bitmap.get(position));
            imgButton.setVisibility(View.VISIBLE);
        }
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(activity, "Image:" + String.valueOf(position + 1) + "Deleted:", Toast.LENGTH_LONG).show();
                bitmap.remove(position);
                fileurls.remove(position);
                if (bitmap.size() == 0) {
                    bitmap.add(null);
                }
                ImageSwipeAdapter imageSwipeAdapter = new ImageSwipeAdapter(activity, bitmap, fileurls, viewPager);
                viewPager.setAdapter(imageSwipeAdapter);

            }
        });

        pageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayDialog(position);

            }
        });

        container.addView(pageview);
        return pageview;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    public void DisplayDialog(int pos) {

        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.image_popup, null);

        final ImageView iv_image1 = (ImageView) view.findViewById(R.id.image);

        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_DeviceDefault_DialogWhenLarge_NoActionBar);
            dialog.setContentView(view);

            final ImageView image = (ImageView) view.findViewById(R.id.image);
            final ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading);

            final ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(activity, new ScaleListener(image));
            image.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mScaleGestureDetector.onTouchEvent(motionEvent);
                    return true;
                }
            });

            image.setImageBitmap(this.bitmap.get(pos));
            loading.setVisibility(View.GONE);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            dialog.show();
            final ImageButton closebuttton = (ImageButton) view.findViewById(R.id.ib_close);
            closebuttton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

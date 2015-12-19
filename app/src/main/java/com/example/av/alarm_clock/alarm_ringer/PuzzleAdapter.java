package com.example.av.alarm_clock.alarm_ringer;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.av.alarm_clock.R;

import java.util.List;

/**
 * Created by mikrut on 16.12.15.
 */
public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.ViewHolder> {
    private final Activity activity;
    private final List<ImagePuzzle> objects;

    public PuzzleAdapter(Activity activity, List<ImagePuzzle> objects) {
        this.activity = activity;
        this.objects = objects;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View rowView, int width, int height) {
            super(rowView);
            imageView = (ImageView) rowView.findViewById(R.id.imageview);

            //imageView.setAdjustViewBounds(true);
            //imageView.setMaxHeight(height);
           // imageView.setMaxWidth(width);
           // imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
    }

    @Override
    public PuzzleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DisplayMetrics display = activity.getResources().getDisplayMetrics();

        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rise_and_shine_element_layout, parent, false);
        ViewHolder holder = new ViewHolder(rowView, display.widthPixels, display.heightPixels);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImagePuzzle puzzle = objects.get(position);
        holder.imageView.setImageBitmap(puzzle.getBitmap());
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}

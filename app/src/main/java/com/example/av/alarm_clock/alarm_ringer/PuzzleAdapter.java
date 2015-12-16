package com.example.av.alarm_clock.alarm_ringer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.av.alarm_clock.R;

import java.util.List;

/**
 * Created by mikrut on 16.12.15.
 */
public class PuzzleAdapter extends ArrayAdapter<ImagePuzzle> {
    private final Context context;
    private final List<ImagePuzzle> objects;

    public PuzzleAdapter(Context context, List<ImagePuzzle> objects) {
        super(context, R.layout.rise_and_shine_element_layout, objects);
        this.context = context;
        this.objects = objects;
    }

    static class ViewHolder {
        public ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder;

        View rowView = convertView;
        if (rowView == null) {
            rowView = layoutInflater.inflate(R.layout.rise_and_shine_element_layout,
                    null, true);
            holder = new ViewHolder();
            holder.imageView = (ImageView) rowView.findViewById(R.id.imageview);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        ImagePuzzle puzzle = objects.get(position);
        holder.imageView.setImageBitmap(puzzle.getBitmap());

        return rowView;
    }
}

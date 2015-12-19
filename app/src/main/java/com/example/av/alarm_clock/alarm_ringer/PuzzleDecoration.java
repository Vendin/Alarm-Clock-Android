package com.example.av.alarm_clock.alarm_ringer;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Михаил on 18.12.2015.
 */
public class PuzzleDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = 15;
        outRect.bottom = 15;
    }
}

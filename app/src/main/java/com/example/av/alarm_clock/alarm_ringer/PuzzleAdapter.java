package com.example.av.alarm_clock.alarm_ringer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.example.av.alarm_clock.R;

import java.util.List;

/**
 * Created by mikrut on 16.12.15.
 */
public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.ViewHolder> {
    private final Activity activity;
    private final List<ImagePuzzle> objects;

    private final static int PUZZLE_TAG = R.id.imageTagPuzzle;
    private final static int HOLDER_TAG = R.id.imageTagHolder;

    public PuzzleAdapter(Activity activity, List<ImagePuzzle> objects) {
        this.activity = activity;
        this.objects = objects;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View rowView;

        public ImageView imageView;
        public ImageView resultView;
        private StateListDrawable stateListDrawable;

        public Button isFriendButton;
        public Button isNotFriendButton;

        public ViewHolder(View rowView) {
            super(rowView);
            this.rowView = rowView;

            imageView = (ImageView) rowView.findViewById(R.id.imageview);
            resultView = (ImageView) rowView.findViewById(R.id.result_view);

            isFriendButton = (Button) rowView.findViewById(R.id.isFriendButton);
            isNotFriendButton = (Button) rowView.findViewById(R.id.isNotFriendButton);

            isFriendButton.setOnClickListener(onFriendClickListener);
            isNotFriendButton.setOnClickListener(onOtherClickListener);

            isFriendButton.setTag(rowView);
            isNotFriendButton.setTag(rowView);

            rowView.setTag(HOLDER_TAG, this);

            Context context = rowView.getContext();
            Drawable guessed = context.getResources().getDrawable(R.drawable.ic_mood_black_48dp);
            Drawable misrecognized = context.getResources().getDrawable(R.drawable.ic_mood_bad_black_48dp);

            stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{R.attr.unguessed}, null);
            stateListDrawable.addState(new int[]{R.attr.recognized}, guessed);
            stateListDrawable.addState(new int[]{R.attr.misrecognized}, misrecognized);

            resultView.setImageDrawable(stateListDrawable);

            //imageView.setAdjustViewBounds(true);
            //imageView.setMaxHeight(height);
           // imageView.setMaxWidth(width);
           // imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }

        public void initView(ImagePuzzle puzzle) {
            imageView.setImageBitmap(puzzle.getBitmap());
            rowView.setTag(PUZZLE_TAG, puzzle);

            ImagePuzzle.State puzzleState = puzzle.getState();

            Context context = rowView.getContext();

            int color = Color.BLACK;
            int visibility = View.VISIBLE;
            boolean buttonActiveness = true;

            switch(puzzleState) {
                case MISRECOGNIZED:
                    stateListDrawable.setState(new int[]{R.attr.misrecognized});
                    color = context.getResources().getColor(R.color.not_ok);
                    visibility = View.VISIBLE;
                    buttonActiveness = false;
                    break;
                case UNGUESSED:
                    stateListDrawable.setState(new int[]{R.attr.unguessed});
                    visibility = View.GONE;
                    buttonActiveness = true;
                    break;
                case RECOGNIZED:
                    stateListDrawable.setState(new int[]{R.attr.recognized});
                    color = context.getResources().getColor(R.color.ok);
                    visibility = View.VISIBLE;
                    buttonActiveness = false;
                    break;
            }

            // Android has not tinting support in older versions...
            // That's such a pity!
            // drawable.setTint(color);
            if (visibility == View.VISIBLE && resultView.getVisibility() == View.GONE) {
                resultView.setAlpha(0f);
                resultView.setVisibility(visibility);

                resultView.animate()
                        .alpha(1f)
                        .setDuration(3000);
            } else {
                resultView.setVisibility(visibility);
            }
            isFriendButton.setEnabled(buttonActiveness);
            isNotFriendButton.setEnabled(buttonActiveness);
        }
    }

    @Override
    public PuzzleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DisplayMetrics display = activity.getResources().getDisplayMetrics();

        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rise_and_shine_element_layout, parent, false);
        ViewHolder holder = new ViewHolder(rowView);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImagePuzzle puzzle = objects.get(position);

        holder.initView(puzzle);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    private static final View.OnClickListener onFriendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchElementClick((View) v.getTag(), true);
        }
    };

    private static final View.OnClickListener onOtherClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dispatchElementClick((View) v.getTag(), false);
        }
    };

    private static void dispatchElementClick(View rowView, boolean isFriend) {
        ImagePuzzle puzzle = (ImagePuzzle) rowView.getTag(PUZZLE_TAG);
        puzzle.guess(isFriend);
        ViewHolder holder = (ViewHolder) rowView.getTag(HOLDER_TAG);
        holder.initView(puzzle);
    }
}

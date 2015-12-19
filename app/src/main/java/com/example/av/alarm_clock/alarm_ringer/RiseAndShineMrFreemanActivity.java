package com.example.av.alarm_clock.alarm_ringer;

import android.annotation.SuppressLint;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.ImageTableHelper;

import java.util.ArrayList;
import java.util.List;

public class RiseAndShineMrFreemanActivity extends AppCompatActivity {
    public static final String GUESSES = "guesses";
    public static final String IMAGES_ARRAY = "imagesArray";

    private LinearLayout mContentView;
    private RecyclerView imagesList;
    private LinearLayout fullscreenContentControls;

    private static final int NEED_FRIEND = 3;
    private static final int NEED_OTHERS = 3;
    private static final int NEED_TOTAL  = NEED_FRIEND + NEED_OTHERS;

    private int guesses = 0;
    private int clicks  = 0;
    private int total   = 0;
    private List<ImagePuzzle> puzzles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rise_and_shine_mr_freeman);
        mContentView = (LinearLayout) findViewById(R.id.fullscreen_content);

        Log.d(this.getClass().getCanonicalName(), "Started rise and shine");

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        imagesList = (RecyclerView) findViewById(R.id.imagesList);
        fullscreenContentControls = (LinearLayout) findViewById(R.id.fullscreen_content_controls);

        puzzles = new ArrayList<>();

        //if (savedInstanceState != null){
        //    guesses = savedInstanceState.getInt(GUESSES);
        //    Object[] images = (Object[]) savedInstanceState.get(IMAGES_ARRAY);
        //} else {
            ImageTableHelper imageTableHelper = new ImageTableHelper(this);
            List<ImageFile> unshownFrs = imageTableHelper.getImageFiles(true, NEED_FRIEND, 0);
            List<ImageFile> unshownOts = imageTableHelper.getImageFiles(false, NEED_OTHERS, 0);

            int necessaryAdditional = NEED_TOTAL - unshownFrs.size() - unshownOts.size();
            List<ImageFile> shownFrs = imageTableHelper.getImageFiles(true, necessaryAdditional,
                    0, true);
            necessaryAdditional = necessaryAdditional - shownFrs.size();
            List<ImageFile> shownOts = imageTableHelper.getImageFiles(false, necessaryAdditional,
                    0, true);

            ImagePuzzle.addListToList(this, puzzles, unshownFrs);
            ImagePuzzle.addListToList(this, puzzles, unshownOts);
            ImagePuzzle.addListToList(this, puzzles, shownFrs);
            ImagePuzzle.addListToList(this, puzzles, shownOts);
        //}

        PuzzleAdapter puzzleAdapter = new PuzzleAdapter(this, puzzles);
        imagesList.setAdapter(puzzleAdapter);
        imagesList.setLayoutManager(new LinearLayoutManager(this));
        imagesList.addItemDecoration(new PuzzleDecoration());

        total = puzzles.size();
        checkFinish();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(GUESSES, guesses);
        savedInstanceState.putIntegerArrayList(IMAGES_ARRAY, null);

        super.onSaveInstanceState(savedInstanceState);
    }

    private void checkFinish() {
        if (total == clicks) {
            if (total == 0 || guesses/((float) total) > 0.5) {
                showFinish();
            } else {
                showFinish();
                //TODO: reload
            }
        }
    }

    private void showFinish() {
        fullscreenContentControls.setVisibility(View.VISIBLE);
    }
}

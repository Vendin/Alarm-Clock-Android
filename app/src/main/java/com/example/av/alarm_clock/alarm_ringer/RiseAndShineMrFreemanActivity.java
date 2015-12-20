package com.example.av.alarm_clock.alarm_ringer;

import android.annotation.SuppressLint;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.ImageTableHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiseAndShineMrFreemanActivity extends AppCompatActivity {
    public static final String GUESSES = "guesses";
    public static final String IMAGES_ARRAY = "imagesArray";

    private LinearLayout mContentView;
    private RecyclerView imagesList;
    private LinearLayout fullscreenContentControls;
    private Button button;

    private static final int NEED_FRIEND = 3;
    private static final int NEED_OTHERS = 3;
    private static final int NEED_TOTAL  = NEED_FRIEND + NEED_OTHERS;

    private int guesses = 0;
    private int clicks  = 0;
    private int total   = 0;
    private List<ImagePuzzle> puzzles;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rise_and_shine_mr_freeman);
        mContentView = (LinearLayout) findViewById(R.id.fullscreen_content);

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        imagesList = (RecyclerView) findViewById(R.id.imagesList);
        fullscreenContentControls = (LinearLayout) findViewById(R.id.fullscreen_content_controls);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRinging();
            }
        });

        puzzles = new ArrayList<>();
        ImageTableHelper imageTableHelper = new ImageTableHelper(this);

        //if (savedInstanceState != null){
        //    guesses = savedInstanceState.getInt(GUESSES);
        //    Object[] images = (Object[]) savedInstanceState.get(IMAGES_ARRAY);
        //} else {

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

            // Randomizing puzzles order
            Collections.shuffle(puzzles);
        //}
        for (ImagePuzzle puzzle : puzzles) {
            puzzle.setOnGuessListener(puzzleListener);
            puzzle.getImageFile().setShown(true);
            imageTableHelper.updateImageFile(puzzle.getImageFile());
        }

        PuzzleAdapter puzzleAdapter = new PuzzleAdapter(this, puzzles);
        imagesList.setAdapter(puzzleAdapter);
        imagesList.setLayoutManager(new LinearLayoutManager(this));
        imagesList.addItemDecoration(new PuzzleDecoration());

        total = puzzles.size();
        checkFinish();

        startRinging();
    }

    @Override
    public void onAttachedToWindow() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        super.onAttachedToWindow();
    }

    public void startRinging() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setVolume(1.0f, 1.0f);
        try {
            mediaPlayer.setDataSource(this,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRinging() {
        mediaPlayer.stop();
        finish();
    }

    private PuzzleListener puzzleListener = new PuzzleListener();

    public class PuzzleListener implements ImagePuzzle.OnGuessListener {
        @Override
        public void onGuess(ImagePuzzle puzzle) {
            clicks++;
            if (puzzle.getState().equals(ImagePuzzle.State.RECOGNIZED)) {
                guesses++;
            }
            checkFinish();
        }
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

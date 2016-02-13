package com.example.av.alarm_clock.alarm_ringer;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Build;
import android.app.LoaderManager;
import android.content.Loader;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.loaders.AlarmLoader;
import com.example.av.alarm_clock.loaders.ImageLoader;
import com.example.av.alarm_clock.models.Alarm;
import com.example.av.alarm_clock.models.ImageFile;
import com.example.av.alarm_clock.storage.ImageTableHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RiseAndShineMrFreemanActivity
        extends AppCompatActivity
        implements ImagePuzzle.OnGuessListener {
    public static final String EXTRA_ALARM_ID =
            RiseAndShineMrFreemanActivity.class.getCanonicalName() + ".EXTRA_ALARM_ID";

    public static final String GUESSES = "guesses";
    public static final String IMAGES_ARRAY = "imagesArray";

    public static final int MAX_ATTEMPT = 3;

    private RelativeLayout mContentView;
    private RecyclerView imagesList;
    private LinearLayout fullscreenContentControls;
    private Button button;
    private ProgressBar progressBar;

    private int guesses  = 0;
    private int clicks   = 0;
    private int total    = 0;
    private int attempts = 0;

    private static final int PUZZLE_LOADER_ID = 0;
    private static final int ALARM_LOADER_ID = 1;
    private List<ImagePuzzle> puzzles;
    private Alarm alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rise_and_shine_mr_freeman);
        mContentView = (RelativeLayout) findViewById(R.id.fullscreen_content);

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        imagesList = (RecyclerView) findViewById(R.id.imagesList);
        fullscreenContentControls = (LinearLayout) findViewById(R.id.fullscreen_content_controls);
        button = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRinging();
            }
        });

        imagesList.setLayoutManager(new LinearLayoutManager(this));
        imagesList.addItemDecoration(new PuzzleDecoration());

        Bundle alarmArgs = new Bundle();
        alarmArgs.putInt(EXTRA_ALARM_ID, getIntent().getIntExtra(EXTRA_ALARM_ID, 0));
        getLoaderManager().initLoader(ALARM_LOADER_ID, alarmArgs, alarmLoaderListener);
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
        Intent intent = new Intent(this, MosEisleyOrchestraService.class);
        intent.putExtra(MosEisleyOrchestraService.VIBRATE, alarm.isVibration());
        intent.putExtra(MosEisleyOrchestraService.RINGTONE_URI, alarm.getRingtoneString());
        startService(intent);
    }

    public void stopRinging() {
        Intent intent = new Intent(this, MosEisleyOrchestraService.class);
        stopService(intent);
        finish();
    }

    @Override
    public void onGuess(ImagePuzzle puzzle) {
        clicks++;
        if (puzzle.getState().equals(ImagePuzzle.State.RECOGNIZED)) {
            guesses++;
        }
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
                attempts++;
                if (attempts >= MAX_ATTEMPT) {
                    Toast.makeText(this, "Вы пытались...", Toast.LENGTH_SHORT).show();
                    showFinish();
                } else {
                    Toast.makeText(this, "Слишком много ошибок!", Toast.LENGTH_SHORT).show();
                    puzzles = null;
                    guesses = 0;
                    clicks = 0;
                    getLoaderManager().initLoader(0, null, imagePuzzleLoaderListener).forceLoad();
                }
            }
        }
    }

    private void showFinish() {
        fullscreenContentControls.setVisibility(View.VISIBLE);
    }

    LoaderManager.LoaderCallbacks<List<ImagePuzzle>> imagePuzzleLoaderListener =
            new LoaderManager.LoaderCallbacks<List<ImagePuzzle>>() {
                public Loader<List<ImagePuzzle>> onCreateLoader ( int id, Bundle args){
                    return new ImageLoader(RiseAndShineMrFreemanActivity.this,
                            args.getInt(ImageLoader.TOTAL_PICTURES));
                }

                public void onLoadFinished (Loader<List<ImagePuzzle>> loader,
                                            List<ImagePuzzle> data){
                    puzzles = data;

                    for (ImagePuzzle puzzle : puzzles) {
                        puzzle.setOnGuessListener(RiseAndShineMrFreemanActivity.this);
                    }

                    PuzzleAdapter puzzleAdapter =
                            new PuzzleAdapter(RiseAndShineMrFreemanActivity.this, puzzles);
                    imagesList.setAdapter(puzzleAdapter);

                    imagesList.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    total = puzzles.size();
                    checkFinish();
                }

                public void onLoaderReset(Loader<List<ImagePuzzle>> loader) {
                // TODO: something...
                }
            };

    LoaderManager.LoaderCallbacks<Alarm> alarmLoaderListener =
            new LoaderManager.LoaderCallbacks<Alarm>() {
                public Loader<Alarm> onCreateLoader ( int id, Bundle args){
                    return new AlarmLoader(RiseAndShineMrFreemanActivity.this,
                            args.getInt(EXTRA_ALARM_ID));
                }

                public void onLoadFinished (Loader<Alarm> loader, Alarm data){
                    alarm = data;
                    startRinging();

                    Bundle imagePuzzleArgs = new Bundle();
                    imagePuzzleArgs.putInt(ImageLoader.TOTAL_PICTURES, data.getCountPhoto());
                    getLoaderManager().initLoader(PUZZLE_LOADER_ID, imagePuzzleArgs, imagePuzzleLoaderListener);
                }

                public void onLoaderReset(Loader<Alarm> loader) {
                    // TODO: something...
                }
            };
}

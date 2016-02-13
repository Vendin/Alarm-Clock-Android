package com.example.av.alarm_clock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.av.alarm_clock.alarm_main.AlarmList;
import com.example.av.alarm_clock.auth.ConnectInstagramAsyncTask;
import com.example.av.alarm_clock.auth.LoginActivity;
import com.example.av.alarm_clock.storage.PreferenceConstants;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final Bus bus = new Bus();
    private static AsyncTask loginTask;

    private Button loginViaInstagram;
    private Button loginAsGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginViaInstagram = (Button) findViewById(R.id.button_login_Instagram);
        loginViaInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        loginAsGuest = (Button) findViewById(R.id.button_login_guest);
        loginAsGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(
                        PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PreferenceConstants.ACCESS_TOKEN, PreferenceConstants.USER_IS_GUEST);
                editor.putString(PreferenceConstants.FULL_NAME, "Гость");
                editor.apply();

                Intent intent = new Intent(MainActivity.this, AlarmList.class);
                finish();
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(
                PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        final String authToken = sharedPreferences.getString(PreferenceConstants.ACCESS_TOKEN, null);

        if (authToken != null) {
            if (!authToken.equals(PreferenceConstants.USER_IS_GUEST) &&
                    sharedPreferences.getString(PreferenceConstants.FULL_NAME, null) == null) {
                if (loginTask == null) {
                    loginTask = new ConnectInstagramAsyncTask(authToken,
                            ConnectInstagramAsyncTask.SELF_INFO, this, bus);
                    loginTask.execute((String[]) null);
                }
            } else {
                Intent intent = new Intent(this, AlarmList.class);
                finish();
                startActivity(intent);
            }
        }

        bus.register(this);
        if (loginTask != null && loginTask.getStatus() == AsyncTask.Status.FINISHED)
            finishTask(null);
    }

    @Subscribe
    public void finishTask(JSONObject result) {
        loginTask = null;
        Intent intent = new Intent(this, AlarmList.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }
}

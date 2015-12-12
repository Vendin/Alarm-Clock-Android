package com.example.av.alarm_clock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.av.alarm_clock.auth.ConnectInstagramAsyncTask;
import com.example.av.alarm_clock.auth.LoginActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final Bus bus = new Bus();
    private static AsyncTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.app_pref_file), Context.MODE_PRIVATE);

        final String authToken = sharedPreferences.getString("auth_token", "no auth token");
        Toast toast = Toast.makeText(this, authToken, Toast.LENGTH_SHORT);
        toast.show();

        if (sharedPreferences.getString("auth_token", null) == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (sharedPreferences.getString("full_name", null) == null) {
            if (loginTask == null) {
                loginTask = new ConnectInstagramAsyncTask(authToken,
                        ConnectInstagramAsyncTask.SELF_INFO, this, bus);
                loginTask.execute((String[]) null);
            }
        }

        bus.register(this);
        finishTask(null);
    }

    @Subscribe
    public void finishTask(JSONObject result) {
        if (loginTask != null && loginTask.getStatus() == AsyncTask.Status.FINISHED) {
            Toast toast = Toast.makeText(this, "Task has finished!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }
}

package com.example.av.alarm_clock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.av.alarm_clock.auth.ConnectInstagramAsyncTask;
import com.example.av.alarm_clock.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

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
        }

        if (sharedPreferences.getString("full_name", null) == null) {
            ConnectInstagramAsyncTask asyncTask = new ConnectInstagramAsyncTask(authToken,
                    ConnectInstagramAsyncTask.SELF_INFO, this);
            asyncTask.execute((String) null);
        }
    }
}

package com.example.av.alarm_clock.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.av.alarm_clock.MainActivity;
import com.example.av.alarm_clock.R;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {
    private static final String REDIRECT_URI    = "http://localhost";
    private static AsyncTask loginTask;
    private static final Bus bus = new Bus();

    // UI references.
    private WebView webView;
    private ProgressBar progressBar;

    private String AUTH_URL;
    private String CLIENT_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLIENT_ID = getString(R.string.oauth_client_id);
        AUTH_URL =  "https://instagram.com/oauth/authorize/?client_id="
                + CLIENT_ID + "&redirect_uri=" + REDIRECT_URI + "&response_type=token" +
                "&scope=public_content+follower_list";

        setContentView(R.layout.activity_login);
        setupActionBar();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Set up the login form.
        webView = (WebView) findViewById(R.id.webView);
        setClient(webView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(ProgressBar.GONE);
        webView.loadUrl(AUTH_URL);

        bus.register(this);
        if (loginTask != null && loginTask.getStatus() == AsyncTask.Status.FINISHED) {
            finishTask(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Subscribe
    public void finishTask(JSONObject result) {
        Log.d(this.getClass().getCanonicalName(), "Task was finished");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            // getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setClient(final WebView webView) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(this.getClass().getCanonicalName(), "URL: " + url);
                if (url.startsWith(REDIRECT_URI)) {
                    if (url.contains("access_token")) {
                        String accessToken = url.split("#access_token=")[1];
                        Context context = LoginActivity.this;

                        webView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);

                        Log.d(this.getClass().getCanonicalName(), "TOKEN: " + accessToken);
                        if (loginTask == null) {
                            loginTask = new ConnectInstagramAsyncTask(accessToken,
                                    ConnectInstagramAsyncTask.SELF_INFO,
                                    LoginActivity.this, bus);
                        }
                        if (loginTask.getStatus() == AsyncTask.Status.PENDING) {
                            loginTask.execute((String[]) null);
                        }

                        SharedPreferences sharedPreferences = context.getSharedPreferences(
                                context.getString(R.string.app_pref_file),
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefereceEditor = sharedPreferences.edit();
                        prefereceEditor.putString("access_token", accessToken);
                        prefereceEditor.commit();
                    } else if (url.contains("error_reason")) {
                        Toast.makeText(LoginActivity.this,
                                String.format(getString(R.string.account_denied), "Instagram"),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
}


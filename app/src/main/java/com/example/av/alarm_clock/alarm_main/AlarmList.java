package com.example.av.alarm_clock.alarm_main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.av.alarm_clock.MainActivity;
import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.alarm_ringer.ImageDownloadIntentService;
import com.example.av.alarm_clock.alarm_ringer.RingADingDingReceiver;
import com.example.av.alarm_clock.auth.LoginActivity;
import com.example.av.alarm_clock.storage.AlarmTableHelper;
import com.example.av.alarm_clock.storage.PreferenceConstants;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmList extends AppCompatActivity {
    private ListView alarmListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        alarmListView = (ListView) findViewById(R.id.alarmListView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString(PreferenceConstants.FULL_NAME, "Гость");

        Drawer.Result drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new SectionDrawerItem().withName("Информация"),
                        new SecondaryDrawerItem().withName(username).withIcon(FontAwesome.Icon.faw_user).setEnabled(false),
                        new SecondaryDrawerItem().withName("О приложении").setEnabled(true),
                        new SecondaryDrawerItem().withName("Выход").setEnabled(true)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) AlarmList.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(AlarmList.this.getCurrentFocus().getWindowToken(), 0);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            String itemName = ((Nameable) drawerItem).getName();

                            if (itemName.equals("Выход")) {
                                SharedPreferences.Editor sharedPreferences = getSharedPreferences(PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
                                sharedPreferences.clear();
                                sharedPreferences.commit();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else if (itemName.equals("О приложении")) {
                                Intent intent = new Intent(AlarmList.this, AboutActivity.class);
                                startActivity(intent);
                            }
                        }
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                                   int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                        }
                        return false;
                    }
                })
                .build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmList.this, AlarmSetup.class);
                startActivity(intent);
            }
        });

        Calendar calendar = GregorianCalendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_YEAR);
        Context context = getApplicationContext();

        SharedPreferences preferences = context.getSharedPreferences(
                PreferenceConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        int lastUpload = preferences.getInt(PreferenceConstants.LAST_UPLOAD_DAY, today + 1);

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        boolean connected = info != null && info.isAvailable() && info.isConnected();

        if (connected) {
            ImageDownloadIntentService.startActionUploadImages(context);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AlarmTableHelper alarmTableHelper = new AlarmTableHelper(this);
        Cursor cursor = alarmTableHelper.getAlarmsCursor();

        AlarmCursorAdapter alarmCursorAdapter = new AlarmCursorAdapter(this, cursor, 0);
        alarmListView.setAdapter(alarmCursorAdapter);
        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int alarmId = (Integer) view.getTag(AlarmCursorAdapter.TAG_ID);
                Intent intent = new Intent(AlarmList.this, AlarmSetup.class);
                intent.putExtra(AlarmSetup.ALARM_ID_PARAMETER, alarmId);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

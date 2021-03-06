package com.example.av.alarm_clock.alarm_main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.av.alarm_clock.loaders.AlarmLoader;
import com.example.av.alarm_clock.loaders.ImageLoader;
import com.example.av.alarm_clock.models.Alarm;
import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.storage.AlarmTableHelper;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

public class AlarmSetup extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Alarm> {
    public static final String ALARM_ID_PARAMETER = AlarmSetup.class.getCanonicalName() + ":ALARM_ID_PARAMETER";

    private Integer alarmID;

    private String name;
    private int choosenHour;
    private int choosenMinute;
    private int dayMask;

    private boolean vibrate = false;
    private Uri chosenRingtone;

    private int countImage = 5;

    protected ProgressBar progressBar;
    protected ScrollView scrollView;

    protected TextView input_name;
    protected TextView input_time;
    protected TextView input_day;

    protected Switch switchVibration;
    protected TextView input_signal;

    protected TextView input_count_img;

    protected ArrayList<Integer> seletedItems=new ArrayList<>();

    final CharSequence[] fullDate = {
            " Понедельник",
            " Вторник ",
            " Среда ",
            " Четверг",
            " Пятница",
            " Суббота",
            " Воскресенье"
    };

    final CharSequence[] shortDate = {
            " Пн",
            " Вт",
            " Ср ",
            " Чт",
            " Пт",
            " Сб",
            " Вс"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setup);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initViews();

        String presetName = null;
        GregorianCalendar now = new GregorianCalendar();
        int presetHour = now.get(Calendar.HOUR_OF_DAY);
        int presetMinute = now.get(Calendar.MINUTE);
        int presetDayMask = 0;
        boolean presetVibrate = false;
        Uri presetRingtone = null;
        int presetCountImage = 5;

        setName(presetName);
        setChoosenHourMinute(presetHour, presetMinute);
        setDayMask(presetDayMask);
        setVibrate(presetVibrate);
        setChosenRingtone(presetRingtone);
        setCountImage(presetCountImage);

        Intent callerIntent = getIntent();
        if (callerIntent.hasExtra(ALARM_ID_PARAMETER)) {
            alarmID = callerIntent.getIntExtra(ALARM_ID_PARAMETER, 0);
            Bundle bundle = new Bundle();
            bundle.putInt(ALARM_ID_PARAMETER, alarmID);
            getLoaderManager().initLoader(0, bundle, this);
        } else {
            scrollView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        input_name = (TextView)findViewById(R.id.input_name);
        input_time = (TextView)findViewById(R.id.input_time);
        input_day = (TextView)findViewById(R.id.input_day);

        switchVibration = (Switch) findViewById(R.id.switch1);
        input_signal = (TextView) findViewById(R.id.input_signal);

        input_count_img = (TextView)findViewById(R.id.count_img);

        switchVibration.setChecked(false);
        switchVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVibrate(isChecked);
            }
        });
    }

    public void onClickTime(View v){
        openDialogTime();
    }

    protected void openDialogTime(){
        TimePickerDialog timePickerDialog =
                new TimePickerDialog(this, myCallBack, choosenHour, choosenMinute, true);

        timePickerDialog.setTitle("Введите время");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            setChoosenHourMinute(hour, minute);
        }
    };

    public void onClickDay(View v){
        opentDialogDay();
    }

    private int bufDayMask;
    private void opentDialogDay() {
        boolean[] check = new boolean[7];
        for (int i = 0; i < 7; i++) {
            check[i] = ((dayMask & (1 << i)) != 0);
        }
        bufDayMask = dayMask;
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Выберете дни, в которые будет повторяться будильник.")
            .setMultiChoiceItems(fullDate, check, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                    if (isChecked) {
                        bufDayMask = bufDayMask | (1 << indexSelected);
                    } else {
                        bufDayMask = bufDayMask & ~(1 << indexSelected);
                    }
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    setDayMask(bufDayMask);
                }
            }).setNegativeButton("Отмена", null).create();
        dialog.show();
    }

    public void onClickName(View v) {
        openDialogName();
    }

    protected void openDialogName(){
        final EditText input = new EditText(this);
        input.setText(name);

        new AlertDialog.Builder(this)
        .setTitle("Введите название будильника")
        .setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                setName(input.getEditableText().toString());
            }
        }).setNegativeButton("Отмена", null).create().show();
    }

    public void onClickCountImg(View v){
        openDialogCountImg();
    }

    public void openDialogCountImg(){
        final NumberPicker input = new NumberPicker(this);
        input.setMaxValue(10);
        input.setMinValue(1);
        input.setValue(countImage);
        input.setWrapSelectorWheel(false);
        input.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        new AlertDialog.Builder(this)
        .setTitle("Выберете количество всего фотографий")
        .setView(input)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                setCountImage(input.getValue());
            }
        })
                .setNegativeButton("Отмена", null).create().show();
    }

    public void onClickSignal(View v) {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Выберите сигнал");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, chosenRingtone);
        this.startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            setChosenRingtone(uri);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_edit, menu);
        if (alarmID == null) {
            MenuItem item = menu.findItem(R.id.deleteAction);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveAction:
                saveAlarm();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.deleteAction:
                deleteAlarm();
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void saveAlarm() {

        AlarmTableHelper alarmTableHelper = new AlarmTableHelper(this);
        Alarm alarmToSave = null;
        if (alarmID != null)
            alarmToSave = alarmTableHelper.getAlarm(alarmID);
        if (alarmToSave == null) {
            alarmToSave = new Alarm();
            alarmToSave.setEnabled(true);
        }

        alarmToSave.setHour((short) choosenHour);
        alarmToSave.setMinute((short) choosenMinute);
        alarmToSave.setVibration(vibrate);
        alarmToSave.setRingtone(chosenRingtone);
        alarmToSave.setName(name);
        alarmToSave.setCountPhoto(countImage);
        alarmToSave.setDayMask(dayMask);

        if (alarmToSave.getId() != null) {
            alarmTableHelper.updateAlarm(alarmToSave);
        } else {
            alarmTableHelper.saveAlarm(alarmToSave);
        }
    }

    protected void deleteAlarm() {
        AlarmTableHelper alarmTableHelper = new AlarmTableHelper(this);
        alarmTableHelper.deleteAlarm(alarmID);
    }

    public void setChoosenHourMinute(int choosenHour, int choosenMinute) {
        this.choosenHour = choosenHour;
        this.choosenMinute = choosenMinute;

        input_time.setText(String.format("%02d:%02d", choosenHour, choosenMinute));
    }

    public void setDayMask(int dayMask) {
        this.dayMask = dayMask;
        seletedItems.clear();
        if(dayMask != 0) {
            for (int i = 0; i < 7; i++) {
                if ((dayMask & (1 << i)) != 0) {
                    seletedItems.add(i);
                }
            }
        }

        Collections.sort(seletedItems);
        StringBuilder resultBuilder = new StringBuilder();
        for(int i = 0; i < seletedItems.size(); ++i){
            resultBuilder.append(shortDate[(int) seletedItems.get(i)]);
        }
        String result = resultBuilder.toString();
        if (result.equals("")) {
            result = "Всегда";
        }
        input_day.setText(result);
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
        switchVibration.setText(vibrate ? "Включена" : "Выключена");
    }

    public void setChosenRingtone(Uri chosenRingtone) {
        this.chosenRingtone = chosenRingtone;
        String title = "Signal";
        if (chosenRingtone != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(this, chosenRingtone);
            title = ringtone.getTitle(this);
        }
        input_signal.setText(title);
    }

    public void setCountImage(int countImage) {
        this.countImage = countImage;
        input_count_img.setText(String.valueOf(countImage));
    }

    public void setName(String name) {
        this.name = name;
        input_name.setText(name);
    }

    @Override
    public Loader<Alarm> onCreateLoader(int id, Bundle args) {
        return new AlarmLoader(this, args.getInt(ALARM_ID_PARAMETER));
    }

    @Override
    public void onLoadFinished(Loader<Alarm> loader, Alarm data){
        if (data != null) {
            setChoosenHourMinute(data.getHour(), data.getMinute());
            setVibrate(data.isVibration());
            setName(data.getName());
            setDayMask(data.getDayMask());
            setChosenRingtone(data.getRingtone());
            setCountImage(data.getCountPhoto());
        } else {
            alarmID = null;
            invalidateOptionsMenu();
        }
        scrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Alarm> loader) {
        // TODO: something...
    }
}

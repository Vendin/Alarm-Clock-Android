package com.example.av.alarm_clock.alarm_main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.av.alarm_clock.models.Alarm;
import com.example.av.alarm_clock.R;
import com.example.av.alarm_clock.storage.AlarmTableHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmSetup extends AppCompatActivity {
    public static final String ALARM_ID_PARAMETER = AlarmSetup.class.getCanonicalName() + ":ALARM_ID_PARAMETER";

    private Integer alarmID;
    private TimePicker timePicker;

    protected TextView input_time;
    protected EditText input_name;
    protected TextView input_day;
    protected Switch switchVibration;

    protected ArrayList seletedItems=new ArrayList();

    final CharSequence[] fullDate = {
            " Понедельник",
            " Вторник ",
            " Среда ",
            " Четверг",
            " Пятница",
            " Суббота",
            " Воскресение"
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
        //timePicker = (TimePicker) findViewById(R.id.timePicker);

//        GregorianCalendar now = new GregorianCalendar();
//        timePicker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
//        timePicker.setCurrentMinute(now.get(Calendar.MINUTE));
//
//        Intent callerIntent = getIntent();
//        if (callerIntent.hasExtra(ALARM_ID_PARAMETER)) {
//            alarmID = callerIntent.getIntExtra(ALARM_ID_PARAMETER, 0);
//            AlarmTableHelper alarmTableHelper = new AlarmTableHelper(this);
//            Alarm alarm = alarmTableHelper.getAlarm(alarmID);
//            if (alarm != null) {
//                timePicker.setCurrentHour((int) alarm.getHour());
//                timePicker.setCurrentMinute((int) alarm.getMinute());
//            } else {
//                alarmID = null;
//            }
//        }

        input_time = (TextView)findViewById(R.id.input_time);
        input_name = (EditText)findViewById(R.id.input_name);
        input_day = (TextView)findViewById(R.id.input_day);


        switchVibration = (Switch) findViewById(R.id.switch1);
        switchVibration.setChecked(false);
        switchVibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchVibration.setText("Включена");
                } else {
//                    Toast.makeText(getApplicationContext(), "SET OFF", Toast.LENGTH_SHORT).show();
                    switchVibration.setText("Выключена");
                }
            }
        });
    }

    public void onClickTime(View v){
        openDialogTime();
    }

    protected void openDialogTime(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, myCallBack, 00, 00, true);
        timePickerDialog.setTitle("Введите время");
        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            input_time.setText(hour + ":" + minute);
        }
    };

    public void onClickDay(View v){
        opentDialogDay();
    }

    private void opentDialogDay() {
        boolean check[] = {false, false, false, false, false, false, false};
        for(int i = 0; i < seletedItems.size(); ++i){
            check[(int)seletedItems.get(i)] = true;
        }
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Выберете дни, в которые будет повторяться будильник.")
                .setMultiChoiceItems(fullDate, check, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String result = "";
                        for(int i = 0; i < seletedItems.size(); ++i){
                            result += shortDate[(int)seletedItems.get(i)];
                        }
                        if(result.equals(new String(""))){
                            result = "Никогда";
                        }
                        input_day.setText(result);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create();
        dialog.show();
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
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        AlarmTableHelper alarmTableHelper = new AlarmTableHelper(this);
        Alarm alarmToSave = null;
        if (alarmID != null)
            alarmToSave = alarmTableHelper.getAlarm(alarmID);
        if (alarmToSave == null) {
            alarmToSave = new Alarm();
            alarmToSave.setHour((short) hour);
            alarmToSave.setMinute((short) minute);
            alarmToSave.setEnabled(true);
            alarmTableHelper.saveAlarm(alarmToSave);
        } else {
            alarmToSave.setHour((short) hour);
            alarmToSave.setMinute((short) minute);
            alarmTableHelper.updateAlarm(alarmToSave);
        }
    }

    protected void deleteAlarm() {
        AlarmTableHelper alarmTableHelper = new AlarmTableHelper(this);
        alarmTableHelper.deleteAlarm(alarmID);
    }
}

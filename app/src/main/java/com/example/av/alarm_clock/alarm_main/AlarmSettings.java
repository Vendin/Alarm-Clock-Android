package com.example.av.alarm_clock.alarm_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.av.alarm_clock.R;

public class AlarmSettings extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private Button save;
    private TextView percentageView;
    private int percentage;
    private int numRingers;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_settings);

        save = (Button) findViewById(R.id.button_save);
        percentageView = (TextView)findViewById(R.id.label_percentage);

        View.OnClickListener onSave = new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Spinner spinner = (Spinner) findViewById(R.id.entry_phcount);
                String selected = spinner.getSelectedItem().toString();
                numRingers = Integer.parseInt(selected);
            }
        };


        save.setOnClickListener(onSave);
    }

    @Override
    public void onStart() {
        seekBar = (SeekBar)findViewById(R.id.entry_percentage);
        seekBar.setOnSeekBarChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub
        percentage = seekBar.getProgress();
        percentageView.setText("Процент угадывания: " + String.valueOf(percentage));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        percentage = seekBar.getProgress();
        percentageView.setText("Процент угадывания: " + percentage);
    }
}
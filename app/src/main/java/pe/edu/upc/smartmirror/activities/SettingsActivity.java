package pe.edu.upc.smartmirror.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;

import pe.edu.upc.smartmirror.R;
import pe.edu.upc.smartmirror.backend.models.Widget;

public class SettingsActivity extends BaseActivity {

    Switch mClockSwitch;
    Switch mWeatherSwitch;
    Switch mNewsSwitch;
    Switch mCalendarSwitch;
    Switch mPlayerSwitch;
    Switch mMailSwitch;
    Widget mWidget;
    int mUserId;
    ImageButton cancelButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initialize();
    }
    private void initialize(){
        initializeTollbar();
        mClockSwitch = (Switch) findViewById(R.id.clockSwitch);
        mWeatherSwitch = (Switch) findViewById(R.id.weatherSwitch);
        mNewsSwitch = (Switch) findViewById(R.id.newsSwitch);
        mCalendarSwitch = (Switch) findViewById(R.id.calendarSwitch);
        mPlayerSwitch = (Switch) findViewById(R.id.playerSwitch);
        mMailSwitch = (Switch) findViewById(R.id.mailSwitch);
        cancelButton = (ImageButton) findViewById(R.id.cancelSettings);
        saveButton = (ImageButton) findViewById(R.id.saveSettings);
        mUserId = getCurrentUser().getForeId();
        if(mUserId > 0){
            mWidget = Widget.findOrCreate(mUserId);
            setSwitchState();
        }else{
            showMessage("Error de coneccion, no se encontro al usuario");
        }
    }
    private void initializeTollbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });
    }
    private void setSwitchState(){
        mClockSwitch.setChecked(mWidget.isClock());
        mWeatherSwitch.setChecked(mWidget.isWeather());
        mNewsSwitch.setChecked(mWidget.isNews());
        mCalendarSwitch.setChecked(mWidget.isCalendar());
        mPlayerSwitch.setChecked(mWidget.isPlayer());
        mMailSwitch.setChecked(mWidget.isMail());
        bindEvents();
    }
    private void bindEvents(){
        mClockSwitch.setOnClickListener(onChangeSwitch);
        mWeatherSwitch.setOnClickListener(onChangeSwitch);
        mNewsSwitch.setOnClickListener(onChangeSwitch);
        mCalendarSwitch.setOnClickListener(onChangeSwitch);
        mPlayerSwitch.setOnClickListener(onChangeSwitch);
        mMailSwitch.setOnClickListener(onChangeSwitch);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWidget.save();
                updateWidget(mWidget);
                showMessage("Guardado!");
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });
    }

    private View.OnClickListener onChangeSwitch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mWidget.setClock(mClockSwitch.isChecked());
            mWidget.setWeather(mWeatherSwitch.isChecked());
            mWidget.setNews(mNewsSwitch.isChecked());
            mWidget.setCalendar(mCalendarSwitch.isChecked());
            mWidget.setPlayer(mPlayerSwitch.isChecked());
            mWidget.setMail(mMailSwitch.isChecked());
        }
    };
}

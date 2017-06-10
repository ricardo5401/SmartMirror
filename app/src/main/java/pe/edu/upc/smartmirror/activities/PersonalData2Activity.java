package pe.edu.upc.smartmirror.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pe.edu.upc.smartmirror.R;
import pe.edu.upc.smartmirror.backend.models.User;

public class PersonalData2Activity extends BaseActivity {

    final static int PICKER_DIALOG = 999;
    private User user;
    private Context context;
    private DatePicker datePicker;
    private int year, month, day;
    EditText birthDateEditText;
    Spinner occupationSpinner, areaSpinner;
    ImageButton prevButton;
    ImageButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data2);
        user = getCurrentUser();
        validateUser();
    }
    private void validateUser(){
        if(user == null || user.getForeId() < 0){
            goToLogin();
        }else{
            Log.e("PERSONAL_DATA", "stage 2");
            logUser(user);
            initializeComponents();
            initializeDatePicker();
        }
    }

    private void initializeComponents(){
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        prevButton = (ImageButton) findViewById(R.id.prevButton);
        nextButton.setEnabled(true);
        context = this;
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonalData2Activity.super.onBackPressed();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            updateUser();
            gotToNextActivity();
            }
        });
        birthDateEditText = (EditText) findViewById(R.id.birthDateEditText);
        birthDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                birthDateEditText.setEnabled(false);
                showDialog(999);
                return false;
            }
        });
        initializeSpinner();
    }

    private void initializeSpinner(){
        occupationSpinner = (Spinner) findViewById(R.id.occupationSpinner);
        areaSpinner = (Spinner) findViewById(R.id.areaSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> areaAdapter = ArrayAdapter.createFromResource(this,
                R.array.area, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.occupation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        occupationSpinner.setAdapter(adapter);
        areaSpinner.setAdapter(areaAdapter);
        int occupation = adapter.getPosition(user.getOccupation());
        int area = areaAdapter.getPosition(user.getArea());
        areaSpinner.setSelection(area);
        occupationSpinner.setSelection(occupation);
    }

    private void updateUser(){
        user.setBirthDate(birthDateEditText.getText().toString());
        user.setArea((String) areaSpinner.getSelectedItem());
        user.setOccupation((String) occupationSpinner.getSelectedItem());
        update(user, false);
        user.save();
    }

    private void gotToNextActivity(){
        if(user.getPictureCount() > 2){
            goToHome();
        }else{
            startActivity(new Intent(context, PhotoActivity.class));
        }
    }

    private void initializeDatePicker(){
        datePicker = new DatePicker(new android.view.ContextThemeWrapper(this.getBaseContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        String date = user.getBirthDate();
        if(date != null && !date.isEmpty()){
            birthDateEditText.setText(formatDate(date));
        }else{
            year = datePicker.getYear();
            month = datePicker.getMonth();
            day = datePicker.getDayOfMonth();
            showDate(year, month, day);
        }
    }

    private String formatDate(String date){

        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            Date parsed = f.parse(date);
            date = f.format(parsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            DatePickerDialog dialog = new DatePickerDialog(this,
                    myDateListener, year, month, day);
            dialog.setOnCancelListener(cancelListener);
            return dialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    birthDateEditText.setEnabled(true);
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private DatePickerDialog.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            birthDateEditText.setEnabled(true);
        }
    };

    private void showDate(int year, int month, int day){
        String parsedMonth = month < 10 ? "0" + String.valueOf(month) : String.valueOf(month);
        String parsedDay = day < 10 ? "0" + String.valueOf(day) : String.valueOf(day);
        birthDateEditText.setText(new StringBuilder().append(year).append("-")
                .append(parsedMonth).append("-").append(parsedDay));
    }
}

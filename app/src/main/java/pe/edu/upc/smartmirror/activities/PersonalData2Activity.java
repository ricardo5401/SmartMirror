package pe.edu.upc.smartmirror.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import pe.edu.upc.smartmirror.R;
import pe.edu.upc.smartmirror.backend.models.User;

public class PersonalData2Activity extends BaseActivity {

    final static int PICKER_DIALOG = 999;
    private User user;
    private Context context;
    private DatePicker datePicker;
    private int year, month, day;
    EditText birthDateEditText;
    ImageButton prevButton;
    ImageButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data2);
        user = (User) getIntent().getSerializableExtra("user");
        validateUser();
    }
    private void validateUser(){
        if(user == null || user.getForeId() < 0){
            goToLogin();
        }else{
            initializeComponents();
        }
    }

    private void initializeComponents(){
        initializeDatePicker();
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        prevButton = (ImageButton) findViewById(R.id.prevButton);
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
                user.setBirthDate(birthDateEditText.getText().toString());
                update(user, false);
                Intent intent = new Intent(context, PhotoActivity.class);
                intent.putExtra("user_id", user.getForeId());
                startActivity(intent);
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
    }

    private void initializeDatePicker(){
        datePicker = new DatePicker(new android.view.ContextThemeWrapper(this.getBaseContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
        year = datePicker.getYear();
        month = datePicker.getMonth();
        day = datePicker.getDayOfMonth();
        showDate(year, month, day);
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

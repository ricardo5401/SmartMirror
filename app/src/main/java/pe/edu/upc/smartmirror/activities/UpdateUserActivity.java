package pe.edu.upc.smartmirror.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import pe.edu.upc.smartmirror.R;
import pe.edu.upc.smartmirror.backend.models.User;

public class UpdateUserActivity extends BaseActivity {

    final static int PICKER_DIALOG = 999;
    Spinner spinner;
    private DatePicker datePicker;
    private int year, month, day;
    EditText birthDateEditText;
    EditText editTextName;
    EditText editTextLastName;
    ImageButton saveButton;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);
        Log.e("UPDATE_USER_ACTIVITY", "start activity");
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
        initializeEditText();
        initializeSpinner();
        initializeDatePicker();
        saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildUser();
                update(user, true);
            }
        });
    }

    private void initializeEditText(){
        birthDateEditText = (EditText) findViewById(R.id.birthDateEditText);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextName.setText(user.getFirstName());
        editTextLastName.setText(user.getLastName());
        birthDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                birthDateEditText.setEnabled(false);
                showDialog(999);
                return false;
            }
        });
    }

    private void buildUser(){
        user.setGender((String) spinner.getSelectedItem())
            .setBirthDate(birthDateEditText.getText().toString())
            .setFirstName(editTextName.getText().toString())
            .setLastName(editTextLastName.getText().toString());
    }

    private void initializeSpinner(){
        spinner = (Spinner) findViewById(R.id.genderSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        String gender = user.getGender();
        spinner.setSelection((gender == "Femenino") ? 1 : 0);
    }

    private void initializeDatePicker(){
        datePicker = new DatePicker(new android.view.ContextThemeWrapper(this.getBaseContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar));
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

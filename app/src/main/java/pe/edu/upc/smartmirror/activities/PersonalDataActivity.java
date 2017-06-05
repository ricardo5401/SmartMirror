package pe.edu.upc.smartmirror.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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

public class PersonalDataActivity extends BaseActivity {

    Spinner spinner;
    EditText editTextName;
    EditText editTextLastName;
    ImageButton saveButton;
    User user;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        Log.e("UPDATE_USER_ACTIVITY", "start activity");
        user = getCurrentUser();
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
        context = this;
        saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildUser();
                user.save();
                Intent intent = new Intent(context, PersonalData2Activity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeEditText(){
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextName.setText(user.getFirstName());
        editTextLastName.setText(user.getLastName());
    }

    private void buildUser(){
        user.setGender((String) spinner.getSelectedItem())
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

}

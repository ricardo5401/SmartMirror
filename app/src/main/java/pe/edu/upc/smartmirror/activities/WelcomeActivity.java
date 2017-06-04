package pe.edu.upc.smartmirror.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pe.edu.upc.smartmirror.R;
import pe.edu.upc.smartmirror.backend.models.User;

public class WelcomeActivity extends AppCompatActivity {

    Button mStartConfigButton;
    Context mContext;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initialize();
    }

    private void initialize(){
        mContext = this;
        mUser = (User) getIntent().getSerializableExtra("user");
        mStartConfigButton = (Button) findViewById(R.id.startConfigButton);
        mStartConfigButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonalDataActivity.class);
                intent.putExtra("user", mUser);
                startActivity(intent);
            }
        });
    }
}
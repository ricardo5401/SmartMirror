package pe.edu.upc.smartmirror.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pe.edu.upc.smartmirror.R;
import pe.edu.upc.smartmirror.backend.network.SmartMirrorAPI.Permisions;

public class FacebookConfirmActivity extends AppCompatActivity {

    Button mAcceptedButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_confirm);
        initialize();
    }
    private void initialize(){
        mAcceptedButton = (Button) findViewById(R.id.acceptedButton);
        mAcceptedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Permisions.FACEBOOK_ALERT_ACCEPTED);
                finish();
            }
        });
    }
}

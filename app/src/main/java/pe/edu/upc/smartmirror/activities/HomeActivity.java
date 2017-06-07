package pe.edu.upc.smartmirror.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import pe.edu.upc.smartmirror.R;
import pe.edu.upc.smartmirror.backend.models.User;
import pe.edu.upc.smartmirror.backend.network.SmartMirrorAPI;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class HomeActivity extends BaseActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private Button updateButton;
    private Button photoButton;
    private Button widgetButton;
    User user;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = getCurrentUser();
        initializeComponents();
    }
    private void initializeComponents(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        context = this;
        updateButton = (Button) findViewById(R.id.updateButton);
        photoButton = (Button) findViewById(R.id.photoButton);
        widgetButton = (Button) findViewById(R.id.widgetButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PersonalDataActivity.class));
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("no implementado");
            }
        });
        widgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });

        ((TextView)findViewById(R.id.welcomeTextView))
                .setText(getString(R.string.home_text) + " " + user.getFirstName().toUpperCase() + "!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cerrar_sesion:
                logout();
                return true;
            default: return false;
        }
    }

    @Override
    public void onBackPressed() {

    }

}

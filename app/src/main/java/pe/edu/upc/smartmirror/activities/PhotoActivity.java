package pe.edu.upc.smartmirror.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import pe.edu.upc.smartmirror.R;
import pe.edu.upc.smartmirror.backend.models.User;
import pe.edu.upc.smartmirror.backend.network.SmartMirrorAPI;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PhotoActivity extends BaseActivity {

    private static final String TAG = "PHOTO_ACTIVITY";
    private boolean cameraAvailable = false;
    final int PERMISSION_REQUEST_CAMERA = SmartMirrorAPI.Permisions.PERMISSION_REQUEST_CAMERA;
    final int CAPTURE_IMAGE_REQUEST_CODE = SmartMirrorAPI.Permisions.CAPTURE_IMAGE_REQUEST_CODE;
    ImageButton photoImageButton1;
    ImageButton photoImageButton2;
    ImageButton photoImageButton3;
    ImageButton prevButton, nextButton;
    int photoCount;
    User user;
    String baseName;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initialize();
    }

    private void initialize(){
        user = getCurrentUser();
        if(user != null){
            initializeComponents();
            validatePermissions();
        }else{ goToLogin(); }
    }

    private void initializeComponents(){
        context = this;
        photoImageButton1 = (ImageButton) findViewById(R.id.photoImageButton1);
        photoImageButton2 = (ImageButton) findViewById(R.id.photoImageButton2);
        photoImageButton3 = (ImageButton) findViewById(R.id.photoImageButton3);
        photoImageButton1.setOnClickListener(takePhotoListener);
        photoImageButton2.setOnClickListener(takePhotoListener);
        photoImageButton3.setOnClickListener(takePhotoListener);
        prevButton = (ImageButton) findViewById(R.id.prevButton);
        nextButton = (ImageButton) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ReadyActivity.class));
            }
        });
        nextButton.setEnabled(false);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoActivity.super.onBackPressed();
            }
        });
        photoCount = user.getPictureCount();
        baseName = "SM_" + String.valueOf( user.getForeId()) + "_";
    }
    //validate permisions
    private void validatePermissions() {
        if(permissionsGranted()) {
            cameraAvailable = true;
            setButtonsStates();
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
    }

    private boolean permissionsGranted() {
        boolean grantedCameraPermission =
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PERMISSION_GRANTED);
        boolean grantedStoragePermission =
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED);
        Log.d(TAG, "Permission for CAMERA: " + String.valueOf(grantedCameraPermission));
        Log.d(TAG, "Permission for STORAGE: " + String.valueOf(grantedStoragePermission));
        return (grantedCameraPermission && grantedStoragePermission);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA: {
                cameraAvailable = ((grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) &&
                        (grantResults.length > 0 && grantResults[1] == PERMISSION_GRANTED));
            }

        }
        updatePermissionsDependentFeatures();
    }

    private void updatePermissionsDependentFeatures() {
        photoImageButton1.setEnabled(cameraAvailable);
        photoImageButton2.setEnabled(cameraAvailable);
        photoImageButton3.setEnabled(cameraAvailable);
        if(cameraAvailable)
            setButtonsStates(); //check photos
    }


    View.OnClickListener takePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                startPhotoActivity();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    private void startPhotoActivity(){
        Intent intent = new Intent(this, TakePhotoActivity.class);
        intent.putExtra("PhotoName", baseName +
                String.valueOf(photoCount+1) + ".png");
        intent.putExtra("UserId", user.getForeId());
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            try{
                //Bitmap bm = EnviarFotoActivity.photoBitmap;
                photoCount += 1;
                user.setPictureCount(photoCount);
                //updating on local storage
                user.save();
                setButtonsStates();

            }catch (Exception ex){
                ex.printStackTrace();
                Log.w("ACT INICIO","Error en activity result");
            }
        }
    }

    private void setButtonsStates(){
        switch (photoCount){
            case 0: break;
            case 1:
                setImageUri(photoImageButton1, baseName + "1.png");
                break;
            case 2:
                setImageUri(photoImageButton1, baseName + "1.png");
                setImageUri(photoImageButton2, baseName + "2.png");
                break;
            default:
                setImageUri(photoImageButton1, baseName + "1.png");
                setImageUri(photoImageButton2, baseName + "2.png");
                setImageUri(photoImageButton3, baseName + "3.png");
                nextButton.setBackground(getDrawable(R.drawable.circle_button));
                nextButton.setImageResource(R.drawable.ic_right_arrow);
                nextButton.setEnabled(true);
                break;
        }
    }

    private void setImageUri(ImageButton button, String path){
        Uri uri = getPhotoUri(path);
        if(uri != null){
            button.setImageURI(uri);
        }else{
            button.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
        }
        button.setScaleType(ImageView.ScaleType.FIT_XY);
        button.setEnabled(false);
    }
}

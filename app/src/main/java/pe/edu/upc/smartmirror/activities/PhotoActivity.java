package pe.edu.upc.smartmirror.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.io.File;

import pe.edu.upc.smartmirror.R;
import pe.edu.upc.smartmirror.backend.network.SmartMirrorAPI;
import pe.edu.upc.smartmirror.backend.utils.Encoder;
import pe.edu.upc.smartmirror.backend.utils.ImageHelper;

public class PhotoActivity extends BaseActivity {

    Uri uri;
    ImageView photoImageView;
    ImageButton moreButton;
    ImageButton minusButton;
    ImageButton readyButton;
    ImageButton againButton;
    ImageButton cancelButton;
    static final String TAG = "PHOTO_ACTIVITY";

    int userId = -1;
    String photoName;
    public Bitmap photoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        InitializeComponents();
        startTakeAPhoto();
    }

    private void InitializeComponents(){
        userId = getIntent().getIntExtra("UserId",-1);
        photoName = getIntent().getStringExtra("PhotoName");
        photoImageView = (ImageView) findViewById(R.id.mainPhotoImageView);
        moreButton = (ImageButton) findViewById(R.id.moreButton);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoBitmap = ImageHelper.rotateToRight(photoBitmap);
                showPhoto(photoBitmap);
            }
        });
        minusButton = (ImageButton) findViewById(R.id.minusButton);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoBitmap = ImageHelper.rotateToLeft(photoBitmap);
                showPhoto(photoBitmap);
            }
        });
        readyButton = (ImageButton) findViewById(R.id.readyButton);
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "ACTION: CLICK");
                showDialogLoading("Processing photo, please wait a moment");
                Log.e(TAG, "START SEND PHOTO");
                if(photoBitmap ==null) {
                    hideDialogLoading();
                    showMessage("No hay foto. Tomar foto primero");
                }else{ sendPhoto(photoBitmap); }

            }
        });
        againButton = (ImageButton) findViewById(R.id.againButton);
        againButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTakeAPhoto();
            }
        });
        cancelButton = (ImageButton) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }

    private void sendPhoto(final Bitmap photo){
        AndroidNetworking.post(SmartMirrorAPI.Server.PICTURE_URL)
                .addBodyParameter("UserId", String.valueOf(userId))
                .addBodyParameter("Base64Photo", Encoder.ToBase64(photo))
                .setTag(TAG)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        sendPhotoSuccess();
                    }
                    @Override
                    public void onError(ANError error) {
                        hideDialogLoading();
                        showMessage("Something is wrong, please contact to RodoLinares");
                        Log.e(TAG, "Error on send photo");
                        Log.e(TAG, "Error status code: " + String.valueOf(error.getErrorCode()));
                        Log.e(TAG, "Error body: " + error.getErrorBody());
                        Log.e(TAG, "Error detail: " + error.getErrorDetail());
                    }
                });
    }

    private void sendPhotoSuccess(){
        Log.i(TAG, "success!!");
        hideDialogLoading();
        showMessage("Photo successfully uploaded");
        setResult(RESULT_OK);
        finish();
    }

    private void startTakeAPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),photoName);
        uri = Uri.fromFile(file);
        Log.i(TAG, "File path: " + uri.getEncodedPath());

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, SmartMirrorAPI.MediaType.PHOTO);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SmartMirrorAPI.MediaType.PHOTO && resultCode == RESULT_OK) {
            ContentResolver contentResolver = getContentResolver();
            contentResolver.notifyChange(uri,null);
            //Bitmap bm;
            try{
                Bitmap bm = MediaStore.Images.Media.getBitmap(contentResolver, uri);
                photoBitmap = ImageHelper.rescale(bm);
                Log.i(TAG, "showing Picture");
                showPhoto(photoBitmap);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else if(resultCode == RESULT_CANCELED){
            setResult(RESULT_CANCELED);
            finish();

        }
    }

    private void showPhoto(Bitmap foto){
        photoImageView.setImageBitmap(foto);
    }
}

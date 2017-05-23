package pe.edu.upc.smartmirror.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import pe.edu.upc.smartmirror.backend.models.User;
import pe.edu.upc.smartmirror.backend.network.Constants;

/**
 * Created by ricardo on 5/19/17.
 */

public class BaseActivity extends AppCompatActivity {
    Handler toastMessage;
    private ProgressDialog dialog;
    public static final String UPDATE_TAG = "UPDATE_USER";
    public static final String SIGNIN_TAG = "SIGNIN_USER";
    public static final String SIGNUP_TAG = "SIGNUP_USER";
    public static final String LOG_USER = "LOG_USER";
    public static final String SHOW_LOADER = "LOADING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        createHandlers(this);
    }

    protected void createHandlers(final Context context){
        toastMessage = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                Toast.makeText(context, message.obj.toString(), Toast.LENGTH_LONG).show();
            }
        };
    }

    protected void showMessage(String mensaje){
        toastMessage.obtainMessage(1, mensaje).sendToTarget();
    }

    protected void showDialogLoading(String mensaje){
        this.dialog.setMessage(mensaje);
        this.dialog.show();
    }

    protected void hideDialogLoading(){
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    protected void update(final User user, final boolean toMain){
        Log.i(UPDATE_TAG, "updating user");
        logUser(user);
        showDialogLoading("Updating...");
        String url = Constants.Server.USER_URL + "/" + String.valueOf( user.getForeId() );
        Log.i(UPDATE_TAG, "URL: " + url);
        AndroidNetworking.put(url)
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addHeaders("charset", "utf-8")
                .addBodyParameter("Id", String.valueOf( user.getForeId() ))
                .addBodyParameter("FirstName", user.getFirstName())
                .addBodyParameter("LastName", user.getLastName())
                .addBodyParameter("Gender", user.getGender())
                .addBodyParameter("BirthDate", user.getBirthDate())
                .addBodyParameter("Email", user.getEmail())
                .addBodyParameter("AccountType", user.getAccountType())
                .setTag(UPDATE_TAG)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(UPDATE_TAG, "success!!");
                        saveUser(user);
                        if(toMain){ goToHome(user); }
                    }
                    @Override
                    public void onError(ANError error) {
                        if(error.getErrorCode() == 0){
                            Log.e(UPDATE_TAG, "Corregir la respuesta del servidor, retorno no content");
                            saveUser(user);
                            if(toMain){ goToHome(user); }
                        }else{
                            manageNetworkError(error, UPDATE_TAG);
                            showError("Ocurrio un error al actualizar, intenta mas tarde");
                        }
                    }
                });
    }

    public void checkUserFields(User user){
        Log.i(SIGNIN_TAG, "Checking user");
        if(user.requireUpdate()){
            Log.i("USER_LOADED", "REQUIRE_UPDATE");
            goToUpdateUser(user);
        }else{
            saveUser(user);
            goToHome(user);
        }
    }

    public void goToHome(User user){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void goToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(SHOW_LOADER, 5);
        startActivity(intent);
    }

    public void goToUpdateUser(User user){
        Intent intent = new Intent(this, UpdateUserActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public JSONObject extractUser(JSONObject jsonObject){
        try {
            return jsonObject.getJSONObject("user");
        } catch (JSONException e) {
            e.printStackTrace();
            return jsonObject;
        }
    }

    public void manageNetworkError(ANError error, String TAG){
        Log.e(TAG, "Error code: " + String.valueOf(error.getErrorCode()));
        Log.e(TAG, "Error detail: " + error.getErrorDetail());
    }

    public void showError(String message){
        showMessage(message);
        hideDialogLoading();
    }

    public void logUser(User user){
        Log.e(LOG_USER, "Id: " + String.valueOf(user.getForeId()));
        Log.e(LOG_USER, "FirstName: " + user.getFirstName());
        Log.e(LOG_USER, "LastName: " + user.getLastName());
        Log.e(LOG_USER, "Email: " + user.getEmail());
        Log.e(LOG_USER, "Token: " + user.getRefreshToken());
        Log.e(LOG_USER, "Gender: " + user.getGender());
        Log.e(LOG_USER, "BirthDate: " + user.getBirthDate());
        Log.e(LOG_USER, "Pictures: " + String.valueOf(user.getPictureCount()));
    }

    public void logout(){
        LoginManager.getInstance().logOut();
        removerSavedEmail();
        goToLogin();
    }

    public void saveUser(User user){
        //save on local db
        User fromDB = User.findByForeId(user.getForeId());
        if(fromDB != null){
            //asing local user id
            user.setId(fromDB.getId());
        }
        user.save();
        //save email as index
        storeEmail(user.getEmail());
    }

    public void storeEmail(String email){
        // save email to load user on app startup
        SharedPreferences.Editor sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE).edit();
        sharedPreferences.putString("email", email);
        sharedPreferences.commit();
        Log.i("STORAGE_USER", "Saved!");
    }

    public String loadEmail(){
        Log.i("STORAGE_USER", "loaded!");
        return getSharedPreferences("prefs", MODE_PRIVATE).getString("email", null);
    }

    public void removerSavedEmail(){
        getSharedPreferences("prefs", MODE_PRIVATE).edit().remove("email").apply();
        Log.i("STORAGE_USER", "Removed!");
    }


    protected boolean genderToBoolean(String gender){
        if(gender == "" || gender == null){
            return false;
        }else return gender == "female";
    }
    protected String genderToString(boolean gender){
        return gender ? "female" : "male";
    }

    protected Uri getPhotoUri(String name){
        File file = getPhotoFile(name);
        return file.exists() ? Uri.fromFile(file) : null;
    }
    protected File getPhotoFile(String name){
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), name);
    }
}
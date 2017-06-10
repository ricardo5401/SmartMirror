package pe.edu.upc.smartmirror.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import pe.edu.upc.smartmirror.R;
import pe.edu.upc.smartmirror.backend.models.User;
import pe.edu.upc.smartmirror.backend.network.SmartMirrorAPI;
import pe.edu.upc.smartmirror.backend.network.Facebook;
import pe.edu.upc.smartmirror.backend.network.Google;
import pl.droidsonroids.gif.GifImageView;

public class LoginActivity extends BaseActivity {

    static final int SPLASH_SCREEN_DELAY = 2000;
    private static final int RC_SIGN_IN = 20;
    private static final int FB_RC_SIGN_IN = 30;
    TextView loadingTextView;
    GifImageView loadingGiftView;
    LoginButton facebookButton;
    Button facebookValidator;
    SignInButton googleButton;
    CallbackManager callbackManager;
    GoogleApiClient mGoogleApiClient;
    int loader;
    User fbUser;
    static Context context;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LOGIN_ACTIVITY", "start activity");
        setContentView(R.layout.activity_login);
        initialize();
        simulateLoader();
    }

    private void initialize(){
        loadingTextView = (TextView) findViewById(R.id.loadingGiftText);
        loadingGiftView = (GifImageView) findViewById(R.id.loadingGift);
        googleButton = (SignInButton) findViewById(R.id.googleButton);
        TextView textView = (TextView) googleButton.getChildAt(0);
        textView.setText(R.string.continue_with_google);
        textView.setTextAlignment(ViewFlipper.TEXT_ALIGNMENT_TEXT_START);
        facebookButton = (LoginButton) findViewById(R.id.facebookButton);
        callbackManager = CallbackManager.Factory.create();
        loader = getIntent().getIntExtra(SHOW_LOADER, 0);
        context = this;
        initializeGoogleAuth();
        initializeFacebookAuth();
        initializeFacebookValidator();
        timer = new Timer();
    }

    private void simulateLoader(){
        // Simulate a long loading process on application startup.
        if(loader > 0){
            showLogin();
        }else{
            timer.schedule(new LoaderTask(), SPLASH_SCREEN_DELAY);
        }
    }

    private void showLogin(){
        loadingGiftView.setVisibility(View.INVISIBLE);
        loadingTextView.setVisibility(View.INVISIBLE);
        googleButton.setVisibility(View.VISIBLE);
        facebookButton.setVisibility(View.VISIBLE);
        facebookValidator.setVisibility(View.VISIBLE);
    }

    private void showLoading(){
        loadingGiftView.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.VISIBLE);
        googleButton.setVisibility(View.INVISIBLE);
        facebookButton.setVisibility(View.INVISIBLE);
        facebookValidator.setVisibility(View.INVISIBLE);
    }

    private void goToNextActivity(String email){
        User user = User.findByEmail(email);
        if(user != null){
            Log.e("LOGIN_ACTIVITY", "User loaded");
            checkUserFields(user);
        }else{
            showLogin();
        }
    }

    private void initializeGoogleAuth(){
        mGoogleApiClient = Google.getClient(this);
        findViewById(R.id.googleButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestGoogleAuth(RC_SIGN_IN);
            }
        });
    }


    private void requestGoogleAuth(int requestCode){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, requestCode);
    }

    private void initializeFacebookValidator(){
        facebookValidator = (Button) findViewById(R.id.facebookValidator);
        facebookValidator.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FacebookConfirmActivity.class);
                startActivityForResult(intent, SmartMirrorAPI.Permisions.FACEBOOK_ALERT_REQUEST);
            }
        });
    }


    private void initializeFacebookAuth(){
        facebookButton = (LoginButton) findViewById(R.id.facebookButton);
        facebookButton.setReadPermissions(Facebook.ReadPermission());
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                try {
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    Log.v("FACEBOOK_AUTH", response.toString());
                                    //asign result to temp fb_user
                                    fbUser = Facebook.SignInResult(object);
                                    handleSignInResult(fbUser);
                                }
                            });;
                    request.setParameters(Facebook.Parameters());
                    request.executeAsync();
                }catch(Exception ex){
                    showMessage(ex.getMessage());
                    handleSignInResult(null);
                }
            }

            @Override
            public void onCancel() {
                Log.e("FACEBOOK_AUTH","cancelado");
                showMessage("Login cancelado");
            }

            @Override
            public void onError(FacebookException exception) {
                showMessage(exception.getMessage());
                exception.printStackTrace();
            }

        });
    }

    private void setLoadingText(String text){
        loadingTextView.setText(text);
    }

    @Override
    public void onBackPressed() {
        //empty prevent back to main activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult( Google.SignInResult(result) );
        }else if( requestCode == FB_RC_SIGN_IN ){
            // TThis request is only accessed when register is required
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            fbUser.setRefreshToken( Google.SignInResult(result).getRefreshToken() );
            signUP(fbUser);
        }else if( requestCode == SmartMirrorAPI.Permisions.FACEBOOK_ALERT_REQUEST){
            // validate accepted
            if(resultCode == SmartMirrorAPI.Permisions.FACEBOOK_ALERT_ACCEPTED){
                facebookButton.callOnClick();
            }
        }
        else{ //facebook callback
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void handleSignInResult(User user){
        if(user != null && !user.getEmail().isEmpty()){
            showLoading();
            signIn(user);
        }else{
            showMessage("Unknown error, please try again");
        }
    }

    protected void signIn(final User user){
        setLoadingText("Iniciando...");
        Log.i(SIGNIN_TAG, "URL: " + SmartMirrorAPI.Server.USER_URL);
        AndroidNetworking.get(SmartMirrorAPI.Server.USER_URL)
                .addQueryParameter("email", user.getEmail())
                .setTag(SIGNIN_TAG)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        User authenticatedUser = User.build( extractUser(response) );
                        if(authenticatedUser != null && !authenticatedUser.getEmail().isEmpty()){
                            Log.i(SIGNIN_TAG, "Sign in successfully");
                            logUser(authenticatedUser);
                            checkUserFields(authenticatedUser);
                        }else{

                            signUP(user);
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        manageSignInError(error, user);
                    }
                });
    }

    private void checkAccountType(User user){
        if(user.getAccountType() == "facebook" && user.getRefreshToken().isEmpty()){
            requestGoogleAuth(FB_RC_SIGN_IN);
        }else{
            signUP(user);
        }
    }

    public void signUP(final User user){
        setLoadingText("Registrando...");
        Log.i(SIGNUP_TAG, "URL: " + SmartMirrorAPI.Server.USER_URL);
        AndroidNetworking.post(SmartMirrorAPI.Server.USER_URL)
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addHeaders("charset", "utf-8")
                .addBodyParameter("FirstName", user.getFirstName())
                .addBodyParameter("LastName", user.getLastName())
                .addBodyParameter("Gender", user.getGender())
                .addBodyParameter("BirthDate", user.getBirthDate())
                .addBodyParameter("Email", user.getEmail())
                .addBodyParameter("AccountType", user.getAccountType())
                .addBodyParameter("RefreshToken", user.getRefreshToken())
                .setTag(SIGNUP_TAG)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            user.setForeId(response.getInt("UserId"))
                                    .setWidgetsId(response.getInt("WidgetsId"));
                            Log.i(SIGNUP_TAG, "sign up sucess!! with id: " + String.valueOf(user.getId()));
                            logUser(user);
                            goToWelcome(user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        ManageUnkownError(error, SIGNUP_TAG);
                    }
                });
    }

    private void manageSignInError(ANError error, User user){
        if(error.getErrorCode() == 404){
            Log.i(SIGNIN_TAG, "Not found, trying sign up");
            checkAccountType(user);
        }else{
            ManageUnkownError(error, SIGNIN_TAG);
        }
    }

    private void ManageUnkownError(ANError error, String TAG){
        manageNetworkError(error, TAG);
        showLogin();
        showMessage("Server error, try again");
    }

    private class LoaderTask extends TimerTask{
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String email = loadEmail();
                    if(email == null){
                        showLogin();
                    }else{
                        goToNextActivity(email);
                    }
                }
            });
        }
    }
}

package pe.edu.upc.smartmirror.backend.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import pe.edu.upc.smartmirror.backend.models.User;

/**
 * Created by ricardo on 5/19/17.
 */

public class Google {

    private static GoogleSignInOptions getOptions(){
        return new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Constants.Google.CLIENT_ID)
                .requestServerAuthCode(Constants.Google.CLIENT_ID, true)
                .requestScopes(
                        new Scope(Constants.Google.EMAIL_SCOPE),
                        new Scope(Constants.Google.CALENDAR_SCOPE))
                .requestEmail().build();
    }

    public static GoogleApiClient getClient(Context activity){

        return new GoogleApiClient.Builder(activity)
                .enableAutoManage((FragmentActivity) activity /* FragmentActivity */,
                        new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.e("ON CONN FAILED","CONEXION FALLIDA");

                            }
                        } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, getOptions())
                .build();
    }
    public static User SignInResult(GoogleSignInResult result){

        Log.d("GOOGLE LOGIN", "Resultado de Login GOOGLE:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acc = result.getSignInAccount();

            LogResult(acc);
            return new User()
                    .setEmail(acc.getEmail())
                    .setFirstName(acc.getGivenName())
                    .setLastName(acc.getFamilyName())
                    .setAccountType("google")
                    .setRefreshToken(acc.getServerAuthCode());
            //getServerAuthCode Returns a one-time server auth code to send to your web server
            // which can be exchanged for access token and sometimes refresh token
        } else {
            return null;
        }
    }
    public static User SignInResult(GoogleSignInResult result, User user){
        //Pedir credenciales de google cuando se registre con facebook
        //por ello solo actualizo los google fields
        if (result.isSuccess()) {
            GoogleSignInAccount acc = result.getSignInAccount();
            return user.setRefreshToken(acc.getServerAuthCode());
        }
        return user;
    }

    private static void LogResult(GoogleSignInAccount acc){
        Log.e("GOOGLE LOGIN", "email= "+ acc.getEmail());
        Log.e("GOOGLE LOGIN", "family name = "+ acc.getFamilyName());
        Log.e("GOOGLE LOGIN", "display name = "+ acc.getDisplayName());
        Log.e("GOOGLE LOGIN", "given name= "+ acc.getGivenName());
        Log.e("GOOGLE LOGIN", "token = "+ acc.getIdToken());
        Log.e("GOOGLE LOGIN", "server code = "+ acc.getServerAuthCode());
    }
}

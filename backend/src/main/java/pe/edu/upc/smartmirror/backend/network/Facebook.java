package pe.edu.upc.smartmirror.backend.network;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import pe.edu.upc.smartmirror.backend.models.User;

/**
 * Created by ricardo on 5/19/17.
 */

public class Facebook {
    public static List<String> ReadPermission(){
        return Arrays.asList("public_profile", "email", "user_friends");
    }

    public static User SignInResult(JSONObject result){
        User user = new User()
                .setFirstName(getString(result, "first_name"))
                .setLastName(getString(result, "last_name"))
                .setEmail(getString(result, "email"))
                .setGender(getString(result ,"gender"))
                .setAccountType("facebook");
        return user;
    }
    public static String getString(JSONObject result, String field){
        try {
            return result.getString(field);
        }catch (JSONException e){
            Log.e("BUILD_JSON", e.getMessage());
            return "";
        }
    }

    public static Bundle Parameters(){
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender");
        return parameters;
    }
}

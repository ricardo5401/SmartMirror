package pe.edu.upc.smartmirror.backend.models;

import android.content.SharedPreferences;
import android.util.Log;

import com.orm.SugarRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ricardo on 5/19/17.
 */

public class User extends SugarRecord implements Serializable {

    private String accountType;
    private String birthDate;
    private String email;
    private String firstName;
    private String gender;
    private String lastName;
    private int pictureCount;
    private int foreId;
    private int widgetsId;
    private String refreshToken;

    public User(){
        pictureCount = 0;
    }

    public String getFirstName() {
        return invalidStringField( firstName ) ? "" : firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public int getForeId(){
        return this.foreId;
    }

    public User setForeId(int mforeId){
        this.foreId = mforeId;
        return this;
    }

    public String getLastName() {
        return invalidStringField( lastName ) ? "" : lastName;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return invalidStringField( email ) ? "" : email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getRefreshToken() {
        return invalidStringField( refreshToken ) ? "" : refreshToken;
    }

    public User setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public String getAccountType() {
        return invalidStringField( accountType ) ? "" : accountType;
    }

    public User setAccountType(String accountType) {
        this.accountType = accountType;
        return this;
    }

    public String getBirthDate() {
        return invalidStringField( birthDate ) ? "" : birthDate;
    }

    public User setBirthDate(String birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public int getPictureCount() {
        return pictureCount;
    }

    public User setPictureCount(int pictureCount) {
        this.pictureCount = pictureCount;
        return this;
    }

    public String getGender() {
        return invalidStringField( gender ) ? "" : gender;
    }

    public User setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public int getWidgetsId() {
        return widgetsId;
    }

    public User setWidgetsId(int widgetsId) {
        this.widgetsId = widgetsId;
        return this;
    }

    public boolean invalidStringField(String field){
        return field == null ||  field == "null" || field.isEmpty();
    }

    public boolean requireUpdate(){
        return invalidStringField(this.email) || invalidStringField(this.firstName) ||
                invalidStringField(this.birthDate) || invalidStringField(this.lastName) ||
                invalidStringField(this.gender) || pictureCount < 3;
    }

    public static User findByEmail(String email){
        List<User> users = User.find(User.class, "email = ?", email);
        return (users.size() > 0) ? users.get(0) : null;
    }

    public static User findByForeId(int foreId){
        List<User> users = User.find(User.class, "fore_id = ?", String.valueOf(foreId));
        return (users.size() > 0) ? users.get(0) : null;
    }

    public static User build(JSONObject object){
        try {
            return new User()
                    .setForeId(object.getInt("Id"))
                    .setBirthDate(object.getString("BirthDate"))
                    .setFirstName(object.getString("FirstName"))
                    .setLastName(object.getString("LastName"))
                    .setGender(object.getString("Gender"))
                    .setEmail(object.getString("Email"))
                    .setAccountType(object.getString("AccountType"))
                    .setPictureCount(object.getInt("PhotoQty"))
                    .setRefreshToken(object.getString("RefreshToken"));
        } catch (JSONException e) {
            Log.i("BUILD_USER", e.getMessage());
            return null;
        }
    }
}

package pe.edu.upc.smartmirror.backend.utils;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by ricardo on 5/19/17.
 */

public class Encoder {
    
    public static String ToBase64(Bitmap bitmap){
        if(bitmap==null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.w("Encoder" , "ToBase64: "+encoded);
        return encoded;
    }
}

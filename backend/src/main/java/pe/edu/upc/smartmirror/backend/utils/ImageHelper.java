package pe.edu.upc.smartmirror.backend.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by ricardo on 5/19/17.
 */

public class ImageHelper {

    public static final int PIXEL_LIMIT = 150000;

    public static Bitmap rescale(Bitmap bitmap){
        float x = bitmap.getWidth();
        float y = bitmap.getHeight();
        float pixels = x*y;
        float newScale = 1;
        if(pixels > PIXEL_LIMIT)
            newScale = PIXEL_LIMIT / pixels;
        newScale= (float) Math.sqrt(newScale);
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap,(int)(newScale * x) ,(int)(newScale*y),false);
        bitmap.recycle();
        return newBitmap;
    }

    public static Bitmap rotateToRight(Bitmap bitmap){
        return rotate(bitmap,90);
    }

    public static Bitmap rotateToLeft(Bitmap bitmap){
        return rotate(bitmap,-90);
    }

    private static Bitmap rotate(Bitmap bitmap, int degrees){
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        bitmap.recycle();
        return newBitmap;
    }
}

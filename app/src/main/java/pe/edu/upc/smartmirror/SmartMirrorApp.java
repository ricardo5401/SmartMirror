package pe.edu.upc.smartmirror;

import com.androidnetworking.AndroidNetworking;
import com.orm.SugarApp;

/**
 * Created by ricardo on 5/19/17.
 */

public class SmartMirrorApp extends SugarApp {

    private static SmartMirrorApp mSmartMirrorApp;

    public SmartMirrorApp(){
        super();
        mSmartMirrorApp = this;
    }

    public static SmartMirrorApp getInstance(){
        return mSmartMirrorApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}

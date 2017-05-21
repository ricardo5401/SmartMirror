package pe.edu.upc.smartmirror.backend.network;

/**
 * Created by ricardo on 5/19/17.
 */
public class Constants {

    public static class Google {
        public static String CLIENT_ID = "23682495278-8ks35ogopofp419ipiapo7o8of3ruluc.apps.googleusercontent.com";
        public static String EMAIL_SCOPE = "https://www.googleapis.com/auth/gmail.readonly";
        public static String CALENDAR_SCOPE = "https://www.googleapis.com/auth/calendar";
    }
    public static class Server {
        static String BASE = "https://reflection-upc.azurewebsites.net";
        public static String USER_URL = BASE + "/api/Person";
        public static String PICTURE_URL = BASE + "/api/Photo";
        public static String WIDGET_URL = BASE + "/api/Widgets";
    }

    public static class Permisions {
        public static final int CAPTURE_IMAGE_REQUEST_CODE = 100;
        public static final int CAPTURE_VIDEO_REQUEST_CODE = 200;
        public static final int PERMISSION_REQUEST_CAMERA = 100;
    }

    public static class MediaType {
        public static final int PHOTO = 50;
        public static final int VIDEO = 60;
    }

}

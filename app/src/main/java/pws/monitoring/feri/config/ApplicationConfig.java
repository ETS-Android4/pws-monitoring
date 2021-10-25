package pws.monitoring.feri.config;

public class ApplicationConfig {
    public static final String PWS_SERVER = "https://pws-server.herokuapp.com";
    public static final String USER_KEY = "user";
    public static final String RECIPIENT_KEY = "recipient";
    public static final String AUTHORITY_KEY = "pws.monitoring.feri.fileprovider";
    public static final String API_UPDATE_R_KEY = "updatePlant";
    public static final String API_UPDATE_P_KEY = "updateRecipient";
    public static final String API_DELETE_KEY = "deleteRecipient";
    public static final String ERROR_KEY = "Other error";
    public static final String WARNING_KEY = "Warning";
    public static final String INFO_KEY = "Info";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_TIME_FORMAT_PICTURE = "dd/MM/yyyy HH:mm:ss";
    public static final int ERROR_CODE = -1;
    public static final int TRIES_LIMIT = 30;
    public static final int INTERVAL = 10000;
}

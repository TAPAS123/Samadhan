package groupmanager.samadhan;

public interface Config {

	// CONSTANTS
	static final String YOUR_SERVER_URL =  "YOUR_SERVER_URL/gcm_server_files/register.php";
	// YOUR_SERVER_URL : Server url where you have placed your server files
    
	// Google test project id 209463317513 created by tapas 
    static final String GOOGLE_SENDER_ID = "730875472331"; // Place here your Google project id

    /*** Tag used on log messages.*/
    static final String TAG = "SAMADHAN GCM";

    static final String DISPLAY_MESSAGE_ACTION ="groupmanager.samadhan.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";
		
}

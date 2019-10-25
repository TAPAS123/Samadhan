package groupmanager.samadhan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class SplashScreen1 extends Activity{
	Intent menuIntent;
	byte[] AppLogo;
	Context context=this;
	Thread background ;
	String TimeOut;
	int timeoutval;
	VideoView VidVw;
	
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.splashscreen1);
		    VidVw=(VideoView) findViewById(R.id.videoView);
	        Get_SharedPref_Values();

	        if((TimeOut==null)||(TimeOut.length()==0)||(TimeOut.equals("0"))){
	        	timeoutval=15000;
	        }else{
	        	int val=Integer.parseInt(TimeOut);
	        	timeoutval=val*1000;
	        }
	         
	        /*VidVw.setOnClickListener(new OnClickListener()
	        { 
	        	@Override
	        	public void onClick(View arg0) {
	        		//cdt.cancel();
	        		//if(background.isAlive())
	        		//{
	        			//background.interrupt();
	        			DoTaskClickEvent();
	        		//}
  			  	}
  			});*/

		 VidVw.setOnTouchListener(new View.OnTouchListener() {
			 @Override
			 public boolean onTouch(View v, MotionEvent event) {

				 if (VidVw.isPlaying()) {
					 VidVw.stopPlayback();
				 }
				 DoTaskClickEvent();
				 return true;
			 }
		 });


		 VidVw.setMediaController(null);
		 Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
				 + R.raw.intro1); //add file without any extension
		 VidVw.setVideoURI(video);
		 //setContentView(VidVw);
		 VidVw.start();
				
			/*background = new Thread() {
			public void run() {
				try {
					sleep(timeoutval);
					runOnUiThread(new Runnable() {
						public void run() {
							DoTaskClickEvent();// Show the Result of
						}
					});
				} catch (InterruptedException e) {
					// e.getMessage();
				} catch (Exception e) {
					e.getMessage();
				}
				// Progsdial.dismiss();
			}
		};
		background.start();*/
			  

	 }
	 
	 private void Get_SharedPref_Values()
		{
		   SharedPreferences sharedpreferences  = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
			 if (sharedpreferences.contains("TimeOut"))
		      {
		    	  TimeOut=sharedpreferences.getString("TimeOut", "");
	          } 
		}
	 
	 private void DoTaskClickEvent()
	    {
		 menuIntent= new Intent(context,Main_Home.class);
	     startActivity(menuIntent);
	     finish();
	    	
	    }
	 
	
	 public boolean onKeyDown(int keyCode, KeyEvent event) 
	 {
	   	 if (keyCode == KeyEvent.KEYCODE_BACK) {
	   		// finish();
	   	    return true;
	   	 }
	   	return super.onKeyDown(keyCode, event);
	 }

}

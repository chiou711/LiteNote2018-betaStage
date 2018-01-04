package com.cw.litenote.operation.audio;

import com.cw.litenote.R;
import com.cw.litenote.note.Note_audio;
import com.cw.litenote.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

/***************************************************************
 * 
 * audio prepare task
 * 
 */
public class Async_audioPrepare extends AsyncTask<String,Integer,String>
{
	 private Activity act;
	 public ProgressDialog mPrepareDialog;

	 Async_audioPrepare(Activity act)
	 {
		 this.act = act;
	 }	 
	 
	 @Override
	 protected void onPreExecute() 
	 {
	 	super.onPreExecute();
	 	System.out.println("Async_audioPrepare / onPreExecute" );

		mPrepareDialog = new ProgressDialog(act);
	 	if (!Note_audio.isPausedAtSeekerAnchor)
		{
			mPrepareDialog.setMessage(act.getResources().getText(R.string.audio_message_preparing_to_play));
			mPrepareDialog.setCancelable(true); // set true for enabling Back button
			mPrepareDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //ProgressDialog.STYLE_HORIZONTAL
			//keep LOW_PROFILE for note view
			if(AudioManager.getAudioPlayMode() == AudioManager.PAGE_PLAY_MODE)
				mPrepareDialog.show();
		}

        AudioManager.mIsPrepared = false;
	 } 
	 
	 @Override
	 protected String doInBackground(String... params) 
	 {
         System.out.println("Async_audioPrepare / doInBackground / params[0] = " + params[0] );

		 boolean isTimeOut = false;
		 int progress = 0;
		 int count = 0;

		 while( (!AudioManager.mIsPrepared) && (!isTimeOut) )
		 {
			 System.out.println("Async_audioPrepare / doInBackground / count = " + count);
			 count++;
			 
			 if(count >= 40) // 10 seconds, 1/4 * 40
				 isTimeOut = true;
			 
			 publishProgress(progress);
			 
			 progress =+ 20;
			 if(progress >= 100)
				 progress = 0;
			 
			 try {
				Thread.sleep(Util.oneSecond/4);
			 } catch (InterruptedException e) {
				e.printStackTrace();
			 } 
		 }
		 
		 if(isTimeOut)
			return "timeout";
		 else
			return "ok";
	 }
	
	 @Override
	 protected void onProgressUpdate(Integer... progress) 
	 { 
//		 System.out.println("Async_audioPrepare / OnProgressUpdate / progress[0] " + progress[0] );
	     super.onProgressUpdate(progress);
	     
	     if((mPrepareDialog != null) && mPrepareDialog.isShowing())
	    	 mPrepareDialog.setProgress(progress[0]);
	 }
	 
	 // This is executed in the context of the main GUI thread
	 @Override
	 protected void onPostExecute(String result)
	 {
//	 	System.out.println("Async_audioPrepare / _onPostExecute / result = " + result);
	 	
	 	// dialog off
		if((mPrepareDialog != null) && mPrepareDialog.isShowing())
			mPrepareDialog.dismiss();

		mPrepareDialog = null;

		// show time out
		if(result.equalsIgnoreCase("timeout"))
		{
			Toast toast = Toast.makeText(act.getApplicationContext(), R.string.audio_message_preparing_time_out, Toast.LENGTH_SHORT);
			toast.show();
		}

		// unlock orientation
		Util.unlockOrientation(act);
		// enable rotation
//	 	act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	 }
	 
}

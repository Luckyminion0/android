package com.bumba27.demo_pheludar;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.bumba27.utils.ReusableClass;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditJobPopUp extends Activity{
	
	EditText editTextJobId;
	private ProgressDialog pgLogin;
	String oldJobId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_job_popup);
		
		editTextJobId = (EditText)findViewById(R.id.editTextJobId);
		
		Bundle extras = getIntent().getExtras(); 

		if (extras != null) 
		{
			oldJobId = extras.getString("JobId");
			editTextJobId.setText(oldJobId);
		}
	}
	
//	edit_job_id.php
	public void editingJob(View v) 
	{
		if(ReusableClass.isConnectingToInternet(EditJobPopUp.this))
		{
			//Showing progress dialog 
			pgLogin = new ProgressDialog(EditJobPopUp.this);
			pgLogin.setMessage("Please wait a min ...");
			pgLogin.setIndeterminate(true);
			pgLogin.setCancelable(true);
			pgLogin.setCanceledOnTouchOutside(false);

			pgLogin.show();
			
			new MyAsyncEditJob().execute(editTextJobId.getText().toString(), oldJobId);	

		}
		else
		{
			Toast.makeText(this, "Sorry!! No internet connection.", Toast.LENGTH_LONG).show();
		}
	}
	
	//===================================================================================================================================
	//Creating a job
	//===================================================================================================================================

	private class MyAsyncEditJob extends AsyncTask<String, Integer, Double>{

		String responseBody = null;
		@Override
		protected Double doInBackground(String... params) 
		{
			postData(params[0],params[1]);
			return null;
		}

		protected void onPostExecute(Double result)
		{
			if (pgLogin.isShowing()) 
			{
				pgLogin.cancel();
				pgLogin.dismiss();
			}
			Log.i("TAG", "Server Responce:" + responseBody);
			
			if("YES\n".equalsIgnoreCase(responseBody))
			{
				Toast.makeText(getApplicationContext(), "Your job edited. Thank you.", Toast.LENGTH_LONG).show();
				finish();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Sorry! Job not edited.", Toast.LENGTH_LONG).show();
			}
		}
		protected void onProgressUpdate(Integer... progress){
		}

		public void postData(String jobId, String oldJobId) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(ReusableClass.base_url + "edit_job_id.php");

			try 
			{
				// Data that I am sending
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("job_id", jobId));
				nameValuePairs.add(new BasicNameValuePair("old_job_id", oldJobId));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				responseBody = EntityUtils.toString(response.getEntity());
			} 
			catch (Throwable t ) {
				//Toast.makeText( getApplicationContext(),""+t,Toast.LENGTH_LONG).show();
				Log.d("Error Time of Login",t+"");
			} 
		}
	}
	//===================================================================================================================================
	//END Creating a job
	//===================================================================================================================================

}

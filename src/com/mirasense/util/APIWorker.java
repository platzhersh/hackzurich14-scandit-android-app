package com.mirasense.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.mirasense.demos.ScanditSDKDemo;
import com.mirasense.demos.ScanditSDKSampleBarcodeActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

/***
 * 
 * @author chregi
 * more info on AsyncTasks in Android: http://developer.android.com/training/articles/perf-anr.html
 */
public class APIWorker extends AsyncTask<URL, Integer, String> {

	ScanditSDKSampleBarcodeActivity sourceActivity;
	
	public APIWorker(ScanditSDKSampleBarcodeActivity a) {
		sourceActivity = a;
	}
	
	@Override
	// Do the long-running work in here
	protected String doInBackground(URL... arg0) {
		String url = arg0[0].toString();
		Log.i("custom", "url: " + url);
		
		
        String resp = "";
        try {
	    	HttpClient client = new DefaultHttpClient();
	    	HttpGet request = new HttpGet(url);
	    	Log.i("custom", "Request: "+ request.toString());
	    	
	    	HttpResponse response = client.execute(request);
	    	Log.i("custom", "Response: "+ response.toString());
	    	
	    	
	    	
	    	BufferedReader rd = new BufferedReader
	    			  (new InputStreamReader(response.getEntity().getContent()));
	    			    
	    			String line = "";
	    			while ((line = rd.readLine()) != null) {
	    			  Log.i("custom",line);
	    			  resp += line;
	    			} 
			Log.i("custom", "Response: "+ resp);
			
        } catch (Exception e) {
        	//sourceActivity.showAlert("Exception", e.toString());
        	sourceActivity.showToast("API Worker Exception");
        }        
		return resp;
	}
	// This is called when doInBackground() is finished
	@Override
    protected void onPostExecute(String result) {
    	sourceActivity.showToast(result);
    }

}

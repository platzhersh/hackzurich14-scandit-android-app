package com.mirasense.demos;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.mirasense.scanditsdk.ScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;
import com.mirasense.scanditsdkdemo.R;

/**
 * Simple demo application illustrating the use of the Scandit SDK.
 * 
 * Important information for the developer with respect to Android 2.1 support!
 * 
 * Android 2.1 differs from subsequent versions of Android OS in that it 
 * does not offer a camera preview mode in portrait mode (landscape only). 
 * Android 2.2+ offers both - a camera preview in landscape mode and in portrait 
 * mode. There are certain devices that run Android 2.2+ but do not properly
 * implement the methods needed for a portrait camera view. 
 * 
 * To address this difference between the Android versions, the Scandit SDK 
 * offers the following approaches and the developer needs to choose his 
 * preferred option:
 * 
 * If you are showing the scanner on the full screen in a new Activity:
 * 
 * - Instantiate the ScanditSDKAutoAdjustingBarcodePicker which will choose 
 * whether to use the new or legacy picker.
 * 
 * If you want to show the picker inside a view hierarchy/cropped/scaled you
 * have to make the distinction between the different pickers yourself. Fore 
 * devices that don't support the new picker the following options exist:
 * 
 * - a scan view in landscape mode scanning only(!) that is fully 
 * customizable by the developer - ScanditSDKBarcodePicker.class
 * 
 * - our own custom scan view with portrait mode scanning that offers only 
 * limited customization options (show/hide title & tool bars, 
 * but no additional Android UI elements) -  LegacyPortraitScanditSDKBarcodePicker.class
 * 
 * For devices that do support the new picker the following options exist:
 * 
 * - a scan view with portrait mode scanning that is fully customizable 
 * by the developer (RECOMMENDED) - ScanditSDKBarcodePicker.class
 * 
 * - any of the options listed under Android 2.1
 * 
 * We recommend that developers choose the scan view in portrait mode on Android 2.2.
 * It has the native Android look&feel and provides full customization. We provide our
 * own custom scan view (LegacyPortraitScanditSDKBarcodePicker.class) in Android 2.1
 * to provide backwards compatibility with Android 2.1. 
 *
 * To integrate the Scandit SDK, carry out the following three steps:
 * 
 * 1. Create a BarcodePicker object that manages camera access and 
 *    bar code scanning:
 *    
 *    e.g.
 *    ScanditSDKBarcodePicker barcodePicker = new ScanditSDKBarcodePicker(this, 
 *              R.raw.class, "your app key", true, 
                ScanditSDKBarcodePicker.LOCATION_PROVIDED_BY_SCANDIT_SDK);
 *
 *  IMPORTANT: Make sure add your app key here. It is available from your Scandit SDK account. 
 *
 * 2. Add it to the activity:    
 *    my_activity.setContentView(barcodePicker);
 * 
 * 3. Implement the ScanditSDKListener interface (didCancel, didScanBarcode, 
 *    didManualSearch) and register with the ScanditSDKOverlayView to receive 
 *    callbacks:
 *    barcodePicker.getOverlayView().addListener(this);
 * 
 * 
 * If you want to use the custom scan view for scanning in portrait mode in 
 * Android 2.1, instantiate the LegacyPortraitScanditSDKBarcodePicker
 * class (as shown in the example below). There is utility method available 
 * to determine whenever the default portrait scan view is not available
 * ScanditSDKBarcodePicker.canRunPortraitPicker().
 * 
 * 
 * 
 * Copyright 2010 Mirasense AG
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing premissions and
 * limitations under the License.
 */
public class ScanditSDKDemo extends Activity implements ScanditSDKListener, 
                                                        LocationListener {
	// The main object for scanning barcodes.
	private ScanditSDKBarcodePicker mBarcodePicker;
	
	public static final String sCanditSdkAppKey = "oV/Wzk5DEeSQUeGdK5vfsPz8+7BrOnfnciXpNvsI0qM";
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		if (Build.VERSION.SDK_INT >= 14) {
			super.setTheme(android.R.style.Theme_DeviceDefault);
		} else if (Build.VERSION.SDK_INT >= 11) {
			super.setTheme(android.R.style.Theme_Holo);
		} else {
			super.setTheme(android.R.style.Theme_Black_NoTitleBar);
		}

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        startLocationGathering();
        
        Button settingsButton = (Button) findViewById(R.id.settings);
        settingsButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View v) {
                startActivity(new Intent(ScanditSDKDemo.this, SettingsActivity.class));
        	}
        });

        /* Here we show how to start a new Activity that implements the Scandit
         * SDK as a full screen scanner. The Activity can be found in the
         * ScanditSDKSampleBarcodeActivity in this demo project. The old and
         * new GUIs can both be easily opened this way, which is also shown in 
         * the aforementioned activity. */
        Button activityButton = (Button) findViewById(R.id.fullscreen_button);
        activityButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanditSDKDemo.this, 
                        ScanditSDKSampleBarcodeActivity.class));
            }
        });        
	}
	
	@Override
	protected void onPause() {
	    // When the activity is in the background immediately stop the 
	    // scanning to save resources and free the camera.
	    if (mBarcodePicker != null) {
	        mBarcodePicker.stopScanning();
	    }
        LocationManager locationManager = 
            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
	    super.onPause();
	}
	
	@Override
	protected void onResume() {
	    // Once the activity is in the foreground again, restart scanning.
        if (mBarcodePicker != null) {
            mBarcodePicker.startScanning();
        }
	    super.onResume();
	}

	/** 
     *  Called when a barcode has been decoded successfully.
     *  
     *  @param barcode Scanned barcode content.
     *  @param symbology Scanned barcode symbology.
     */
	@Override
	public void didScanBarcode(String barcode, String symbology) {
		// Example code that would typically be used in a real-world app using 
		// the Scandit SDK.
		/*
		// Access the image in which the bar code has been recognized.
		byte[] imageDataNV21Encoded = barcodePicker.getCameraPreviewImageOfFirstBarcodeRecognition();
		int imageWidth = barcodePicker.getCameraPreviewImageWidth();
		int imageHeight = barcodePicker.getCameraPreviewImageHeight();
		
		// Stop recognition to save resources.
		mBarcodePicker.stopScanning();
		*/
	}
	
	/** 
     * Called when the user entered a bar code manually.
     * 
     * @param entry The information entered by the user.
     */
	@Override
	public void didManualSearch(String entry) {
		// Example code that would typically be used in a real-world app using 
	    // the Scandit SDK.
		/*
		// Access the current camera image.
		byte[] imageDataNV21Encoded = barcodePicker.getMostRecentCameraPreviewImage();
		int imageWidth = barcodePicker.getCameraPreviewImageWidth();
		int imageHeight = barcodePicker.getCameraPreviewImageHeight(); 
		 
		// Stop recognition to save resources.
		mBarcodePicker.stopScanning();
		*/
	}
	
	@Override
	public void didCancel() {
	}
	
	@Override
	public void onBackPressed() {
	    if (mBarcodePicker != null) {
	        mBarcodePicker.stopScanning();
	    }
	    finish();
	}
	
	/**
	 * Starts to gather location data from the finest allowed location provider.
	 * For this to work either one of the following permissions has to be set:
	 * ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION.
	 */
	private void startLocationGathering() {
        LocationManager locationManager = 
            (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 5, 5, this);
        }
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
        LocationManager locationManager = 
            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
	}
	
	@Override
	public void onProviderDisabled(String arg0) {}
	
	@Override
	public void onProviderEnabled(String arg0) {}
	
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
}

package ch.hackzurich.migrozept;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mirasense.scanditsdkdemo.R;
import com.mirasense.scanditsdkdemo.R.id;
import com.mirasense.scanditsdkdemo.R.layout;
import com.mirasense.scanditsdkdemo.R.menu;

import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class RecipeListActivity extends Activity  {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_list);
		
		Intent intent = getIntent();
		String message = intent.getStringExtra(ScanditSDKSampleBarcodeActivity.EXTRA_MESSAGE);
		
		// parse recipes
		try {
			JSONObject obj = new JSONObject(message);;
			
			if (!obj.has("error")) {
				JSONArray jsonArray = obj.getJSONArray("recipes");

				for (int i=0; i<jsonArray.length(); i++) {
				    JSONObject item = jsonArray.getJSONObject(i);
				    TextView name = new TextView(getApplicationContext());
				    name.setText(jsonArray.getJSONObject(i).getString("name"));
				    
				    this.addContentView(name, null);
				    
				}
			}
		}
		catch (Exception e) {
			
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipe_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

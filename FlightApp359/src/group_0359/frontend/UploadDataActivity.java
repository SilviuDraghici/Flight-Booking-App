package group_0359.frontend;

import group_0359.backend.FlightData;
import group_0359.backend.UserData;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class UploadDataActivity extends UserActionBarActivity {

	private static final int USER_RESULT_CODE = 0, FLIGHT_RESULT_CODE = 1;
	private int numBrowsers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_data);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		PackageManager packageManager = this.getPackageManager();
		//checks how many apps are installed that have ACTION_GET_CONTENT
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.GET_ACTIVITIES);
		this.numBrowsers = list.size();
	}

	@Override
	protected void editBar(ActionBar bar) {
		bar.setTitle("Upload Data");
	}
	
	/**
	 * Starts ACTION_GET_CONTENT for user csv
	 * @param view
	 */
	public void browseUser(View view) {
		startPicker(USER_RESULT_CODE);
	}
	
	/**
	 * Starts ACTION_GET_CONTENT for flight csv
	 * @param view
	 */
	public void browseFlight(View view) {
		startPicker(FLIGHT_RESULT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent returned) {
		EditText pathDisplay = null;
		String filePath = null;
		//read filepath from intent
		if (resultCode == RESULT_OK) {
			filePath = returned.getData().getPath();
		}
		//put filepath in correct edittext
		switch (requestCode) {
		case USER_RESULT_CODE:
			pathDisplay = (EditText) findViewById(R.id.user_filepath);
			break;
		case FLIGHT_RESULT_CODE:
			pathDisplay = (EditText) findViewById(R.id.flight_filepath);
			break;
		}
		pathDisplay.setText(filePath);
	}
	
	/**
	 * Uses the filepath in the user edittext 
	 * to try and read user csv.
	 * @param view
	 */
	public void saveUser(View view) {
		EditText userPath = (EditText) findViewById(R.id.user_filepath);
		String path = userPath.getText().toString();
		UserData userData = data.getUserList();
		try {
			userData.addClients(path);
			Toast.makeText(this, "Users Added", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Toast.makeText(this, "Could not read file", Toast.LENGTH_SHORT)
					.show();
		}
		data.saveToFile(userData);
	}
	
	/**
	 * Uses the filepath in the flight edittext 
	 * to try and read flight csv.
	 * @param view
	 */
	public void saveFlight(View view) {
		EditText flightPath = (EditText) findViewById(R.id.flight_filepath);
		String path = flightPath.getText().toString();
		UserData userData = data.getUserList();
		FlightData flightData = userData.getFlightData();
		try {
			flightData.addFlights(path);
			Toast.makeText(this, "Flights added", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, "Could not read file", Toast.LENGTH_SHORT)
					.show();
		}
		data.saveToFile(userData);
	}
	
	private void startPicker(int resultCode) {
		//starts Action_get_content if an app that has it is installed
		if (numBrowsers > 0) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("file/*");
			startActivityForResult(intent, resultCode);
		} else
			Toast.makeText(this,
					"No adequate browser apps installed. Type the filepath",
					Toast.LENGTH_LONG).show();
	}
}

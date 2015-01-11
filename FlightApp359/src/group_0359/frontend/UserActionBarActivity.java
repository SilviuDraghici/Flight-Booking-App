package group_0359.frontend;

import group_0359.backend.FileManager;
import group_0359.backend.NoSuchUserException;
import group_0359.backend.User;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public abstract class UserActionBarActivity extends ActionBarActivity {
	
	/**
     * Stores the manager of this System received from the previous Activity.
     */
	protected FileManager data;
	
	/** Stores the email of the User of the system. */
	protected String email;
	protected User user;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		// Receives FileManager and email from previous Activity.
		data = (FileManager) intent.getExtras().getSerializable(LoginActivity.DATA_KEY);
		email = intent.getExtras().getString(LoginActivity.USER_KEY);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_action_bar, menu);
		ActionBar bar = getSupportActionBar();
		editBar(bar);
		return true;
	}
	
	protected void editBar(ActionBar bar) {
		bar.setTitle("protected void editBar(ActionBar)");		
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem viewClientsItem = menu.findItem(R.id.action_view_all_clients);
		MenuItem viewFlightsItem = menu.findItem(R.id.action_view_all_flights);
		MenuItem viewUploadItem = menu.findItem(R.id.action_upload_system_data);
		if (user.isAdmin()) {
			viewClientsItem.setVisible(true);
			viewFlightsItem.setVisible(true);
			viewUploadItem.setVisible(true);
		}

		// Returns true for menu to be displayed.
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_logout:
			logout();
			return true;
		case R.id.action_view_all_clients:
			viewAllClients();
			return true;
		case R.id.action_view_all_flights:
			viewAllFlights();
			return true;
		case R.id.action_upload_system_data:
			uploadData();
			return true;
		    
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	private void logout() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	private void viewAllClients() {
		Intent intent = new Intent(this, DisplayAllClientsActivity.class);
		intent.putExtra(LoginActivity.DATA_KEY, data);
		intent.putExtra(LoginActivity.USER_KEY, email);
		startActivity(intent);
	}

	private void viewAllFlights() {
		Intent intent = new Intent(this, DisplayAllFlightsActivity.class);
		intent.putExtra(LoginActivity.DATA_KEY, data);
		intent.putExtra(LoginActivity.USER_KEY, email);
		startActivity(intent);
	}

	private void uploadData() {
		Intent intent = new Intent(this, UploadDataActivity.class);
		intent.putExtra(LoginActivity.DATA_KEY, data);
		intent.putExtra(LoginActivity.USER_KEY, email);
		startActivity(intent);
	}
	
	public void onResume(){
		super.onResume();
		try {
			user = data.getUserList().getUser(email);
		} catch (NoSuchUserException e) {
			e.printStackTrace();
		}
	}
}

package group_0359.frontend;

import group_0359.backend.NoSuchUserException;
import group_0359.backend.User;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class UserActivity extends UserActionBarActivity {

	private String displayEmail;
	private User displayUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Intent intent = getIntent();
		displayEmail = intent.getExtras().getString(LoginActivity.DISPLAY_KEY);
	}

	/**
	 * Refreshes displayed information.
	 */
	public void onResume() {
		super.onResume();
		try {
			displayUser = data.getUserList().getUser(displayEmail);
		} catch (NoSuchUserException e) {
			e.printStackTrace();
		}
		displayInfo();
		editBar(getSupportActionBar());
	}
	
	/**
	 * Launches EditUserActivity.
	 * @param view
	 */
	public void toEditUserActivity(View view) {
		Intent intent = new Intent(this, EditUserActivity.class);
		addExtras(intent);
		startActivity(intent);
	}
	
	/**
	 * Launches DisplayBookingsActivity.
	 * @param view
	 */
	public void viewBookedItineraries(View view) {
		Intent intent = new Intent(this, DisplayBookingsActivity.class);
		addExtras(intent);
		startActivity(intent);
	}
	
	/**
	 * Launches SearchBookingsActivity.
	 * @param view
	 */
	public void searchForItineraries(View view) {
		Intent intent = new Intent(this, SearchBookingsActivity.class);
		addExtras(intent);
		startActivity(intent);
	}
	
	/**
	 * Sets actionbar text.
	 */
	@Override
	protected void editBar(ActionBar bar) {
		User user = null;
		try {
			user = data.getUserList().getUser(displayEmail);
		} catch (NoSuchUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String status = "Client: %s %s";
		if (displayUser.isAdmin())
			status = "Admin: %s %s";
		String title = String.format(status, user.getFirstName(),
				user.getLastName());
		bar.setTitle(title);
	}
	
	private void displayInfo() {
		TextView email = (TextView) findViewById(R.id.display_email);
		email.setText(displayUser.getEmail());
		TextView address = (TextView) findViewById(R.id.display_address);
		address.setText(displayUser.getAddress());
		TextView creditcard = (TextView) findViewById(R.id.display_creditcard);
		creditcard.setText(displayUser.getCreditCardNumber());
		TextView expirydate = (TextView) findViewById(R.id.display_expirydate);
		expirydate.setText(displayUser.getExpiryDate());
		TextView numBooked = (TextView) findViewById(R.id.num_booked);
		String nums = "%s Booked";
		numBooked.setText(String.format(nums, displayUser
				.getBookedItineraries().size()));
	}
	
	private void addExtras(Intent intent) {
		//adds extras to given intent
		intent.putExtra(LoginActivity.DATA_KEY, data);
		intent.putExtra(LoginActivity.USER_KEY, email);
		intent.putExtra(LoginActivity.DISPLAY_KEY, displayEmail);
	}
}

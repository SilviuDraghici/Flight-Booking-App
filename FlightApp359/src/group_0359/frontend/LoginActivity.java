package group_0359.frontend;

import group_0359.backend.FileManager;
import group_0359.backend.NoSuchUserException;

import java.io.File;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	private FileManager data;
	//keys for intent extras
	public static final String DATA_KEY = "data_key", USER_KEY = "user_key";
	public static final String DISPLAY_KEY = "C_K", FLIGHT_ID_KEY = "F_K";
	public static final String ITINERARY_KEY = "I_K";
	private final String PASSWORD = "passwords.txt";
	private final String FILENAME = "users.data";
	private final String DIR = "data";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// create filemanager with correct filepaths
		data = new FileManager(getPath(FILENAME), getPath(PASSWORD));
	}

	/**
	 * Removes the text previously in the text fields. Gives all newly created
	 * users default passwords.
	 */
	public void onResume() {
		super.onResume();
		EditText emailField = (EditText) findViewById(R.id.email);
		EditText passwordField = (EditText) findViewById(R.id.password);
		emailField.setText("");
		passwordField.setText("");
		data.addPasswords();
	}

	/**
	 * Attempts to log in a user with the given email and password.
	 * 
	 * @param view
	 */
	public void attemptLogin(View view) {
		// gets the text fields
		EditText emailField = (EditText) findViewById(R.id.email);
		String email = emailField.getText().toString();

		EditText passwordField = (EditText) findViewById(R.id.password);
		String enteredPassword = passwordField.getText().toString();

		// gets email, password pairs
		HashMap<String, String> pwords = data.getPasswords();
		String password = pwords.get(email);

		// launches useractivity is correct email and password are entered.
		Intent intent;
		if (password != null) {
			if (enteredPassword.equals(password)) {
				try {
					data.getUserList().getUser(email);
					intent = new Intent(this, UserActivity.class);
					intent.putExtra(DATA_KEY, data);
					intent.putExtra(USER_KEY, email);
					intent.putExtra(DISPLAY_KEY, email);
					startActivity(intent);
				} catch (NoSuchUserException e) {
					Toast.makeText(this, "No Such User", Toast.LENGTH_LONG)
							.show();
				}
			} else {
				Toast.makeText(this, "Incorrect Password ", Toast.LENGTH_LONG)
						.show();
			}
		} else {
			Toast.makeText(this, "Incorrect Email", Toast.LENGTH_LONG).show();
		}
		try {

		} catch (Exception e) {

		}
	}

	private String getPath(String fileName) {
		// gets the path to the file with the input name
		File directory = this.getApplicationContext().getDir(DIR, MODE_PRIVATE);
		File dataLocation = new File(directory, fileName);
		return dataLocation.getPath();
	}

	// public void onDestroy(){
	// super.onDestroy();
	// Toast.makeText(this,"Saving", Toast.LENGTH_LONG).show();
	// User admin2 = new
	// User("Draghici","Silviu","s","1251 Canborough Cres.","123456789123","2017-03-11",true);
	// UserData userData = data.getUserList();
	// userData.addUser(admin2);
	// data.saveToFile(userData);
	// data.changePassword("s","p");
	// }
}

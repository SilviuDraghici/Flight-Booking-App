package group_0359.frontend;

import group_0359.backend.DateTime;
import group_0359.backend.User;
import group_0359.backend.UserData;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewClientActivity extends UserActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_client);
	}

	public void saveNewClient(View view) {
		EditText emailEdit = (EditText) findViewById(R.id.email_edit);
		EditText firstNameEdit = (EditText) findViewById(R.id.first_name_edit);
		EditText lastNameEdit = (EditText) findViewById(R.id.last_name_edit);
		EditText addressEdit = (EditText) findViewById(R.id.address_edit);
		EditText creditCardNumEdit = (EditText) findViewById(R.id.credit_card_num_edit);
		EditText creditCardExpiryYearEdit = (EditText) findViewById(R.id.expiry_year_edit);
		EditText creditCardExpiryMonthEdit = (EditText) findViewById(R.id.expiry_month_edit);
		EditText creditCardExpiryDayEdit = (EditText) findViewById(R.id.expiry_day_edit);
		String email = emailEdit.getText().toString();
		int expiryCardYear = strToInt(creditCardExpiryYearEdit.getText()
				.toString());
		int expiryCardMonth = strToInt(creditCardExpiryMonthEdit.getText()
				.toString());
		int expiryCardDay = strToInt(creditCardExpiryDayEdit.getText()
				.toString());

		// Saves Client/User changes to System if Credit card Expiry
		// Date is value, alerts User with message otherwise.
		if (DateTime.validDate(expiryCardYear, expiryCardMonth, expiryCardDay)) {
		    if (!data.getUserList().getClientIds().contains(email) || 
		            !data.getUserList().getAdminIds().contains(email) ||
		            !email.equals("")) {
			// All changes valid, so get Client/User reference from
			// system.
			UserData userData = data.getUserList();
			User newUser = new User("e", "f", email, "d", "", "");
			newUser.setFirstName(firstNameEdit.getText().toString());
			newUser.setLastName(lastNameEdit.getText().toString());
			newUser.setAddress(addressEdit.getText().toString());
			newUser.setCreditCardNumber(creditCardNumEdit.getText().toString());
			String expiryDate = DateTime.getDateFormatted(expiryCardYear,
					expiryCardMonth, expiryCardDay);
			newUser.setExpiryDate(expiryDate);
			userData.addUser(newUser);
			data.saveToFile(userData);
			finish();
		    } else {
		        showShortMessage("Invalid email or email already in use.");
		    }
		} else {
			showShortMessage("Invalid credit card expiry date.");
		}
	}

	public void cancelNewClient(View view) {
		finish();
	}

	/**
	 * Shows a short given message to alert the User of the System in this
	 * Activity.
	 * 
	 * @param message
	 *            the given message to alert the User of the System in this
	 *            Activity.
	 */
	private void showShortMessage(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * Returns primitive integer value of a given String. Returns 0 if the given
	 * String is empty.
	 * 
	 * @param str
	 *            the given String to return a primitive integer value from
	 */
	private int strToInt(String str) {
		if (str.equals("")) {
			return 0;
		}
		return Integer.parseInt(str);
	}
	
    @Override
    protected void editBar(ActionBar bar) {
        bar.setTitle("Create Client");

        // Sets action bar/options menu to show unique ID/email
        // of the User of System.
        getSupportActionBar().setSubtitle(email);
    }
}

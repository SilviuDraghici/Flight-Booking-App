package group_0359.frontend;

import group_0359.backend.DateTime;
import group_0359.backend.NoSuchUserException;
import group_0359.backend.User;
import group_0359.backend.UserData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditUserActivity extends UserActionBarActivity {

    /** Stores the email of the User to edit. */
    private String displayEmail;

    // Stores edit text fields for the Client to be edited. 
    private EditText firstNameEdit;
    private EditText lastNameEdit;
    private EditText addressEdit;
    private EditText creditCardNumEdit;
    private EditText creditCardExpiryYearEdit;
    private EditText creditCardExpiryMonthEdit;
    private EditText creditCardExpiryDayEdit;

    /**
     * Receives unique client User id/email and sets
     * edit text with stored values of client User with the
     * received id.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Intent intent = getIntent();

        // Receives the User/Client email of User to edit.
        displayEmail = (String) intent
                .getSerializableExtra(LoginActivity.DISPLAY_KEY);

        // Sets current changeable values of the Flight being edited
        // by this Activity into their respective edit text fields.
        try {
            setsViewOfUserValues(data.getUserList().getUser(displayEmail));
        } catch (NoSuchUserException e) {
            showShortMessage("ERROR: Missing system user: " + displayEmail);
        }
    }

    /**
     * Sets text view for action bar.
     */
    @Override
    protected void editBar(ActionBar bar) {
        
        // Different title depending on whether
        // it is a User editing him/herself or an
        // administrator editing a Client.
        if (displayEmail.equals(email)) {
            bar.setTitle("Edit User");
        } else {
            bar.setTitle("Edit Client");
        }

        // Sets action bar/options menu to show unique ID/email
        // of the User of System.
        getSupportActionBar().setSubtitle(email);
    }

    /**
     * Moves back to viewing the Client/User with the given Client/User 
     * ID email without making any changes. This is a button action.
     * @param view the view of the cancel button in this Activity
     */
    public void cancelUser(View view) {
        finish();
    }

    /**
     * Saves changes into the stored Client/User with the same id email if the
     * changes are valid and moves to the previous Activity to view the 
     * Client/User with the given Client/User email with the changes. This is 
     * a button action.
     * @param view the view of the save button in this Activity
     */
    public void saveUser(View view) {
        int expiryCardYear = 
                strToInt(creditCardExpiryYearEdit.getText().toString());
        int expiryCardMonth = 
                strToInt(creditCardExpiryMonthEdit.getText().toString());
        int expiryCardDay = 
                strToInt(creditCardExpiryDayEdit.getText().toString());
        boolean noExpiryDate = (expiryCardYear == 0 
                                && expiryCardMonth == 0
                                && expiryCardDay == 0);
        EditText passwordEdit = (EditText) findViewById(R.id.password_edit);
        String password = passwordEdit.getText().toString();

        // Saves Client/User changes to System if Credit card Expiry 
        // Date is value, alerts User with message otherwise.
        if (!DateTime.validDate(expiryCardYear, expiryCardMonth, expiryCardDay)
                && !noExpiryDate) {
            showShortMessage("Invalid credit card expiry date.");
        } else {

            // All changes valid, so get Client/User reference from
            // system.
            UserData userData = data.getUserList();
            try {
                User user = userData.getUser(displayEmail);  
                user.setFirstName(firstNameEdit.getText().toString());
                user.setLastName(lastNameEdit.getText().toString());
                user.setAddress(addressEdit.getText().toString());
                user.setCreditCardNumber(creditCardNumEdit.getText()
                                         .toString());
                
                // Sets new credit card expiry date.
                String expiryDate;
                if (noExpiryDate) {
                    expiryDate = new String("");
                } else {
                    expiryDate = DateTime.getDateFormatted(expiryCardYear,
                                                           expiryCardMonth,
                                                           expiryCardDay);
                }
                user.setExpiryDate(expiryDate);
                data.saveToFile(userData);
                data.changePassword(displayEmail, password);
                finish();
            } catch (NoSuchUserException e) {
                showShortMessage("ERROR: Missing system user: " 
                                 + displayEmail);
            }
        }
    }

    /**
     * Sets view for edit view values of received User in this
     * Activity.
     * @param user the received user to edit in this Activity
     */
    private void setsViewOfUserValues(User user) {

        // Sets text view of the unique email of the User being
        // edited by this Activity.
        TextView emailView = (TextView) findViewById(R.id.email_edit);
        emailView.setText(user.getEmail());

        // Sets values of editable text fields for the User being
        // edited by this Activity.
        firstNameEdit = (EditText) findViewById(R.id.first_name_edit);
        lastNameEdit = (EditText) findViewById(R.id.last_name_edit);
        addressEdit = (EditText) findViewById(R.id.address_edit);
        creditCardNumEdit = (EditText) findViewById(R.id.credit_card_num_edit);
        creditCardExpiryYearEdit = (EditText) 
                findViewById(R.id.expiry_year_edit);
        creditCardExpiryMonthEdit = (EditText) 
                findViewById(R.id.expiry_month_edit);
        creditCardExpiryDayEdit = (EditText) 
                findViewById(R.id.expiry_day_edit);
        firstNameEdit.setText(user.getFirstName());
        lastNameEdit.setText(user.getLastName());
        addressEdit.setText(user.getAddress());
        EditText passwordEdit = (EditText) findViewById(R.id.password_edit);
        String password;
        try {
			password = data.getPassword(displayEmail);
		} catch (NoSuchUserException e) {
			password = "";
		}
        passwordEdit.setText(password);
        creditCardNumEdit.setText(user.getCreditCardNumber());
        
        // Sets expiry date edit text view.
        String expiryDate = user.getExpiryDate();
        if (!expiryDate.equals("")) {
            String[] dateValues = user.getExpiryDate().split("-");
            creditCardExpiryYearEdit.setText(dateValues[0]);
            creditCardExpiryMonthEdit.setText(dateValues[1]);
            creditCardExpiryDayEdit.setText(dateValues[2]);
        }
    }

    /**
     * Shows a short given message to alert the User of the System in this
     * Activity.
     * @param message the given message to alert the User of the System in this
     *            Activity.
     */
    private void showShortMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
        .show();
    }

    /**
     * Returns primitive integer value of a given String. Returns 0 if
     * the given String is empty.
     * @param str the given String to return a primitive integer value
     *        from
     */
    private int strToInt(String str) {
        if (str.equals("")) {
            return 0;
        }
        return Integer.parseInt(str);
    }
}

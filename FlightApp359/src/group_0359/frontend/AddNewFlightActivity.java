package group_0359.frontend;

import group_0359.backend.DateTime;
import group_0359.backend.Flight;
import group_0359.backend.UserData;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewFlightActivity extends UserActionBarActivity {

    // /*The New Flight in this Activity. */
    private Flight newFlight;

    private EditText flightNumberEdit;
    private EditText airlineEdit;
    private EditText departureCityEdit;
    private EditText departureYearEdit;
    private EditText departureMonthEdit;
    private EditText departureDayEdit;
    private EditText departureHourEdit;
    private EditText departureMinuteEdit;
    private EditText arrivalCityEdit;
    private EditText arrivalYearEdit;
    private EditText arrivalMonthEdit;
    private EditText arrivalDayEdit;
    private EditText arrivalHourEdit;
    private EditText arrivalMinuteEdit;
    private EditText costDollarEdit;
    private EditText costCentEdit;
    private EditText numSeatsEdit;

    // Stores the current date and time values in the edit Fields.
    private int departureYear;
    private int departureMonth;
    private int departureDay;
    private int departureHour;
    private int departureMinute;
    private int arrivalYear;
    private int arrivalMonth;
    private int arrivalDay;
    private int arrivalHour;
    private int arrivalMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_flight);

        flightNumberEdit = (EditText) findViewById(R.id.flight_num_edit);
        airlineEdit = (EditText) findViewById(R.id.airline_edit);
        costDollarEdit = (EditText) findViewById(R.id.flight_dollar_edit);
        costCentEdit = (EditText) findViewById(R.id.flight_cent_edit);
        numSeatsEdit = (EditText) findViewById(R.id.num_available_seats_edit);
        departureCityEdit = (EditText) findViewById(R.id.departure_city_edit);
        arrivalCityEdit = (EditText) findViewById(R.id.arrival_city_edit);
        departureYearEdit = (EditText) findViewById(R.id.departure_year_edit);
        departureMonthEdit = (EditText) findViewById(R.id.departure_month_edit);
        departureDayEdit = (EditText) findViewById(R.id.departure_day_edit);
        departureHourEdit = (EditText) findViewById(R.id.departure_hour_edit);
        departureMinuteEdit = (EditText) findViewById(R.id.departure_minute_edit);
        arrivalYearEdit = (EditText) findViewById(R.id.arrival_year_edit);
        arrivalMonthEdit = (EditText) findViewById(R.id.arrival_month_edit);
        arrivalDayEdit = (EditText) findViewById(R.id.arrival_day_edit);
        arrivalHourEdit = (EditText) findViewById(R.id.arrival_hour_edit);
        arrivalMinuteEdit = (EditText) findViewById(R.id.arrival_minute_edit);
    }

    public void addFlight(View view){		
        
        // All changes valid, so get Flight reference from
        // system.
        UserData userData = data.getUserList();

        // Sets date and time fields store in this Activity.
        setDateTimeValues();

        // Gets new Flight number.
        String newFlightNum = flightNumberEdit.getText().toString();

        // Sets destination and origin cities to store in this
        // Activity
        String destinationCity = arrivalCityEdit.getText().toString();
        String originCity = departureCityEdit.getText().toString();

        // Checks for valid departure date and time input, if
        // invalid outputs a message informing the user of the
        // System.
        if (!validDateTime(departureYear, departureMonth, 
                departureDay, departureHour, departureMinute)) {
            showShortMessage("Invalid Departure Date or Time.");
        } else if ((!validDateTime(arrivalYear, arrivalMonth, 
                arrivalDay, arrivalHour, arrivalMinute))) {
            showShortMessage("Invalid Arrival Date or Time.");
        } else if (destinationCity.equals("") || originCity.equals("")) {
            showShortMessage("Please enter city names");
        } else if (data.getUserList().getFlightData()
                .getFlightIds().contains(newFlightNum)
                || newFlightNum.equals("")) {
            showShortMessage("Flight number already in use.");
        } else {

            // Creates new DateTime for arrival and departure if the
            // values for date and time are valid.
            DateTime departDateTime = new DateTime(departureYear, departureMonth,
                    departureDay, departureHour, departureMinute);
            DateTime arrivalDateTime = new DateTime(arrivalYear, arrivalMonth,
                    arrivalDay, arrivalHour, arrivalMinute);

            // Checks that the arrival date and time takes place after
            // departure date and time. If not, then outputs a message
            // informing the user of the system.
            if (arrivalDateTime.compareTo(departDateTime) > 0) {

                // Edit text values makes a valid Flight.
                // Gets cost value for new Flight.
                Double cost = strToInt(costDollarEdit.getText().toString())
                        + strToInt(costCentEdit.getText().toString())
                        / 100.0;

                // Creates a new Flight.
                newFlight = new Flight(
                        newFlightNum,
                        departDateTime, arrivalDateTime,
                        airlineEdit.getText().toString(),
                        originCity, destinationCity,
                        cost, 
                        strToInt(numSeatsEdit.getText().toString()));

                //Stores changes and adds Flight
                userData.getFlightData().addFlight(newFlight);

                // Stores changes to System.
                data.saveToFile(userData); 

                // Close and returns to previous activity
                finish();
            } else {
                showShortMessage("Destination time not after arrival time");
            }
        }
    }

    public void cancelNewFlight(View view){
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

    private void setDateTimeValues() {
        departureYear = strToInt(departureYearEdit.getText().toString());
        departureMonth = strToInt(departureMonthEdit.getText().toString());
        departureDay = strToInt(departureDayEdit.getText().toString());
        departureHour = strToInt(departureHourEdit.getText().toString());
        departureMinute = strToInt(departureMinuteEdit.getText().toString());
        arrivalYear = strToInt(arrivalYearEdit.getText().toString());
        arrivalMonth = strToInt(arrivalMonthEdit.getText().toString());
        arrivalDay = strToInt(arrivalDayEdit.getText().toString());
        arrivalHour = strToInt(arrivalHourEdit.getText().toString());
        arrivalMinute = strToInt(arrivalMinuteEdit.getText().toString());
    }

    /**
     * Returns true of the given year, month, day, hour and minute values makes
     * up a valid date and time for DateTime. Returns false otherwise.
     * 
     * @param year the year value of the date to validate
     * @param month the month value of the date to validate
     * @param day the day value of the date to validate
     * @param hour the hour value of the time to validate
     * @param minute the minute value of the time to validate
     * @return true of the given year, month, day, hour and minute values makes
     *         up a valid date and time for DateTime.
     */
    private boolean validDateTime(int year, int month, int day, int hour,
            int minute) {
        return (DateTime.validDate(year, month, day) && DateTime.validTime(
                hour, minute));
    }

    /**
     * Returns primitive integer value of a given String. Returns 0 if the 
     * given String is empty.
     * 
     * @param str the given String to return a primitive integer value from
     */
    private int strToInt(String str) {
        if (str.equals("")) {
            return 0;
        }
        return Integer.parseInt(str);
    }
    
    @Override
    protected void editBar(ActionBar bar) {
        bar.setTitle("New Flight");

        // Sets action bar/options menu to show unique ID/email
        // of the User of System.
        getSupportActionBar().setSubtitle(email);
    }
}

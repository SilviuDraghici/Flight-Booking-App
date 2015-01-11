package group_0359.frontend;

import java.util.List;

import group_0359.backend.DateTime;
import group_0359.backend.Flight;
import group_0359.backend.NoSuchFlightsException;
import group_0359.backend.UserData;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class EditFlightActivity extends UserActionBarActivity {

    /**
     * Stores the flight Id of a Flight stored in this System passed by the
     * previous activity.
     */
    private String flightId;

    /**
     * Stores viewable and changeable travel time which depends on the current
     * date and time values in the edit fields.
     */
    private TextView travelTimeEdit;

    // Stores edit text fields for the Flight to be edited.
    private EditText airlineEdit;
    private EditText costDollarEdit;
    private EditText costCentEdit;
    private EditText numSeatsEdit;
    private EditText departCityEdit;
    private EditText departYearEdit;
    private EditText departMonthEdit;
    private EditText departDayEdit;
    private EditText departHourEdit;
    private EditText departMinuteEdit;
    private EditText arrivalCityEdit;
    private EditText arrivalYearEdit;
    private EditText arrivalMonthEdit;
    private EditText arrivalDayEdit;
    private EditText arrivalHourEdit;
    private EditText arrivalMinuteEdit;

    // Views and adapters for viewing all bookers of this Flight.
    private ListView bookersListView;
    private TextView bookersTitleView;
    private ScrollView flightInfoView;
    private ArrayAdapter<String> adapter;

    // Stores the current date and time values in the edit Fields.
    private int departYear;
    private int departMon;
    private int departDay;
    private int departHour;
    private int departMin;
    private int arriveYear;
    private int arriveMon;
    private int arriveDay;
    private int arriveHour;
    private int arriveMin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flight);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.
                SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Receives FileManager and UserId from previous Activity.
        // Also receives Flight number/ID of Flight to edit.
        Intent intent = getIntent();
        
        // Receives the Flight number/ID of Flight to edit in this
        // Activity.
        flightId = (String) intent
                .getSerializableExtra(LoginActivity.FLIGHT_ID_KEY);

        // Gets the Flight with given number to edit in this Activity
        // and show the current values of the fields in the Flight to
        // User of the System.
        Flight flight;
        TextView flightNumView = (TextView) 
                findViewById(R.id.flight_number_edit);
        flightNumView.setText(flightId);
        try {
            
            // Initializes editable text views of the received Flight.
            flight = data.getUserList().getFlightData().getFlight(flightId);
            initializeEditTexts();
            setFlightValues(flight);
            setTextWatchersToEditTexts();
            
            // Initializes views for looking at bookers and set list view
            // values showing all the bookers of the received Flight.
            bookersTitleView = (TextView) findViewById(R.id.bookers_title);
            bookersListView = (ListView) findViewById(R.id.bookers_list);
            flightInfoView = (ScrollView) findViewById(R.id.flight_info_view);
            initializeBookersList(flight);
        } catch (NoSuchFlightsException e) {
            actionIfNoFlightWithNumber();
        }        
    }
    
    @Override
    protected void editBar(ActionBar bar) {
        bar.setTitle("Edit Flight");
        
        // Sets action bar/options menu to show unique ID/email
        // of the User of System.
        getSupportActionBar().setSubtitle(email);
    }

    /**
     * Moves back to viewing the Flight with the given Flight ID number without
     * making any changes. This is a button action.
     * @param view
     */
    public void cancelFlight(View view) {
        finish();
    }

    /**
     * Saves changes into the stored Flight with the same id number if the
     * changes are valid and moves to the previous Activity to view the Flight
     * with the given Flight ID number with the changes. This is a button
     * action.
     * @param view
     */
    public void saveFlight(View view) {

        // Sets date and time fields store in this Activity.
        setDateTimeValues();
        
        // Sets destination and origin cities to store in this
        // Activity
        String destinationCity = arrivalCityEdit.getText().toString();
        String originCity = departCityEdit.getText().toString();

        // Checks for valid departure date and time input, if
        // invalid outputs a message informing the user of the
        // System.
        if (destinationCity.equals("") || originCity.equals("")) {
            showShortMessage("Please enter city names");
        }else if (!validDateTime(departYear, 
                                 departMon, 
                                 departDay, 
                                 departHour,
                                 departMin)) {
            showShortMessage("Invalid departure date or time");
        } else if ((!validDateTime(arriveYear, 
                                   arriveMon, 
                                   arriveDay,
                                   arriveHour, 
                                   arriveMin))) {
            showShortMessage("Invalid arrival date or time");
        } else {

            // Creates new DateTime for arrival and departure if the
            // values for date and time are valid.
            DateTime departDateTime = new DateTime(departYear, 
                                                   departMon,
                                                   departDay, 
                                                   departHour, 
                                                   departMin);
            DateTime arrivalDateTime = new DateTime(arriveYear, 
                                                    arriveMon,
                                                    arriveDay, 
                                                    arriveHour, 
                                                    arriveMin);

            // Checks that the arrival date and time takes place after
            // departure date and time. If not, then outputs a message
            // informing the user of the system.
            if (arrivalDateTime.compareTo(departDateTime) <= 0) {
                showShortMessage("Destination time not after arrival time");
            } else {
                
                // All changes are valid, so get Flight reference from System,
                // sets changes and saves it into the System using received
                // FileManager object.
                UserData userData = data.getUserList();
                try {

                    // Gets Flight reference from System and
                    // sets validated changes to the referenced
                    // Flight.
                    Flight FlightToEdit = userData.getFlightData().getFlight(
                            flightId);
                    FlightToEdit.setAirline(airlineEdit.getText().toString());
                    FlightToEdit.setArrivalDateTime(arrivalDateTime);
                    Double cost = strToInt(costDollarEdit.getText().toString())
                            + strToInt(costCentEdit.getText().toString())
                            / 100.0;
                    FlightToEdit.setCost(cost);
                    FlightToEdit.setDepartDateTime(departDateTime);
                    FlightToEdit.setDestination(destinationCity);
                    FlightToEdit.setOrigin(originCity);
                    FlightToEdit.setNumSeats(strToInt(numSeatsEdit.getText()
                            .toString()));

                    // Stores changes to system.
                    data.saveToFile(userData);
                    finish();
                } catch (NoSuchFlightsException e) {
                    actionIfNoFlightWithNumber();
                }
            }
        }
    }

    /**
     * Closes this Activity and moves back to viewing the User of the System if
     * there is no Flight found in the System with the given Flight number. This
     * is the action to take if somehow no Flight with the received Flight ID
     * number was found in the System.
     */
    private void actionIfNoFlightWithNumber() {
        showShortMessage("No Flight in system with Flight number " + flightId);
        finish();
    }

    /**
     * Initializes EditText Flight fields for this Activity.
     */
    private void initializeEditTexts() {
        airlineEdit = (EditText) findViewById(R.id.airline_edit);
        costDollarEdit = (EditText) findViewById(R.id.flight_dollar_edit);
        costCentEdit = (EditText) findViewById(R.id.flight_cent_edit);
        numSeatsEdit = (EditText) findViewById(R.id.num_available_seats_edit);
        departCityEdit = (EditText) findViewById(R.id.departure_city_edit);
        departYearEdit = (EditText) findViewById(R.id.departure_year_edit);
        departMonthEdit = (EditText) findViewById(R.id.departure_month_edit);
        departDayEdit = (EditText) findViewById(R.id.departure_day_edit);
        departHourEdit = (EditText) findViewById(R.id.departure_hour_edit);
        departMinuteEdit = (EditText) findViewById(R.id.departure_minute_edit);
        arrivalCityEdit = (EditText) findViewById(R.id.arrival_city_edit);
        arrivalYearEdit = (EditText) findViewById(R.id.arrival_year_edit);
        arrivalMonthEdit = (EditText) findViewById(R.id.arrival_month_edit);
        arrivalDayEdit = (EditText) findViewById(R.id.arrival_day_edit);
        arrivalHourEdit = (EditText) findViewById(R.id.arrival_hour_edit);
        arrivalMinuteEdit = (EditText) findViewById(R.id.arrival_minute_edit);
    }
    
    /**
     * Initializes list of Bookers for Flight to edit in this Activity.
     * @param flight the received Flight to edit in this Activity
     */
    private void initializeBookersList(Flight flight) {
        try {
            List<String> bookers = data.getUserList().getFlightData().getFlight(flightId).getBookers();
            // Initializes adapter for ListView with the list of Flight numbers.
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, bookers);
            bookersListView.setAdapter(adapter);
        } catch (NoSuchFlightsException e) {
            actionIfNoFlightWithNumber();
        }
    }

    /**
     * Watches for all date and time editable fields in this Activity and
     * changes the viewable travel time text accordingly.
     */
    private final TextWatcher travelTimeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {

            // Sets date and time values stored in this Activity to
            // the values currently in the editable fields.
            setDateTimeValues();
        }
        @Override
        public void afterTextChanged(Editable s) {

            // If date and time values are not valid for creating
            // DateTime, show ? hours and ? minutes for travel time,
            // otherwise, show the time difference between the arrival
            // and departure date and time currently in the edit text
            // fields.
            if (!validDateTime(departYear, departMon, departDay, departHour,
                    departMin)
                    || (!validDateTime(arriveYear, arriveMon, arriveDay,
                            arriveHour, arriveMin))) {
                travelTimeEdit.setText("? Hours ? Minutes");
            } else {
                DateTime departDateTime = new DateTime(departYear, departMon,
                        departDay, departHour, departMin);
                DateTime arrivalDateTime = new DateTime(arriveYear, arriveMon,
                        arriveDay, arriveHour, arriveMin);
                double travelTime = arrivalDateTime.timeDiff(departDateTime);
                travelTimeEdit.setText(hoursToString(travelTime));
            }
        }
    };

    /**
     * Sets the instance values in this Activity for telling date and time to
     * what is the current values in the edit text fields.
     */
    private void setDateTimeValues() {
        departYear = strToInt(departYearEdit.getText().toString());
        departMon = strToInt(departMonthEdit.getText().toString());
        departDay = strToInt(departDayEdit.getText().toString());
        departHour = strToInt(departHourEdit.getText().toString());
        departMin = strToInt(departMinuteEdit.getText().toString());
        arriveYear = strToInt(arrivalYearEdit.getText().toString());
        arriveMon = strToInt(arrivalMonthEdit.getText().toString());
        arriveDay = strToInt(arrivalDayEdit.getText().toString());
        arriveHour = strToInt(arrivalHourEdit.getText().toString());
        arriveMin = strToInt(arrivalMinuteEdit.getText().toString());
    }

    /**
     * Sets TextWatcher for all editable fields of Flight that would change
     * travel time of changed Flight.
     */
    private void setTextWatchersToEditTexts() {
        departYearEdit.addTextChangedListener(travelTimeWatcher);
        departMonthEdit.addTextChangedListener(travelTimeWatcher);
        departDayEdit.addTextChangedListener(travelTimeWatcher);
        departHourEdit.addTextChangedListener(travelTimeWatcher);
        departMinuteEdit.addTextChangedListener(travelTimeWatcher);
        arrivalCityEdit.addTextChangedListener(travelTimeWatcher);
        arrivalYearEdit.addTextChangedListener(travelTimeWatcher);
        arrivalMonthEdit.addTextChangedListener(travelTimeWatcher);
        arrivalDayEdit.addTextChangedListener(travelTimeWatcher);
        arrivalHourEdit.addTextChangedListener(travelTimeWatcher);
        arrivalMinuteEdit.addTextChangedListener(travelTimeWatcher);
    }

    /**
     * Sets the editable and non-editable values of the given Flight to be
     * viewed and edited in this Activity.
     * 
     * @param flight the given Flight from which to edit its values.
     */
    private void setFlightValues(Flight flight) {

        // Sets text view of the unique flight number of ID number of the
        // flight passed on to this Activity and the uneditable travel time.
        travelTimeEdit = (TextView) findViewById(R.id.travel_time_edit);
        travelTimeEdit.setText(hoursToString(flight.getTravelTime()));

        // Sets current changeable values of the Flight being edited
        // by this Activity into their respective edit text fields.
        airlineEdit.setText(flight.getAirline());
        int dollars = (int) flight.getCost();
        int cents = (int) (flight.getCost() - dollars) * 100;
        costDollarEdit.setText(String.valueOf(dollars));
        costCentEdit.setText((String.valueOf(cents) + "00").substring(0, 2));
        numSeatsEdit.setText(String.valueOf(flight.getNumSeats()));
        departCityEdit.setText(flight.getOrigin());
        departYearEdit.setText(String.valueOf(flight.getDepartDateTime()
                .getYear()));
        departMonthEdit.setText(String.valueOf(flight.getDepartDateTime()
                .getMonth()));
        departDayEdit.setText(String.valueOf(flight.getDepartDateTime()
                .getDay()));
        departHourEdit.setText(String.valueOf(flight.getDepartDateTime()
                .getHour()));
        departMinuteEdit.setText(String.valueOf(flight.getDepartDateTime()
                .getMinute()));
        arrivalCityEdit.setText(flight.getDestination());
        arrivalYearEdit.setText(String.valueOf(flight.getArrivalDateTime()
                .getYear()));
        arrivalMonthEdit.setText(String.valueOf(flight.getArrivalDateTime()
                .getMonth()));
        arrivalDayEdit.setText(String.valueOf(flight.getArrivalDateTime()
                .getDay()));
        arrivalHourEdit.setText(String.valueOf(flight.getArrivalDateTime()
                .getHour()));
        arrivalMinuteEdit.setText(String.valueOf(flight.getArrivalDateTime()
                .getMinute()));
    }
    
    public void viewBookers(View view) {
        
        // If title for scroll view is visible, then that means it's showing 
        // the bookers list view, so turn flight info view to visible and bookers
        // title and bookers list view to invisible. Vice versa otherwise.
        if (bookersTitleView.isShown()) {
            bookersTitleView.setVisibility(View.GONE);
            bookersListView.setVisibility(View.GONE);
            flightInfoView.setVisibility(View.VISIBLE);
        } else {
            bookersTitleView.setVisibility(View.VISIBLE);
            bookersListView.setVisibility(View.VISIBLE);
            flightInfoView.setVisibility(View.GONE);
        }
    }

    /**
     * Returns a string representation of the given number of hours, in ? Hours
     * ? Minutes format.
     * 
     * @param numHours the given number of hours from which to obtain a string
     *        representation, in ? Hours ? Minutes format.
     * @return a string representation of the given number of hours, in ? Hours
     *         ? Minutes format.
     */
    private String hoursToString(double numHours) {

        // Hour value is rounded down.
        int hours = (int) numHours;

        // Minute value is obtained from the remaining hours
        // (less than 1 hour) and is rounded up.
        int minutes = (int) Math.round(((numHours - hours) / (1.0 / 60.0)));
        return hours + " Hours " + minutes + " Minutes";
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
     * Shows a short given message to alert the User of the System in this
     * Activity.
     * 
     * @param message the given message to alert the User of the System in this
     *        Activity.
     */
    private void showShortMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Returns primitive integer value of a given String. Returns 0 if the given
     * String is empty.
     * 
     * @param str the given String to return a primitive integer value from
     */
    private int strToInt(String str) {
        if (str.equals("")) {
            return 0;
        }
        return Integer.parseInt(str);
    }
}

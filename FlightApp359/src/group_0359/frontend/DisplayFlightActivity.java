package group_0359.frontend;

import group_0359.backend.Flight;
import group_0359.backend.NoSuchFlightsException;
import group_0359.backend.NoSuchUserException;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayFlightActivity extends UserActionBarActivity {

    /**
     * Stores the flight Id of a Flight stored in this System passed by the
     * previous activity.
     */
    private String flightId;

    // Stores edit text fields for the Flight to be edited.
    private TextView flightNumber;
    private TextView travelTime;
    private TextView airline;
    private TextView cost;
    private TextView numSeats;
    private TextView departureCity;
    private TextView departureDateTime;
    private TextView arrivalCity;
    private TextView arrivalDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_flight); 

        Intent intent = getIntent();
        // Gets the Flight number/ID of Flight to edit in this
        flightId = (String) intent.getSerializableExtra(LoginActivity.FLIGHT_ID_KEY);

        try {
            if (data.getUserList().getUser(email).isAdmin()) {
                Button button = (Button) findViewById(R.id.edit_flight_button);
                button.setVisibility(View.VISIBLE);
            }
        } catch (NoSuchUserException e1) {
            showShortMessage("ERROR: No User with email " 
                    + email + " found in system");
        }

        Flight flight;
        try {
            flight = data.getUserList().getFlightData().getFlight(flightId);

            // Sets text view of the unique flight number of ID number of the
            // flight passed on to this Activity and the uneditable travel time.
            flightNumber = (TextView) findViewById(R.id.flight_num_edit);
            flightNumber.setText(flight.getNumber());
            travelTime = (TextView) findViewById(R.id.travel_time_edit);
            travelTime.setText(hoursToString(flight.getTravelTime()));

            // Sets current changeable values of the Flight being edited
            // by this Activity into their respective text view fields.
            airline = (TextView) findViewById(R.id.airline_edit);
            cost = (TextView) findViewById(R.id.flight_cost_edit);
            numSeats = (TextView) findViewById(R.id.num_available_seats_edit);
            departureCity = (TextView) findViewById(R.id.departure_city_edit);
            departureDateTime = (TextView) findViewById(R.id.departure_date_and_time);
            arrivalCity = (TextView) findViewById(R.id.arrival_city_edit);
            arrivalDateTime = (TextView) findViewById(R.id.arrival_date_and_time);
            airline.setText(flight.getAirline());
            cost.setText(String.format("%.2f",flight.getCost()));
            numSeats.setText(String.valueOf(flight.getNumSeats()));
            departureCity.setText(flight.getOrigin());
            departureDateTime.setText(String.valueOf(flight.getDepartDateTime()));
            arrivalCity.setText(flight.getDestination());
            arrivalDateTime.setText(String.valueOf(flight.getArrivalDateTime()));
        } catch (NoSuchFlightsException e) {
            showShortMessage("ERROR: No Flight with flight number " 
                    + flightId + " found in system");
        } 
    }

    public void editFlight(View view){

        // Moves on to the Edit Flight Activity.
        Intent intent = new Intent(this, EditFlightActivity.class);
        intent.putExtra(LoginActivity.FLIGHT_ID_KEY, flightId);
        intent.putExtra(LoginActivity.DATA_KEY, data);
        intent.putExtra(LoginActivity.USER_KEY, email);
        startActivity(intent);
    }

    /**
     * Returns a string representation of the given number of hours, in ? Hours
     * ? Minutes format.
     * 
     * @param numHours
     *            the given number of hours from which to obtain a string
     *            representation, in ? Hours ? Minutes format.
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

    @Override
    protected void editBar(ActionBar bar) {
        bar.setTitle("View Flight");

        // Sets action bar/options menu to show unique ID/email
        // of the User of System.
        getSupportActionBar().setSubtitle(email);
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
}
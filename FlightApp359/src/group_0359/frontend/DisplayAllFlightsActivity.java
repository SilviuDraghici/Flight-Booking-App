package group_0359.frontend;

import group_0359.backend.NoSuchFlightsException;
import group_0359.backend.UserData;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayAllFlightsActivity extends UserActionBarActivity {

    /**
     * Holds the Flight numbers to be viewed as a list in this Activity.
     */
    private List<String> flightIds;

    /** A view for showing a list of all the Flight numbers in the System. */
    private ListView listView;

    /** An Adapter to set Flight numbers into the ListView of this Activity */
    private ArrayAdapter<String> adapter;

    /** Stores the Flight number of the selected item. */
    private String selectedFlightId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_flights);

        // Gets list of Flight numbers for ListView.
        flightIds = data.getUserList().getFlightData().getFlightIds();

        // Initializes empty Id for selected Item.
        selectedFlightId = "";

        // Initializes adapter for ListView with the list of Flight numbers.
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, flightIds);

        // Connects adapter to ListView.
        listView = (ListView) findViewById(R.id.all_flights_list);
        listView.setAdapter(adapter);

        // Selection event to get the selected Flight number.
        // Shows a toast message when an item is selected.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                selectedFlightId = (String) listView
                        .getItemAtPosition(position);
                showShortMessage(selectedFlightId + " selected");
            }
        });
    }
    
    @Override
    protected void editBar(ActionBar bar) {
		bar.setTitle("All Flights");
		
		// Sets action bar/options menu to show unique ID/email
        // of the User of System.
        getSupportActionBar().setSubtitle(email);
	}
    
    /**
     * Updates lists of Flights
     */
    @Override
    public void onResume(){
        super.onResume();
        flightIds = data.getUserList().getFlightData().getFlightIds();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, flightIds);
        listView.setAdapter(adapter);
    }
    
    public void toDisplayFlightActivity(View view) {
        if (hasSelection()) {
            Intent intent = new Intent(this, DisplayFlightActivity.class);
            intent.putExtra(LoginActivity.FLIGHT_ID_KEY, selectedFlightId);
            intent.putExtra(LoginActivity.DATA_KEY, data);
            intent.putExtra(LoginActivity.USER_KEY, email);
            startActivity(intent);
        }
    }

    public void toAddNewFlightActivity(View view) {
        Intent intent = new Intent(this, AddNewFlightActivity.class);
        intent.putExtra(LoginActivity.FLIGHT_ID_KEY, selectedFlightId);
        intent.putExtra(LoginActivity.DATA_KEY, data);
        intent.putExtra(LoginActivity.USER_KEY, email);
        startActivity(intent);
    }

    // Removes selected Flight Id from the list view in this Activity and
    // from the System if a Flight ID/number is selected.
    // Show a message if no Flight ID is selected.
    public void removeFlight(View view) {
        if (hasSelection()) {
            
            // Removes Flight with selected Flight ID from System.
            try {
                data.getUserList().getFlightData()
                .removeFlight(selectedFlightId);
            } catch (NoSuchFlightsException e) {
                showShortMessage(selectedFlightId + " is found not in System");
            }
            
            // Removes Flight with selected Flight ID from
            // the stored data in the System.
            UserData userData = data.getUserList();
            try {
                userData.getFlightData().removeFlight(selectedFlightId);
                data.saveToFile(userData);
                
                // Removes Flight with selected Flight ID from
                // list view and System and updates list view.
                flightIds.remove(selectedFlightId);
                adapter.remove(selectedFlightId);
                selectedFlightId = "";
                adapter.notifyDataSetChanged();
            } catch (NoSuchFlightsException e) {
                showShortMessage(selectedFlightId + " is found not in System");
            }
        }
    }

    /**
     * Returns true if there is a selected Flight Id in the list view of this
     * Activity. Returns false otherwise and shows a Toast message.
     * 
     * @return true if there is a selected Flight Id in the list view of this
     *         Activity. Returns false otherwise and shows a Toast message.
     */
    private boolean hasSelection() {
        if (selectedFlightId.equals("")) {
            showShortMessage("No Flight ID number selected");
        }
        return (!selectedFlightId.equals(""));
    }

    /**
     * Shows a short given message to alert the User of the System in this
     * Activity.
     * @param message the given message to alert the User of the System in this
     *        Activity.
     */
    private void showShortMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
        .show();
    }
}

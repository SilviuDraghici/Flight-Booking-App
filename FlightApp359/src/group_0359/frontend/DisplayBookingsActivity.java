package group_0359.frontend;

import group_0359.backend.Flight;
import group_0359.backend.FlightData;
import group_0359.backend.Itinerary;
import group_0359.backend.NoSuchFlightsException;
import group_0359.backend.NoSuchUserException;
import group_0359.backend.User;
import group_0359.backend.UserData;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayBookingsActivity extends UserActionBarActivity {

	String displayEmail;
	UserData userData;
	User user;
	FlightData flightData;

	static ArrayList<Itinerary> cancelItineraries;
	ArrayList<Itinerary> bookedItineraries;
	ListView listView;
	ArrayAdapter<Itinerary> adapter;
	int selected = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_bookings);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Intent intent = getIntent();
		displayEmail = intent.getExtras().getString(LoginActivity.DISPLAY_KEY);

		userData = data.getUserList();
		try {
			user = userData.getUser(displayEmail);
		} catch (NoSuchUserException e) {
			// Should never be called in normal procedure.
			e.printStackTrace();
		}

		flightData = userData.getFlightData();

		listView = (ListView) findViewById(R.id.booked_list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		bookedItineraries = (ArrayList<Itinerary>) user.getBookedItineraries();

		adapter = new ArrayAdapter<Itinerary>(this,
				android.R.layout.simple_list_item_1, bookedItineraries);
		listView.setAdapter(adapter);

		cancelItineraries = new ArrayList<Itinerary>();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				//Get itinerary from click.
				Itinerary itinerary = (Itinerary) parent
						.getItemAtPosition(position);

				cancelItineraries.add(itinerary);
			}

		});

		listView.setOnTouchListener(new OnTouchListener() {
			// Setting on Touch Listener for handling the touch inside
			// ScrollView
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Disallow the touch request for parent scroll on touch of
				// child view
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
	}

	protected void editBar(ActionBar bar) {
		bar.setTitle("Booked");
		// Sets action bar/options menu to show unique ID/email
		// of the User of System.
		getSupportActionBar().setSubtitle(displayEmail);
	}

	/**
	 * Cancels selected bookings on button click.
	 * 
	 * @param view
	 */
	public void cancelBookings(View view) {

		// If user selected itineraries.
		if (cancelItineraries.size() != 0) {

			Iterator iterator = cancelItineraries.iterator();

			// For all itineraries in cancelItineraries.
			while (iterator.hasNext()) {

				Itinerary itinerary = (Itinerary) iterator.next();

				//cancel the booked itinerary
				user.cancelItinerary(itinerary);

				//update flights in FlightData
				for (int i = 0; i < itinerary.getNumFlights(); i++) {
					Flight flight = itinerary.getFlight(i);

					try {
						flightData.removeFlight(flight.getNumber());
					} catch (NoSuchFlightsException e) {
						// Not supposed to reach during normal procedure.
						e.printStackTrace();
					}

					flightData.addFlight(flight);
				}
			}

			//update user.
			data.saveToFile(userData);
			try {
				user = userData.getUser(email);
			} catch (NoSuchUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// update bookedItineries.
			bookedItineraries = (ArrayList<Itinerary>) user
					.getBookedItineraries();
			
			// update flightData
			flightData = userData.getFlightData();

			// Updates ListView
			adapter.notifyDataSetChanged();

			Toast.makeText(this, "Canceled!", Toast.LENGTH_SHORT).show();

		}

	}
	
	/**
	 * Updates instance variables.
	 */
	@Override
	public void onResume(){
	    super.onResume();
	    
	    userData = data.getUserList();
	    try {
			user = userData.getUser(displayEmail);
		} catch (NoSuchUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    flightData = userData.getFlightData();
		bookedItineraries = (ArrayList<Itinerary>) user.getBookedItineraries();
		adapter = new ArrayAdapter<Itinerary>(this,
				android.R.layout.simple_list_item_1, bookedItineraries);
		listView.setAdapter(adapter);
	}
}

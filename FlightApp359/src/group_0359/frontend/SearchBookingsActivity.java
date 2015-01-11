package group_0359.frontend;

import group_0359.backend.CostComparator;
import group_0359.backend.Flight;
import group_0359.backend.FlightData;
import group_0359.backend.Itinerary;
import group_0359.backend.NoSuchFlightsException;
import group_0359.backend.NoSuchUserException;
import group_0359.backend.TimeComparator;
import group_0359.backend.User;
import group_0359.backend.UserData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.MotionEvent;

public class SearchBookingsActivity extends UserActionBarActivity {

	String displayEmail;
	ArrayList<Itinerary> itineraries;
	ListView listView;
	ArrayAdapter<Itinerary> adapter;
	Itinerary itinerary;
	UserData userData;
	User user;
	FlightData flightData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_bookings);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		userData = data.getUserList();
		flightData = userData.getFlightData();	 
		
		Intent intent = getIntent();
		displayEmail = intent.getExtras().getString(LoginActivity.DISPLAY_KEY);
		
		try {
			user = userData.getUser(displayEmail);
		} catch (NoSuchUserException e) {
			// Shouldn't reach here in normal procedure.
			e.printStackTrace();
		}
		itineraries = new ArrayList<Itinerary>();
		
		listView = (ListView) findViewById(R.id.itinerary_list);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		adapter = new ArrayAdapter<Itinerary>(this,
				android.R.layout.simple_list_item_1, itineraries);

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				itinerary = itineraries.get(position);
			}
		});

		// Enables scrolling of ListView inside of ScrollView
		listView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
	}

	protected void editBar(ActionBar bar) {
		bar.setTitle("Search Itineraries");
		// Sets action bar/options menu to show unique ID/email
		// of the User of System.
		getSupportActionBar().setSubtitle(displayEmail);
	}

	/**
	 * Helper method for search. Initializes itineraries.
	 * 
	 * @param view
	 */
	private void onSearch() {
		
		//Get edit text fields from layout.
		EditText originText = (EditText) findViewById(R.id.origin_field);
		EditText destinationText = (EditText) findViewById(R.id.destination_field);
		EditText departureText = (EditText) findViewById(R.id.departure_date_field);

		//Sets origin, destination, departureDates.
		String origin = originText.getText().toString();
		String destination = destinationText.getText().toString();
		String departureDate = departureText.getText().toString();

		itineraries = flightData.getItineraries(departureDate, origin,
				destination);

		// Iterator for itineraries.
		Iterator iterator = itineraries.iterator();

		// For all itineraries in itineraries
		while (iterator.hasNext()) {

			Itinerary itnry = (Itinerary) iterator.next();

			// remove the itinerary from display if it is not bookable.
			for (int i = 0; i < itnry.getNumFlights(); i++) {
				Flight flight = itnry.getFlight(i);

				if (flight.getNumSeats() == 0) {
					iterator.remove();
					adapter.notifyDataSetChanged();
					break;
				}
			}
		}

	}

	/**
	 * Handles button click to search for Itineraries
	 * 
	 * @param view
	 */
	public void onSearchItineraries(View view) {
		onSearch();

		if (itineraries.size() == 0) {
			Toast.makeText(this, " No such itineraries ", Toast.LENGTH_SHORT)
					.show();
		} else {
			adapter = new ArrayAdapter<Itinerary>(this,
					android.R.layout.simple_list_item_1, itineraries);
			listView.setAdapter(adapter);
		}
	}

	/**
	 * Handles button click to search for flights
	 * 
	 * @param view
	 */
	public void onSearchFlights(View view) {
		onSearchItineraries(view);

		// Iterator for itineraries;
		Iterator iterator = itineraries.iterator();

		while (iterator.hasNext()) {

			Itinerary itnry = (Itinerary) iterator.next();

			// If this itinerary has more than on flights, remove it.
			if (itnry.getNumFlights() != 1) {
				iterator.remove();
			}
		}

		// Update ListView.
		adapter.notifyDataSetChanged();
	}

	/**
	 * Displays list of Itineraries sorted by time.
	 * 
	 * @param view
	 */
	public void sortByTime(View view) {
		
		// Sorts itineraries by time
		Collections.sort(itineraries, new TimeComparator());

		// Updates adapter.
		adapter = new ArrayAdapter<Itinerary>(this,
				android.R.layout.simple_list_item_1, itineraries);

		// Updates ListView
		listView.setAdapter(adapter);

		// Notifes user.
		Toast.makeText(this, "Sorted by time", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Display list of Itineraries sorted by cost.
	 */
	public void sortByCost(View view) {

		// Sorts itineraries by cost
		Collections.sort(itineraries, new CostComparator());

		// Updates adapter.
		adapter = new ArrayAdapter<Itinerary>(this,
				android.R.layout.simple_list_item_1, itineraries);

		// Updates ListView
		listView.setAdapter(adapter);

		// Notifes user.
		Toast.makeText(this, "Sorted by cost", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Books selected itinerary by user, when book button is clicked.
	 * 
	 * @param view
	 * 
	 */
	public void book(View view) {
		if (itinerary != null) {
			user.bookItinerary(itinerary);

			// Checks whether the itinerary has been removed from display.
			Boolean removedItinerary = false;

			// For all flights in selected itinerary.
			for (int i = 0; i < itinerary.getNumFlights(); i++) {
				Flight flight = itinerary.getFlight(i);

				// Update the flight information in flightData
				try {
					flightData.removeFlight(flight.getNumber());
				} catch (NoSuchFlightsException e) {
					// Shouldn't reach here in normal procedure.
					e.printStackTrace();
				}
				flightData.addFlight(flight);

				// remove itinerary if user can't book it.
				if ((flight.getNumSeats() == 0) && !removedItinerary) {
					itineraries.remove(itinerary);
					adapter.notifyDataSetChanged();
					removedItinerary = true;
				}
			}

			// saves updated userData to file.
			data.saveToFile(userData);

			Toast.makeText(this, "Booked!", Toast.LENGTH_SHORT).show();
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
		
		//If user has searches displayed. Update the list.
		if(itineraries.size() != 0){
			onSearch();
		}

	}
}

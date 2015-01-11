/**
 * 
 */
package group_0359.backend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Anushay
 * 
 */
public class Driver {

	private static UserData userData = new UserData();

	/**
	 * Takes a path, to an input csv file. Uploads the client information in the
	 * application.
	 * 
	 * @param path
	 */
	public static void uploadClientInfo(String path) {
		try {
			userData.addClients(path);
		} catch (IOException e) {
		}
	}

	/**
	 * Takes a path, to an input csv files. Uploads the flight information in to
	 * the application.
	 * 
	 * @param path
	 */
	public static void uploadFlightInfo(String path) {
		try {
			userData.getFlightData().addFlights(path);
		} catch (IOException e) {
		}
	}

	/**
	 * 
	 * @param email
	 * @return the information stored for the client, with the given email.
	 * @throws NoSuchUserException
	 */
	public static String getClient(String email) {
		try {
			return userData.getUser(email).toString();
		} catch (NoSuchUserException e) {
			return "";
		}

	}

	/**
	 * 
	 * @param date
	 *            in the format YYYY-MM-DD
	 * @param origin
	 * @param destination
	 * @return all flights that depart from origin, on date and arrive in
	 *         destination.
	 */
	public static String getFlights(String date, String origin,
			String destination) {
		List<Flight> flights = new ArrayList<>();
		try {
			flights = userData.getFlightData().getFlights(date, origin,
					destination);
		} catch (NoSuchFlightsException e) {

		}
		StringBuilder returnString = new StringBuilder();
		for (Flight flight : flights)
			returnString.append(flight.toString() + "\n");
		return returnString.toString();
	}

	/**
	 * 
	 * @param date
	 *            in the format YYYY-MM-DD
	 * @param origin
	 * @param destination
	 * @return all itineraries that depart from origin, on date and arrive in
	 *         destination.
	 */
	public static String getItineraries(String date, String origin,
			String destination) {
		List<Itinerary> itineraries = new ArrayList<>();
		itineraries = userData.getFlightData().getItineraries(date, origin,
				destination);
		StringBuilder returnString = new StringBuilder();
		for (Itinerary itin : itineraries)
			returnString.append(itin.toString() + "\n");
		return returnString.toString();
	}

	/**
	 * 
	 * @param date
	 * @param origin
	 * @param destination
	 * @return
	 */
	public static String getItinerariesSortedByCost(String date, String origin,
			String destination) {
		List<Itinerary> itineraries = new ArrayList<>();
		itineraries = userData.getFlightData().getItineraries(date, origin,
				destination);
		Collections.sort(itineraries, new CostComparator());
		StringBuilder returnString = new StringBuilder();
		for (Itinerary itin : itineraries)
			returnString.append(itin.toString() + "\n");
		return returnString.toString();
	}

	/**
	 * 
	 * @param date
	 * @param origin
	 * @param destination
	 * @return
	 */
	public static String getItinerariesSortedByTime(String date, String origin,
			String destination) {
		List<Itinerary> itineraries = new ArrayList<>();
		itineraries = userData.getFlightData().getItineraries(date, origin,
				destination);
		Collections.sort(itineraries, new TimeComparator());
		StringBuilder returnString = new StringBuilder();
		for (Itinerary itin : itineraries)
			returnString.append(itin.toString() + "\n");
		return returnString.toString();
	}
}

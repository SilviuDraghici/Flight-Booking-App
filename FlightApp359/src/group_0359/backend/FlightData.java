package group_0359.backend;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 package group_0359.backend;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * A collection of Flights that can be obtained from
 * a csv file of a specific format and can also be queried
 * valid Itineraries or Flights given a departure date, 
 * an origin and a destination.
 * @author Kishan, Silviu
 *
 */
public class FlightData implements Serializable{

    /** UID generated by Eclipse */
    private static final long serialVersionUID = 211558807530511034L;

    /**delimiter character*/
    public static final String DELIMITER = ",";

    /**Collection of Flights with the Flight Number as the key. */
    private Map<String, Flight> allFlights;

    /**Collection of flights stored by City of origin, and date of departure.*/
    private Map<String, Map<String, TreeSet<Flight>>> flights;

    /**
     * Creates an empty instance of FlightData.
     */
    public FlightData(){
        allFlights = new HashMap<String, Flight>();
    }

    /**
     * Reads a csv file containing flights and adds them to the 
     * list of flights.
     * @param filePath File path of the flight csv file.
     */
    public void addFlights(String filePath)throws IOException{

        //read in and create a list of unsorted flights
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(filePath));
            String flightString;
            while((flightString = br.readLine()) != null) {

                //parse string into appropriate fields and then save 
                //them in Flight objects
                String[] parsedFlight = flightString.split(DELIMITER);
                if(parsedFlight.length >= 8){
                    Flight flight = new Flight(parsedFlight[0], parsedFlight[1],
                            parsedFlight[2], parsedFlight[3],
                            parsedFlight[4], parsedFlight[5],
                            Double.valueOf(parsedFlight[6]),
                            Integer.valueOf(parsedFlight[7]));
                    allFlights.put(flight.getNumber(), flight);  
                }else{
    				br.close();
    				throw new IOException();
    			}
            }
            br.close();
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        mapFlights();
    }

    /**
     * Returns a list of the unique flight numbers of
     * all the Flights stored in this FlightData.
     * @return a list of the unique flight numbers of
     *         all the Flights stored in this FlightData.
     */
    public List<String> getFlightIds() {
        return new ArrayList<String>(allFlights.keySet());
    }

    /**
     * Removes a Flight stored in this FlightData with the
     * given unique flight number if there is a Flight
     * in this FlightData with the given flight number. 
     * @param num the unique Flight number of a Flight
     *        stored in this FlightData.
     * @throws NoSuchFlightsException if there is no Flight 
     *         in this FlightData with the given flight number.
     */
    public void removeFlight(String num) 
            throws NoSuchFlightsException {
        if (!allFlights.containsKey(num)) {
            String msg = "No Flight with flight number: " + num;
            throw new NoSuchFlightsException(msg);
        } else {
            this.allFlights.remove(num);
            mapFlights();
        }
    }

    /**
     * Returns a Flight in this FlightData with the
     * given flight number.
     * @param num the given flight number of a
     *        Flight in this FlightData
     * @return a Flight in this FlightData with the 
     *         given flight number.
     * @throws NoSuchFlightsException if there is no Flight 
     *         in this FlightData with the given flight number.
     */
    public Flight getFlight(String num) 
            throws NoSuchFlightsException {
        if (!allFlights.containsKey(num)) {
            String msg = "No Flight with flight number: " + num;
            throw new NoSuchFlightsException(msg);
        } else {
            return allFlights.get(num);
        }
    }
    
    /**
     * Adds the given Flight to the Flights stored in this
     * FlightData. If the given Flight has the same flight
     * number as a Flight in this FlightData, it replaces
     * it.
     * @param flight the given Flight to be stored into this
     *        FlightData.
     */
    public void addFlight(Flight flight) {
        this.allFlights.put(flight.getNumber(), flight);
        mapFlights();
    }

    /**
     * Initializes the instance variable, flights, of this FlightData
     * to map all the Flights in this FlightData with origins and then
     * the departure dates of the Flights as the key, and a list of 
     * Flights with the respective origin and departure dates as 
     * the value.
     */
    private void mapFlights(){
        flights = new HashMap<>();
       
        //create a sorted nested map of flights
        for(Flight f: allFlights.values()){
            String origin = f.getOrigin();
            String date = f.getDepartDateTime().getDate();

            //add a new map for the flight origin if there is none
            if(!flights.keySet().contains(origin)){
                flights.put(origin, new HashMap<String, TreeSet<Flight>>());
                flights.get(origin).put(date,new TreeSet<Flight>());
                flights.get(origin).get(date).add(f);
            }
            else{

                //add a new map for the departure date if there is none
                if(!flights.get(origin).keySet().contains(date)){
                    flights.get(origin).put(date, new TreeSet<Flight>());
                    flights.get(origin).get(date).add(f);
                }

                //add the flight to the list of flights with the same origin
                //and departure date
                else{
                    flights.get(origin).get(date).add(f);
                }
            }
        }
    }

    /**
     * Finds all flights departing on a given date from the given origin,
     * that are stored in flights.
     * 
     * @param date of departure
     * @param origin
     * @return list of flights from given origin with given departure date.
     * @throws NoSuchFlightsException when there no flights with the given parameters
     */
    private TreeSet<Flight> findFlights(String date, String origin)
            throws NoSuchFlightsException{

        //Checks if there are flights that depart from given city of origin,
        //or if there are flights that depart on given date from given city.
        if((flights == null) || (flights.get(origin) == null) 
                || (flights.get(origin).get(date)) == null){

            String msg = "No flights from " + origin + " on " + date;
            throw new NoSuchFlightsException(msg);
        }

        return flights.get(origin).get(date);
    }

    /**
     * Finds all flights departing on a given date from the given origin,
     * that are stored in flights.
     * 
     * @param itinerary to add flights to.
     * @return list of flights from given origin with given departure date.
     * @throws NoSuchFlightsException when there no flights with the given parameters
     */
    private TreeSet<Flight> findFlights(Itinerary itinerary)
            throws NoSuchFlightsException{

        String origin = itinerary.getDestination();
        String date = itinerary.getArrivalDateTime().getDate();
        List<String> places = itinerary.getPlaces();

        String tomorrow = itinerary.getArrivalDateTime()
                .offsetDate().getDate();

        TreeSet<Flight> flightList = null;

        TreeSet<Flight> flghts = new TreeSet<Flight>();

        //If need to check tomorrows date.
		boolean checkTomorrow = !(tomorrow.equals(date));

		//If there are flights from the origin 
		if(flights.get(origin) != null){

			//If there are flights on the arrival date.
			if(flights.get(origin).get(date) != null){
				flightList = flights.get(origin).get(date);

				//If can add flights from tomorrow.
				if((flights.get(origin).get(tomorrow) != null) 
						&& checkTomorrow){
					flightList.addAll(flights.get(origin).get(tomorrow));
				}
			}
			
			/*If there are no flights departing at arrival time,
			 * but there are flights leaving tomorrow.
			 */
			else if((flights.get(origin).get(tomorrow) != null) 
					&& checkTomorrow){
				flightList = flights.get(origin).get(tomorrow);
			}
			
			/*
			 * If there are no possible flights.
			 */
			else{
				String msg = "No flights from " + origin + " on " + date + " or "
						+ "untill 6:00 tomorrow";
				throw new NoSuchFlightsException(msg);
			}
		}
		
		//If no flights from origin.
		else{
			String msg = "No flights from " + origin + " on " + date;
			throw new NoSuchFlightsException(msg);
		}
		
		/* Adds flight to flghts in within six hours of arrival time 
		 * of itinerary 
		 */
		for(Flight flight: flightList){
			
			boolean withinSix = (flight.getDepartDateTime()
					.compareTo(itinerary.getArrivalDateTime()) >= 0)
					&& (flight.getDepartDateTime().
							compareTo(itinerary.getArrivalDateTime()
									.offsetDate()) <= 0); 
			
			boolean goesBack = places.contains(flight.getDestination());
			
			if( withinSix && !goesBack && !(flight.getNumSeats() == 0)){
				flghts.add(flight);
			}

		}

		return flghts;
	}
	
    /**
     * Gets all itineraries from origin to destination with given 
     * departure date.
     * 
     * @param date of departure of first flight in the itinerary
     * @param origin of first flight in the itinerary
     * @param destination of last flight in the itinerary
     * @return list of all itineraries that meet the given criteria
     * @throws NoSuchFlightsException if there no itineraries that connect
     * 			from the origin to the destination with given departure date
     */
    private ArrayList<Itinerary> getItineraries(Itinerary itinerary, 
            String destination) throws NoSuchFlightsException{
    	
        //List of Itineraries to return.
        ArrayList<Itinerary> itineraries = new ArrayList<Itinerary>();

        //List of flights departing from given date from city of origin.
        TreeSet<Flight> flights = findFlights(itinerary);

       for(Flight flight: flights){
			
			boolean conti = false;
			
			//Starts with all flights leaving origin at departure date.
			Itinerary itinry = new Itinerary(itinerary);
			itinry.add(flight);
			
			//If reached destination, add to itineraries, and continue.
			if((itinry.getDestination().equals(destination))){
				itineraries.add(itinry);
				conti = true;
			}
			
			if(!conti){
		
				ArrayList<Itinerary> itnrs = null;
				
				boolean add = true;
			
				//itinerary still does not connect straight to destination.
				//
				// So check for Itineraries starting from origin of itinerary,
				// and departing within 6 hours of itinerary arrival time.
				//
				// If no such flights exist, or can't connect to destination 
				// from this flight, stop and check other flights.
				try{
					itnrs = 
							getItineraries(itinry, destination);
				}
				catch(NoSuchFlightsException e){
					add = false;
				}
				
				if(add){
					itineraries.addAll(itnrs);
				}
			}
		}
		
		if(itineraries.size() == 0){
			
			String msg = "No itineraries from " + itinerary.getOrigin() + " to " 
					+ destination + " with departure date of "
					+ itinerary.getDepartDateTime();
			throw new NoSuchFlightsException(msg);
		}
		
		return itineraries;
		
	}

    /**
     * Gets all itineraries from origin to destination with given 
     * departure date.
     * 
     * @param date of departure of first flight in the itinerary
     * @param origin of first flight in the itinerary
     * @param destination of last flight in the itinerary
     * @return list of all itineraries that meet the given criteria
     */
    public ArrayList<Itinerary> getItineraries(String date, String origin, 
            String destination){
    	
    	

        //List of Itineraries to return.
        ArrayList<Itinerary> itineraries = new ArrayList<Itinerary>();

        //List of flights departing from given date from city of origin.
        TreeSet<Flight> flights;
        
		try {
			flights = findFlights(date, origin);
		

        for(Flight flight: flights){
			
			boolean conti = false;
			
			//Starts with all flights leaving origin at departure date.
			Itinerary itinerary = new Itinerary(flight);
			
			/*If single flight connects from origin to destination,
			  then found Itinerary from origin to destination.
			
			  So add to Itineraries, 
			  and continue to other check other flights. */
			if(itinerary.getDestination().equals(destination)){
				itineraries.add(itinerary);
				
				conti = true;
			}
			
			if(!conti){
			
				ArrayList<Itinerary> itnrs = null;
				
				boolean add = true;
			
				//This flight does not connect straight to destination.
				//
				// So check for Itineraries starting from origin of this flight,
				// and departing within 6 hours of this flight.
				//
				// If no such flights exist, or can't connect to destination 
				// from this flight, stop and check other flights.
				try{
					itnrs = 
							getItineraries(itinerary, destination);
				}
				catch(NoSuchFlightsException e){
					add =false;
				}
			
				if(add)
					itineraries.addAll(itnrs);
			}
			
		}
		
		if(itineraries.size() == 0){
			String msg = "No itineraries from " + origin + " to " 
					+ destination + " with departure date of " + date;
			throw new NoSuchFlightsException(msg);
		}
		
		} catch (NoSuchFlightsException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return itineraries;
		
	}

    /**
     * Gets all straight flights from origin to destination, on given
     * departure date.
     * 
     * @param date of departure
     * @param origin of flight
     * @param destination of flight
     * @return list of all flights that meet the given criteria
     * @throws NoSuchFlightsException if no straight flights from origin
     * 			to destination, with given date of departure.
     */
    public ArrayList<Flight> getFlights(String date, String origin, 
            String destination) throws NoSuchFlightsException{
        ArrayList<Flight> flights = new ArrayList<Flight>();

        for(Flight flight: findFlights(date,origin)){
            if(flight.getDestination().equals(destination)){
                flights.add(flight);
            }
        }

        if(flights.size() == 0){
            String msg = "No flight from " + origin + " to " + destination
                    + " on " + date;
            throw new NoSuchFlightsException(msg);
        }

        return flights;
    }

    /**
     * Returns a string representation of this FlightData.
     */
    public String toString(){
        StringBuilder returnString = new StringBuilder();
        for(Flight f: allFlights.values()){
            returnString.append(f.toString() + "\n");
        }
        return returnString.toString();
    }

	public Map<String, Map<String, TreeSet<Flight>>> getFlights() {
		return flights;
	}
}

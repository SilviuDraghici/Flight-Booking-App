package group_0359.backend;

import java.util.Comparator;

public class TimeComparator implements Comparator<Itinerary> {

	/**
	 * Compares Itineraries by travel time.
	 * Note: this comparator imposes orderings that are 
	 * inconsistent with equals.
	 * 
	 * @param itinerary1
	 * @param itinerary2
	 * @return 0 if travelTime of the Itineraries are equal 
	 * 		  -1 if travelTime of itinerary1 is less than 
	 * 			    travelTime of itinerary2
	 * 		   1 if travelTime of itinerary is greater than
	 * 		        travelTime of itinerary2.
	 */
	@Override
	public int compare(Itinerary itinerary1, Itinerary itinerary2) {
		int c = 0;
		
		if(itinerary1.getTravelTime() > itinerary2.getTravelTime()){
			c = 1;
		}
		
		if(itinerary1.getTravelTime() < itinerary2.getTravelTime()){
			c = -1;
		}
		
		return c;
	}

	
}

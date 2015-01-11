package group_0359.backend;

import java.util.Comparator;

public class CostComparator implements Comparator<Itinerary> {

	/**
	 * Compares Itineraries by cost.
	 * Note: this comparator imposes orderings that are 
	 * inconsistent with equals.
	 * 
	 * @param itinerary1
	 * @param itinerary2
	 * @return 0 if totalCost of the Itineraries are equal 
	 * 		  -1 if totalCost of itinerary1 is less than 
	 * 		        totalCost of itinerary2
	 * 		   1 if totalCost of itinerary1 is greater than
	 * 		        totalCost of itinerary2.
	 */
	@Override
	public int compare(Itinerary itinerary1, Itinerary itinerary2) {
		int c = 0;
		
		if(itinerary1.getCost() > itinerary2.getCost()){
			c = 1;
		}
		
		if(itinerary1.getCost() < itinerary2.getCost()){
			c = -1;
		}
		
		return c;
	}

}

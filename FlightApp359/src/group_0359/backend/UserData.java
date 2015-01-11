package group_0359.backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A collection of Users that can be obtained from a csv file of a specific
 * format. Also holds an instance of FlightData.
 * 
 * @author Silviu
 * 
 */
public class UserData implements Serializable {

	/** Field delimiter character */
	public static final String FIELDDELIMITER = ",";

	/** FlightData instance */
	private FlightData flightData = new FlightData();

	/** List of all clients */
	private Map<String, User> users;

	/**
	 * Creates an empty instance of UserData with no Users.
	 */
	public UserData() {
		users = new HashMap<String, User>();
	}

	/**
	 * Reads Users from a csv file and adds them to this UserData.
	 * 
	 * @param filePath
	 *            the given file path to the csv File holding User information.
	 * @throws IOException
	 */
	public void addClients(String filePath) throws IOException {
		addNewUsers(filePath);
	}

	/**
	 * Adds the given User to the Users stored in this FlightData. If the given
	 * User has the same email User in this UserData, it replaces it.
	 * 
	 * @param user
	 *            the given User to be stored into this UserData.
	 */
	public void addUser(User user) {
		this.users.put(user.getEmail(), user);
	}

	/**
	 * Returns a User in this UserData with the given email.
	 * 
	 * @param email
	 *            the given email of a User in this UserData
	 * @return a User in this UserData with the given email.
	 * @throws NoSuchUserException
	 *             if there is no User with the given email in this UserData.
	 */
	public User getUser(String email) throws NoSuchUserException {
		if (!users.containsKey(email)) {
			String msg = "No User with email: " + email;
			throw new NoSuchUserException(msg);
		} else {
			return users.get(email);
		}
	}

	/**
	 * Returns an instance of FlightData.
	 * 
	 * @return FlightData.
	 */
	public FlightData getFlightData() {
		return flightData;
	}

	/**
	 * Returns a list of the unique Client emails of all the Clients stored in
	 * this UserData.
	 * 
	 * @return a list of the unique Client emails of all the Client stored in
	 *         this UserData.
	 */
	public List<String> getClientIds() {
		List<String> clientIds = new ArrayList<String>();
		for (User user : users.values()) {
			if (!user.isAdmin()) {
				clientIds.add(user.getEmail());
			}
		}
		return clientIds;
	}

	/**
	 * Returns a list of the unique Admin emails of all the Clients stored in
	 * this UserData.
	 * 
	 * @return a list of the unique Admin emails of all the Client stored in
	 *         this UserData.
	 */
	public List<String> getAdminIds() {
		List<String> clientIds = new ArrayList<String>();
		for (User user : users.values()) {
			if (user.isAdmin()) {
				clientIds.add(user.getEmail());
			}
		}
		return clientIds;
	}

	/**
	 * Removes a User stored in this UserData with the given unique email if
	 * there is a User in this UserData with the given email
	 * 
	 * @param email
	 *            the unique email of a User stored in this UserData.
	 * @throws NoSuchUserException
	 *             if there is no User with the given email in this UserData.
	 */
	public void removeUser(String email) throws NoSuchUserException {
		if (!users.containsKey(email)) {
			String msg = "No User with email: " + email;
			throw new NoSuchUserException(msg);
		} else {
			users.remove(email);
		}
	}

	private void addNewUsers(String filePath) throws IOException {
		BufferedReader br;
		br = new BufferedReader(new FileReader(filePath));
		String user;
		while ((user = br.readLine()) != null) {

			// parse string into appropriate fields and then save
			// them in User objects
			String[] parsedUser = user.split(FIELDDELIMITER);
			if (parsedUser.length >= 6) {
				User newUser = new User(parsedUser[0], parsedUser[1],
						parsedUser[2], parsedUser[3], parsedUser[4],
						parsedUser[5]);
				users.put(newUser.getEmail(), newUser);
			}
			else{
				br.close();
				throw new IOException();
			}
		}
		br.close();
	}
}

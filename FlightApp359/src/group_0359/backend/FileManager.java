package group_0359.backend;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FileManager implements Serializable {
    
	private static final long serialVersionUID = 8119618805238033060L;
	private final String FILEPATH;
	private final String PASSWORDPATH;
	private final String DELIMITER = ",", DEFAULTPASSWORD = "123";
	/**
	 * Saves filepaths.
	 * @param filePath path to userdata
	 * @param passwordPath path to passwords
	 */
	public FileManager(String filePath, String passwordPath) {
		this.FILEPATH = filePath;
		this.PASSWORDPATH = passwordPath;
	}
	
	/**
	 * Saves input userData to memory
	 * @param userData to be saved to file
	 */
	public void saveToFile(UserData userData) {
		try {
			OutputStream file = new FileOutputStream(FILEPATH);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				output.writeObject(userData);
			} finally {
				output.close();
			}
		} catch (IOException ex) {
			System.out.println("Cannot perform output.");
		}
	}
	
	/**
	 * Reads userdata from memory.
	 * @return UserData read from memory
	 */
	public UserData getUserList() {
		UserData userData;
		try {
			InputStream file = new FileInputStream(FILEPATH);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			try {
				userData = (UserData) input.readObject();
			} finally {
				input.close();
			}
		} catch (Exception e) {
			userData = new UserData();
			User admin = new User("ln", "fn", "admin@gmail.com", "Spaaace",
					"1234567898765", "3000-03-03", true);
			userData.addUser(admin);
			saveToFile(userData);
		}
		return userData;
	}
	
	/**
	 * Returns password for given user.
	 * @param email of user
	 * @return password of user
	 * @throws NoSuchUserException if user email is not in passwords
	 */
	public String getPassword(String email) throws NoSuchUserException{
		HashMap<String, String> pwords = getPasswords();
		String returnStr = pwords.get(email);
		if(returnStr != null)
			return returnStr;
		else
			throw new NoSuchUserException();
		
	}
	
	/**
	 * Returns map of email keys and password values.
	 * @return Map of passwords
	 */
	public HashMap<String, String> getPasswords() {
		HashMap<String, String> pwords = new HashMap<String, String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(PASSWORDPATH));
			String flightString;
			while ((flightString = br.readLine()) != null) {

				// parse string into appropriate fields and then save
				// add them to map
				String[] parsedPairs = flightString.split(DELIMITER);
				pwords.put(parsedPairs[0], parsedPairs[1]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pwords;
	}
	
	/**
	 * Looks at the users in userdata and assigns default passwords to 
	 * those that have none
	 */
	public void addPasswords(){
		HashMap<String, String> passwords = getPasswords();
		List<String> users = (List<String>)getUserList().getClientIds();
		users.addAll((List<String>)getUserList().getAdminIds());
		for(String usr: users){
			//			Toast.makeText(this,usr, Toast.LENGTH_SHORT).show();
			if(!passwords.containsKey(usr)){
				//				Toast.makeText(this,"adding " + usr, Toast.LENGTH_LONG).show();
				passwords.put(usr, DEFAULTPASSWORD);
			}
		}
		savePasswords(passwords);
	}
	
	/**
	 * Changes the password of given user.
	 * @param email email of user
	 * @param password new password
	 */
	public void changePassword(String email, String password){
		HashMap<String, String> pwords = getPasswords();
		pwords.put(email, password);
		savePasswords(pwords);
	}

	private void savePasswords(HashMap<String, String> pwords){;
		PrintWriter pw;
		try{
			pw = new PrintWriter(new FileWriter(PASSWORDPATH));
			Set<String> emails = pwords.keySet();
			for(String email: emails){
				String password = pwords.get(email);
				pw.println(String.format("%s%s%s", email,DELIMITER,password));
			}
			pw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}

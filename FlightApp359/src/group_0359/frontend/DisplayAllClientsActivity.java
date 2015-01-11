package group_0359.frontend;

import group_0359.backend.NoSuchUserException;
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

public class DisplayAllClientsActivity extends UserActionBarActivity {

    /**
     * Holds the User emails to be viewed as a list in this Activity.
     */
    private List<String> clientIds;

    /** A view for showing a list of all the Flight numbers in the System. */
    private ListView listView;

    /** An Adapter to set emails into the ListView of this Activity. */
    private ArrayAdapter<String> adapter;

    /** Stores the User email of the selected item. */
    private String selectedClientId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_all_clients);

        // Gets list of Client emails for ListView.
        clientIds = data.getUserList().getClientIds();

        // Initializes empty ID for selected Item.
        selectedClientId = "";

        // Initializes adapter for ListView with the list of Client Ids.
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, clientIds);

        // Connects adapter to ListView.
        listView = (ListView) findViewById(R.id.all_clients_list);
        listView.setAdapter(adapter);

        // Selection event to get the selected Client email.
        // Shows a toast message when an item is selected.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                selectedClientId = (String) listView
                        .getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        selectedClientId + " selected.", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
 
    /**
     * Updates lists of Clients.
     */
    @Override
    public void onResume(){
        super.onResume();
        clientIds = data.getUserList().getClientIds();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_single_choice, clientIds);
        listView.setAdapter(adapter);
    }
    
    @Override
	protected void editBar(ActionBar bar) {
		bar.setTitle("All Clients");
		
		// Sets action bar/options menu to show unique ID/email
        // of the User of System.
        getSupportActionBar().setSubtitle(email);
	}

    public void toClientUserActivity(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra(LoginActivity.DISPLAY_KEY, selectedClientId);
        intent.putExtra(LoginActivity.DATA_KEY, data);
        intent.putExtra(LoginActivity.USER_KEY, email);
        startActivity(intent);
    }

    public void toAddNewClientActivity(View view) {
        Intent intent = new Intent(this, AddNewClientActivity.class);
        intent.putExtra(LoginActivity.DATA_KEY, data);
        intent.putExtra(LoginActivity.USER_KEY, email);
        startActivity(intent);
    }

    // Removes selected Client Id from the list view in this Activity and
    // from the System if a Client ID/email is selected.
    // Show a message if no Client ID is selected.
    public void removeClient(View view) {
        if (hasSelection()) {
            
            // Removes Client with selected Client Id from System.
            try {
                data.getUserList().removeUser(selectedClientId);
            } catch (NoSuchUserException e) {
                Toast.makeText(getApplicationContext(),
                        selectedClientId + " is found not in System.",
                        Toast.LENGTH_SHORT).show();
            }
            
            // Removes Client with selected Client ID from
            // the stored data in the System.
            UserData userData = data.getUserList();
            try {
                userData.removeUser(selectedClientId);
                data.saveToFile(userData);

                // Removes Client with selected Client ID from
                // list view and updates list view.
                clientIds.remove(selectedClientId);
                adapter.remove(selectedClientId);
                selectedClientId = "";
                adapter.notifyDataSetChanged();
            } catch (NoSuchUserException e) {
                showShortMessage(selectedClientId + " is found not in System");
            }
        }
    }

    /**
     * Returns true if there is a selected Client Id in the list view of this
     * Activity. Returns false otherwise and shows a Toast message.
     * 
     * @return true if there is a selected Client Id in the list view of this
     *         Activity. Returns false otherwise and shows a Toast message.
     */
    private boolean hasSelection() {
        if (selectedClientId.equals("")) {
            showShortMessage("No Client ID email selected.");
        }
        return (!selectedClientId.equals(""));
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

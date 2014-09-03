package com.adamschalmers.com;

import android.app.Activity;
import android.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.content.Intent;

public class MainActivity extends Activity {
	
	public final int EDIT_ITEM_REQUEST_CODE = 1243123;
	
	// Define variables
	ListView listview;
	ArrayList<String> items;
	ItemAdapter itemsAdapter;
	EditText addItemEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //use activity_main.xml as the layout
        setContentView(R.layout.activity_main);
        
        //reference the listview variable to the id listview in the layout
        listview = (ListView)findViewById(R.id.listView1);
        addItemEditText = (EditText)findViewById(R.id.editText1);
        
        //Create arraylist of Strings
        items = new ArrayList<String>();
        items.add("item 1");
        items.add("item 2");
        
        //turn listview arraylist into Android gui listview thing
        readItemsFromDb();
        itemsAdapter = new ItemAdapter(this, items);
        
        listview.setAdapter(itemsAdapter);
        
        
     /* // Construct the data source
        ArrayList<User> arrayOfUsers = new ArrayList<User>();
        // Create the adapter to convert the array to views
        UsersAdapter adapter = new UsersAdapter(this, arrayOfUsers);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.lvItems);
        listView.setAdapter(adapter);*/
        
        
        
        setupListViewListener();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    } 
    
    
    /**
     * ACTUAL APP LOGIC STARTS HERE
     */
    
    // Activates when the user clicks our Save button
    public void onAddItemClick(View view) {
    	String toAddString = addItemEditText.getText().toString();
    	// Check the user has entered a new item
    	if (toAddString != null && toAddString.length() > 0) {
    		
    		// If the item is a duplicate, don't add it.
    		if (items.contains(toAddString)) {
    			Toast.makeText(this, toAddString + " is already listed", Toast.LENGTH_SHORT).show();
    		} else { // Add the item
        		itemsAdapter.add(toAddString);
        		addItemEditText.setText("");
        		saveItemToDb(toAddString);
    		}
    	}
    }
    
    // Listen for clicks or long clicks
    private void setupListViewListener(){
    	
    	// On item long click, start the 'delete item' dialog
    	listview.setOnItemLongClickListener(new OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long rowId) {
    			Log.i("MainActivity", "Long Clicked item" + position);
    			String pos = (String) items.get(position);
    			
    			// Create and setup the alert dialog builder
    			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    			builder.setTitle(R.string.dialog_delete_title)
    				.setMessage("Delete " + pos + "?")
    				.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    						//delete item
    		    			deleteItemFromDb(items.get(position));
    		    			items.remove(position);
    		    			itemsAdapter.notifyDataSetChanged();
    					}
    				})
    				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {}
    				});
    			
    			builder.create().show();
    			return true;
    		}
    	});
    	
    	// On item click, start the Edit Item activity
    	listview.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView <? > parent, View view, int position, long id) {
	    		String updateItem = (String) itemsAdapter.getItem(position);
	    		Log.i("MainActiviy","Clicked item " + position + ": " + updateItem);
	    		
	    		// Start the new activity
	    		Intent intent = new Intent(MainActivity.this, EditToDoItemActivity.class);
	    		if (intent != null) {
	    			// put 'extras' into the bundle for access in the edit activity
	    			intent.putExtra("item", updateItem);
	    			intent.putExtra("position",  position);
	    			//bring up the new activity
	    			startActivityForResult(intent, EDIT_ITEM_REQUEST_CODE);
	    			itemsAdapter.notifyDataSetChanged();
	    		}
    		}
    		
    	});
    }
    
    // When we return to this activity from the Edit Item activity, update the edited item.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == EDIT_ITEM_REQUEST_CODE) {
    		if (resultCode == RESULT_OK) {
    			// Extract name value from result extras
    			int position = data.getIntExtra("position",  -1);
    			String oldText = items.get(position);
    			String newText = data.getExtras().getString("item");
    			items.set(position, newText);
    			Log.i("Updated Item in list:", newText);
    			Toast.makeText(this,  "Updated:" + newText, Toast.LENGTH_SHORT).show();
    			itemsAdapter.notifyDataSetChanged();
    			deleteItemFromDb(oldText);
    			saveItemToDb(newText);
    		}
    	}
    }
    
    // Load the user's last todo list
    private void readItemsFromFile() {
    	
    	// Find our file in our app's private directory
    	File filesDir = getFilesDir();
    	File todoFile = new File(filesDir, "todo.txt");
    	
    	// Read file contents into items if they exist, otherwise make a new empty items
    	if (!todoFile.exists()) {
    		items = new ArrayList<String>();
    	} else {
    		try {
    			items = new ArrayList<String>(FileUtils.readLines(todoFile));
    		} catch (IOException ex) {
    			items = new ArrayList<String>();
    		}
    	}
    }
    
    // Save the todo list
    private void saveItemsToFile() {
    	// Find our file in our app's private directory
    	File filesDir = getFilesDir();
    	File todoFile = new File(filesDir, "todo.txt");
    	
    	try {
    		FileUtils.writeLines(todoFile, items);
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	}
    	
    }
    
    // Read all items from the DB into our Items list
    private void readItemsFromDb() {
    	List<ToDoItem_Week05> itemsFromORM = new Select().from(ToDoItem_Week05.class).execute();
    	items = new ArrayList<String>();
    	if (itemsFromORM != null && itemsFromORM.size() > 0) {
    		for (ToDoItem_Week05 item:itemsFromORM) {
    			items.add(item.name);
    		}
    	}
    }
    
    private void saveAllItemsToDb() {
    	
    	// Delete all old items
    	new Delete().from(ToDoItem_Week05.class).execute();
    	
    	ActiveAndroid.beginTransaction();
    	try {
    		for (String todo:items) {
    			ToDoItem_Week05 item = new ToDoItem_Week05(todo);
    			item.save();
    		}
    		ActiveAndroid.setTransactionSuccessful();
    	} finally {
    		ActiveAndroid.endTransaction();
    	}
    }
    
    // Save a single item to the DB
    private void saveItemToDb(String todo) {
    	ActiveAndroid.beginTransaction();
    	try {
			ToDoItem_Week05 item = new ToDoItem_Week05(todo);
			item.save();
    		ActiveAndroid.setTransactionSuccessful();
    	} finally {
    		ActiveAndroid.endTransaction();
    	}
    }
    
    // Delete a single item from the DB
	private void deleteItemFromDb(String item) {
    	ActiveAndroid.beginTransaction();
    	try {
    		new Delete().from(ToDoItem_Week05.class).where("name = ?", item).execute();
    		ActiveAndroid.setTransactionSuccessful();
    	} finally {
    		ActiveAndroid.endTransaction();
    	}
		
	}
    
}

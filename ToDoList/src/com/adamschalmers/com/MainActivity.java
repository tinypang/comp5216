package com.adamschalmers.com;

import android.app.Activity;
import android.app.AlertDialog;

import java.util.ArrayList;

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
	
	//define variables
	ListView listview;
	ArrayList<String> items;
	ArrayAdapter<String> itemsAdapter;
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
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        
        listview.setAdapter(itemsAdapter);
        
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
    
    public void onAddItemClick(View view) {
    	String toAddString = addItemEditText.getText().toString();
    	if (toAddString != null && toAddString.length() > 0) {
    		itemsAdapter.add(toAddString);
    		addItemEditText.setText("");
    	}
    }
    
    private void setupListViewListener(){
    	listview.setOnItemLongClickListener(new OnItemLongClickListener() {
    		public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long rowId) {
    			Log.i("MainActivity", "Long Clicked item" + position);
    			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    			builder.setTitle(R.string.dialog_delete_title)
    				.setMessage(R.string.dialog_delete_msg)
    				.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    						//delete item
    		    			items.remove(position);
    		    			itemsAdapter.notifyDataSetChanged();
    					}
    				})
    				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id){}
    				});
    			
    			builder.create().show();
    					
    			
    			
    			return true;
    		}
    	});
    	listview.setOnItemClickListener(new OnItemClickListener() {
    		public void onItemClick(AdapterView <? > parent, View view, int position, long id) {
	    		String updateItem = (String) itemsAdapter.getItem(position);
	    		Log.i("MainActiviy","Clicked item " + position + ": " + updateItem);
	    		
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
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == EDIT_ITEM_REQUEST_CODE) {
    		if (resultCode == RESULT_OK) {
    			// Extract name value from result extras
    			String editedItem = data.getExtras().getString("item");
    			int position = data.getIntExtra("position",  -1);
    			items.set(position, editedItem);
    			Log.i("Updated Item in list:", editedItem);
    			Toast.makeText(this,  "updated:" + editedItem, Toast.LENGTH_SHORT).show();
    			itemsAdapter.notifyDataSetChanged();
    		}
    	}
    }
    
}

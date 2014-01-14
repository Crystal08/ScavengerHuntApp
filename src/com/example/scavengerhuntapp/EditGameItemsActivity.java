package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EditGameItemsActivity extends Activity {  
  private EditText userInput;
  private Button addItemButton;
  private Button doneButton;
  private Button cancelButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameitemsedit);
    listCurrentItems();
    setupButtonCallbacks();
  }
  
  private void listCurrentItems(){
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
    final Intent i = getIntent(); 
    final Context context = this;
    query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
      public void done(final ParseObject gameInfo, ParseException e) {
        if (e == null) {
          final ParseQuery<ParseObject> item_query = ParseQuery.getQuery("Item");
          item_query.whereEqualTo("gameId", gameInfo.getObjectId());
          item_query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> itemsList, ParseException e) {
              if (e == null) {
                //convert List<ParseObject> to List<String> to be accepted by ArrayAdapter
                final List<String> itemNamesList = new ArrayList<String>();
                for(int i = 0; i < itemsList.size(); i++){
                  itemNamesList.add(itemsList.get(i).getString("itemName")); 
                }
                //list current items
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemNamesList); 
                final ListView listView = (ListView) findViewById(R.id.gameItemsEditListView);
                listView.setAdapter(adapter); 
                
                //delete item onClick and refresh list displayed
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                  public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    //delete Parse record of clicked item
                    final String item = (String) parent.getItemAtPosition(position);
                    final ParseQuery<ParseObject> remove_item_query = ParseQuery.getQuery("Item");
                    remove_item_query.whereEqualTo("itemName", item);
                    remove_item_query.getFirstInBackground(new GetCallback<ParseObject>() {
                      public void done(final ParseObject item, ParseException e) {
                        if (e == null) {
                          item.deleteInBackground(); 
                          final ParseQuery<ParseObject> updated_item_query = ParseQuery.getQuery("Item");
                          updated_item_query.whereEqualTo("gameId", gameInfo.getObjectId());
                          updated_item_query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(final List<ParseObject> updated_itemsList, ParseException exc) {
                              if (exc == null) {
                                final List<String> updatedItemNamesList = new ArrayList<String>();
                                for(int j = 0; j < updatedItemNamesList.size(); j++) {
                                  updatedItemNamesList.add(updated_itemsList.get(j).getString("itemName"));
                                }
                                final ArrayAdapter<String> updated_adapter = new ArrayAdapter<String> (context, android.R.layout.simple_list_item_1, updatedItemNamesList);
                                listView.setAdapter(updated_adapter);
                                finish();
                                startActivity(getIntent());
                              }
                              else {
                                Log.d("ScavengerHuntApp", "Error retrieving updated_itemsList: " + Log.getStackTraceString(exc));
                              }
                            }
                          });
                        }
                        else {
                          Log.d("ScavengerHuntApp", "Error retrieving item to delete: " + Log.getStackTraceString(e));
                        }
                      }
                    });
                  }
                });  
                
              }
              else {
                Log.d("ScavengerHuntApp", "itemsList retrieval error: " + Log.getStackTraceString(e));
              }
            }
          });  
        }  
        else {
          CharSequence text = "Sorry, there was a problem. Just a sec.";
          int duration = Toast.LENGTH_SHORT;                     
          Toast.makeText(context, text, duration).show();
          finish();
          startActivity(getIntent()); 
        }
      }
    });  
  }
  
  private void setupButtonCallbacks() {
    userInput = (EditText) findViewById(R.id.enterItem);
    addItemButton = (Button) findViewById(R.id.editItemsButton_addItem);
    addItemButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
        final Intent i = getIntent();    
        query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
          public void done(ParseObject gameInfo, ParseException e) {
            if (e == null) {
              final String new_item = userInput.getText().toString().trim();
              final ParseObject newItem = new ParseObject("Item");
              newItem.put("itemName", new_item);
              newItem.put("ownerId", ParseUser.getCurrentUser().getObjectId());
              newItem.put("gameId", gameInfo.getObjectId());
              newItem.saveInBackground();
              finish();
              startActivity(getIntent());
            }    
            else{
              Context context = getApplicationContext();
              CharSequence text = "Sorry, item did not save. Please try again.";
              int duration = Toast.LENGTH_SHORT;                     
              Toast.makeText(context, text, duration).show();
              Log.d("ScavengerHuntApp", "ParseObject retrieval error: " + Log.getStackTraceString(e));
              finish();
              startActivity(getIntent());
            }
          }  
        });    
      } 
    }); 
    doneButton = (Button) findViewById(R.id.editItemsButton_done); 
    doneButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Context context = getApplicationContext();
        CharSequence text = "Game Items Updated";
        int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
        finish();
        final Intent intent = getIntent();    
        final String gameInfoId = intent.getStringExtra("gameInfoId");
        final Intent i = new Intent(EditGameItemsActivity.this, EditGamePlayersActivity.class);
        i.putExtra("gameInfoId", gameInfoId);        
        EditGameItemsActivity.this.startActivity(i);
      } 
    });
    cancelButton = (Button) findViewById(R.id.editItemsButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
        Intent i = new Intent(EditGameItemsActivity.this, MainMenuActivity.class);
        EditGameItemsActivity.this.startActivity(i);
      }
    });
  }    

}

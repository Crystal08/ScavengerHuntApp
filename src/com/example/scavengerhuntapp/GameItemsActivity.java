package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

public class GameItemsActivity extends Activity {  
  private EditText userInput;
  private Button addItemButton;
  private Button doneButton;
  private Button cancelButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameitemsmanage);
    listCurrentItems();
    setupButtonCallbacks();
  }
  
  private void listCurrentItems(){
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
    final Intent i = getIntent(); 
    final Context context = this;
    query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
      public void done(ParseObject gameInfo, ParseException e) {
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
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemNamesList); 
                ListView listView = (ListView) findViewById(R.id.gameItemsListView);
                listView.setAdapter(adapter);  
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
    userInput = (EditText) findViewById(R.id.enterText);
    addItemButton = (Button) findViewById(R.id.manageItemsButton_addItem);
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
    doneButton = (Button) findViewById(R.id.manageItemsButton_done); 
    doneButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Context context = getApplicationContext();
        CharSequence text = "Game Items Saved";
        int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
        finish();
        final Intent intent = getIntent();    
        final String gameInfoId = intent.getStringExtra("gameInfoId");
        final Intent i = new Intent(GameItemsActivity.this, GamePlayersActivity.class);
        i.putExtra("gameInfoId", gameInfoId);        
        GameItemsActivity.this.startActivity(i);
      } 
    });
    cancelButton = (Button) findViewById(R.id.manageItemsButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
        Intent i = new Intent(GameItemsActivity.this, MainMenuActivity.class);
        GameItemsActivity.this.startActivity(i);
      }
    });
  }    

}




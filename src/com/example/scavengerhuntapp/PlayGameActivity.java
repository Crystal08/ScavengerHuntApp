package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PlayGameActivity extends Activity {  
  private Button doneButton;
  private Button cancelButton;
  private List<ParseObject> itemList;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.playgame);
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
                //list current items, with checkboxes
                itemList = itemsList;
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, itemNamesList); 
                final ListView itemListView = (ListView) findViewById(R.id.playGameItemsListView);
                itemListView.setAdapter(adapter); 
                itemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
              }
              else {
                Log.d("ScavengerHuntApp", "Error retrieving itemsList: " + Log.getStackTraceString(e));
              }
            }  
          });
        }
        else {
          final CharSequence text = "Sorry, there was a problem. Just a sec.";
          final int duration = Toast.LENGTH_SHORT;                     
          Toast.makeText(context, text, duration).show();
          finish();
          startActivity(getIntent()); 
        }
      }
    });  
  }
  
  private void setupButtonCallbacks() {
    doneButton = (Button) findViewById(R.id.playGameButton_done); 
    doneButton.setOnClickListener(new OnClickListener() {
      public void onClick(final View v) {
        saveFoundItems(getSelectedItems());
        final Context context = getApplicationContext();
        final CharSequence text = "Found Items Saved";
        final int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
        finish();
        //final Intent intent = getIntent();    
        //final String gameInfoId = intent.getStringExtra("gameInfoId");
        final Intent i = new Intent(PlayGameActivity.this, MainMenuActivity.class);
        //i.putExtra("gameInfoId", gameInfoId);        
        PlayGameActivity.this.startActivity(i);
      } 
    });
    
    cancelButton = (Button) findViewById(R.id.playGameButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(final View v) {
        finish();
        Intent i = new Intent(PlayGameActivity.this, MainMenuActivity.class);
        PlayGameActivity.this.startActivity(i);
      }
    });
  }  
  
  private List<ParseObject> getSelectedItems() {
    final List<ParseObject> selectedItems = new ArrayList<ParseObject>();
    final ListView itemListView = (ListView) findViewById(R.id.playGameItemsListView);
    final SparseBooleanArray checkedItems = itemListView.getCheckedItemPositions();
    if (checkedItems != null) {
      for(int i = 0; i < checkedItems.size(); i++){
        if (checkedItems.valueAt(i)) { //was getting an invalid index 0 error, so check here
          selectedItems.add(itemList.get(checkedItems.keyAt(i))); 
        }
      }
    }
    return selectedItems;
  }
  
  private void saveFoundItems(final List<ParseObject> itemsList) {
    final Intent intent = getIntent();
    for(int i = 0; i < itemsList.size(); i++){
      final ParseObject foundItem= new ParseObject("foundItem");
      foundItem.put("itemId", itemsList.get(i).getObjectId());
      foundItem.put("gameId", intent.getStringExtra("gameInfoId"));
      foundItem.put("playerId", ParseUser.getCurrentUser().getObjectId());
      foundItem.saveInBackground(new SaveCallback(){
        public void done(ParseException e) {
          if (e== null){
            Log.d("ScavengerHuntApp", "Item found record saved: " + foundItem.getObjectId());
          }
          else {
            Log.d("ScavengerHuntApp", "Item found record not saved: "+Log.getStackTraceString(e));
          }
        }   
      });
    }
  }

}




package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.Collections;
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
  private ParseObject game;
  private List<ParseUser> winners = new ArrayList<ParseUser>();
  private ArrayList<String> winnerIds = new ArrayList<String>();
  private List<ParseObject> allGameItems = new ArrayList<ParseObject>();
  private List<ParseObject> listItems  = new ArrayList<ParseObject>();
  private List<ParseObject> foundListItems = new ArrayList<ParseObject>();
  private List<Integer> foundItemsListPlaces = new ArrayList<Integer>();
  //private List<ParseObject> availableItems;

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.playgame);
    listCurrentItems();
    setupButtonCallbacks();
  }
  
  private void listCurrentItems(){
    //prepare listview and adapter
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice);
    final ListView listview = (ListView) findViewById(R.id.playGameItemsListView);
    listview.setAdapter(adapter);
    listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);
    
    //get game
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
    final Intent i = getIntent(); 
    final Context context = this;
    query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
      public void done(final ParseObject gameInfo, ParseException e) {
        if (e == null) {
          game = gameInfo;
          //get items for game
          final ParseQuery<ParseObject> item_query = ParseQuery.getQuery("Item");
          item_query.whereEqualTo("gameId", gameInfo.getObjectId());
          item_query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> itemsList, ParseException e) {
              if (e == null) {
                allGameItems = itemsList;
                for ( final ParseObject item : itemsList) {
                  final ParseQuery<ParseObject> foundItem_query = ParseQuery.getQuery("foundItem");
                  foundItem_query.whereEqualTo("itemId", item.getObjectId());
                  foundItem_query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(final List<ParseObject> foundItemRecords, ParseException e) {
                      if (e == null) {
                        if (foundItemRecords.isEmpty()) {
                          listItems.add(item);
                          adapter.add(item.getString("itemName"));
                          adapter.notifyDataSetChanged();
                        }
                        else {
                          listItems.add(item); 
                          foundListItems.add(item);
                          adapter.add(item.getString("itemName"));
                          listview.setItemChecked(listItems.indexOf(item), true);
                          foundItemsListPlaces.add(listItems.indexOf(item));
                          adapter.notifyDataSetChanged(); 
                        }
                      }
                      else {
                        Log.d("ScavengerHuntApp", "Error retrieving original foundItemRecords: " + Log.getStackTraceString(e));
                      }
                    } 
                  });  
                }
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
    final ListView availableItemsListView = (ListView) findViewById(R.id.playGameItemsListView);
    final SparseBooleanArray checkedItems = availableItemsListView.getCheckedItemPositions();
    if (checkedItems != null) {
      for(int i = 0; i < checkedItems.size(); i++){
        if (checkedItems.valueAt(i)) { //was getting an invalid index 0 error, so check here
          if (foundItemsListPlaces.contains(i)){
            Log.d("ScavengerHuntApp", "Item at position " + i +" already found.");            
          }
          else {
            selectedItems.add(listItems.get(checkedItems.keyAt(i)));  
          }
        }
      }
    }
    return selectedItems;
  } 
  
  private void saveFoundItems(final List<ParseObject> itemsList) {
    if (itemsList != null) {
      final Intent intent = getIntent();
      final String gameId = intent.getStringExtra("gameInfoId");
      final String playerId = ParseUser.getCurrentUser().getObjectId();
      final List<ParseObject> newFindsList = new ArrayList<ParseObject>();
      
      for(int i = 0; i < itemsList.size(); i++){
        ParseObject foundItem= new ParseObject("foundItem");
        foundItem.put("itemId", itemsList.get(i).getObjectId());
        foundItem.put("gameId", gameId);
        foundItem.put("playerId", playerId);
        newFindsList.add(foundItem);
      }  
    
      ParseObject.saveAllInBackground( newFindsList, new SaveCallback() {
        public void done(ParseException e) {
          if (e== null){
            //check if game is over due to all items found 
            final ParseQuery<ParseObject> found_query = ParseQuery.getQuery("foundItem");
            found_query.whereEqualTo("gameId", intent.getStringExtra("gameInfoId"));
            found_query.findInBackground(new FindCallback<ParseObject>() {
              public void done(final List<ParseObject> foundItems, ParseException e) {
                if (e == null) {
                  if (foundItems.size() == allGameItems.size()) {
                    final List<ParseObject> winners = returnHighestFinders(foundItems);
                    final Context context = getApplicationContext();
                    final CharSequence text = "Game Over- All Items Found!";
                    final int duration = Toast.LENGTH_SHORT;                     
                    Toast.makeText(context, text, duration).show();
                    finish();
                    final Intent a = new Intent(PlayGameActivity.this, ViewMyInvitedGamesActivity.class);       
                    PlayGameActivity.this.startActivity(a);       
                  }
                  else {
                    final Context context = getApplicationContext();
                    final CharSequence text = "More To Find- Keep Looking!";
                    final int duration = Toast.LENGTH_SHORT;                     
                    Toast.makeText(context, text, duration).show();
                    finish();
                    final Intent i = new Intent(PlayGameActivity.this, MainMenuActivity.class);       
                    PlayGameActivity.this.startActivity(i);    
                  }
                }
                else {
                  Log.d("ScavengerHuntApp", "Error retrieving update foundItems: " + Log.getStackTraceString(e));  
                }
              }
            });  
          }
          else {
            Log.d("ScavengerHuntApp", "New items found records not saved: "+Log.getStackTraceString(e));
          }
        }   
      });
    }
    else {
      final Context context = getApplicationContext();
      final CharSequence text = "No New Finds- Keep Looking!";
      final int duration = Toast.LENGTH_SHORT;                     
      Toast.makeText(context, text, duration).show();
      finish();
      final Intent i = new Intent(PlayGameActivity.this, MainMenuActivity.class);       
      PlayGameActivity.this.startActivity(i);
    }
  }

  private List<ParseObject>  returnHighestFinders(List<ParseObject> foundItems) {
    final List<ParseObject> highestFinders = new ArrayList<ParseObject>();
    return highestFinders;
  }

}      
      
     
  
  //for getting winners, not used yet
  //private List<ParseUser> returnFindersFor(int how_many, List<ParseObject> foundItems) {
  //  final List<ParseUser> finders = new ArrayList<ParseUser>();
  //  final List<String> finderIds = new ArrayList<String>();
  //  final ArrayList<String> userIds = new ArrayList<String>();
  //  int counter = 0;
  //  for( ParseObject item : foundItems) {
  //    final ParseQuery<ParseObject> user_query = ParseQuery.getQuery("User");
  //    user_query.getInBackground(item.getString("playerId"), new GetCallback<ParseObject>() {
  //        public void done(final ParseObject user, ParseException e) {
  //          if (e == null) {
  //            userIds.add(user.getObjectId());
  //          }
  //          else {
  //            Log.d("ScavengerHuntApp", "Error retrieving user for foundItem: "+ Log.getStackTraceString(e));
  //          }
  //        }
  //    });    
      
  //    counter++;
  //    if (counter == foundItems.size()) { //check for loop through foundItems to be done
  //      for ( String id : userIds) {
  //        int freq = Collections.frequency(userIds, id);
  //        if ( freq == how_many) {
  //          finderIds.add(id);  
  //        }
  //      }
  //      for( String id: finderIds) {
  //        final ParseQuery<ParseUser> finder_query = ParseQuery.getQuery("User");
  //        finder_query.getInBackground(id, new GetCallback<ParseUser>() {
  //          public void done(final ParseUser user, ParseException e) {
  //            if (e == null) {
  //              finders.add(user);  
  //            }
  //            else {
  //              Log.d("ScavengerHuntApp", "Error retrieving users for finderIds matching given freq: "+ Log.getStackTraceString(e));
  //            }
  //          }
  //        });  
  //      }     
  //    }
  //  }
  //  for (int i = 0; i < finders.size(); i++) {
  //    Log.d("ScavengerHuntApp", "finder: " + finders.get(i).getString("username"));
  //  }
  //  return finders;
  //}
//}

   




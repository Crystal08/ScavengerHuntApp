package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.Arrays;
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

public class EditGamePlayersActivity extends Activity {  
  private Button doneButton;
  private Button cancelButton;
  private List<ParseObject> currentPlayers = new ArrayList<ParseObject>();
  private List<ParseUser> currentUsers = new ArrayList<ParseUser>();
  private List<ParseUser> playerList = new ArrayList<ParseUser>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameplayersedit);
    getCurrentPlayers();
    setupButtonCallbacks();
  }
  
  private void getCurrentPlayers() {
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gamePlayer");
    final Intent intent = getIntent();
    Log.d("ScavengerHuntApp", "gameInfoId: " + intent.getStringExtra("gameInfoId"));
    query.whereEqualTo("gameId", intent.getStringExtra("gameInfoId"));
    query.findInBackground(new FindCallback<ParseObject>() {
      public void done(List<ParseObject> players, ParseException e) {
        if (e == null) {
          currentPlayers = players;
          for(int i = 0; i < currentPlayers.size(); i++) {
            Log.d("ScavengerHuntApp", "currentPlayers retrieved: " + currentPlayers.get(i).getObjectId());
          } 
          getAllUsers();
        }
        else {
          Context context = getApplicationContext();
          CharSequence text = "Sorry, there was a problem retrieving current players. Please try again.";
          int duration = Toast.LENGTH_SHORT;                     
          Toast.makeText(context, text, duration).show();
          finish();
          startActivity(getIntent()); 
        }
      }
    });
    for (int i = 0; i < currentPlayers.size(); i++) {
      currentPlayers.get(i).getParseObject("User").fetchIfNeededInBackground(new GetCallback<ParseUser>() {
        public void done(ParseUser user, ParseException e) {
          if (e == null) {
            currentUsers.add(user);
          }
          else {
            Log.d("ScavengerHuntApp", "User retrieval error:" + Log.getStackTraceString(e));
          }
        }
      });
    }
  }
 
  private void getAllUsers() {
    final ArrayList<ParseUser> allUsers = new ArrayList<ParseUser>();
    final ParseQuery<ParseUser> query = ParseUser.getQuery();
    query.selectKeys(Arrays.asList("username"));
    query.findInBackground(new FindCallback<ParseUser>() {
      public void done(List<ParseUser> users, ParseException e) {
        if (e == null) {
          for(int i = 0; i < users.size(); i++) {
            allUsers.add(users.get(i));
            Log.d("ScavengerHuntApp", "Retrieved users: " + users.get(i).getString("username"));  
          }
          playerList = users;
          setPlayerListView(allUsers);
        }
        else {
          Log.d("ScavengerHuntApp", "Problem retrieving all users: " + Log.getStackTraceString(e));
        }
      }  
    });
  }
  
  private void setPlayerListView(ArrayList<ParseUser> allUsers) {
    Log.d("ScavengerHuntApp","allUsers for ListView: " + allUsers);
    final Context context = this;
    //identify current players on list of all users and save those list places
    int[] currentPlayersListPlaces = new int[currentPlayers.size()];
    for (int i = 0; i < currentPlayers.size(); i++) {
      for (int j = 0; j < allUsers.size(); j++) {
        String userId = allUsers.get(j).getObjectId();
        String currentPlayerId = currentPlayers.get(i).getString("playerId");
        Log.d("Find players on user list: ", currentPlayers.get(i).getString("playerId") +" "+ allUsers.get(j).getObjectId());
          if(userId.equals(currentPlayerId)) {
            currentPlayersListPlaces[i] = j;
            continue;
          }
       }
     }
     for(int f = 0; f < currentPlayersListPlaces.length; f++) {
       Log.d("ScavengerHuntApp", "currentPlayersListPlace: " + currentPlayersListPlaces[f]);  
     }
     //setup checklist of all users, with current players checked via the place values saved
     final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, getNamesList(allUsers));
     Log.d("ScavengerHuntApp", "Users' names: " + getNamesList(allUsers));
     final ListView userListView = (ListView) findViewById(R.id.gamePlayersEditListView);
     userListView.setAdapter(adapter);
     userListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
     for (int position : currentPlayersListPlaces) {
       userListView.setItemChecked(position,  true);
     } 
  }  
  
  private List<String> getNamesList(List<ParseUser> users) {
    List<String> namesList = new ArrayList<String>(); 
    for (int i = 0; i < users.size(); i++) {
      namesList.add(users.get(i).getString("username"));
    } 
    return namesList;
  }
  
  private void setupButtonCallbacks() {
    
    doneButton = (Button) findViewById(R.id.editPlayersButton_done); 
    doneButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        updateSelectedPlayers(getSelectedPlayers());             
        final Context context = getApplicationContext();   
        CharSequence text = "Game Updated";
        int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
        finish();
        final Intent i = new Intent(EditGamePlayersActivity.this, MainMenuActivity.class);
        EditGamePlayersActivity.this.startActivity(i);
      } 
    }); 
       
    cancelButton = (Button) findViewById(R.id.editPlayersButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
        Intent i = new Intent(EditGamePlayersActivity.this, MainMenuActivity.class);
        EditGamePlayersActivity.this.startActivity(i);
      }
    });
  }
    
  private List<ParseUser> getSelectedPlayers() {
    final List<ParseUser> selectedPlayers = new ArrayList<ParseUser>();
    final ListView playerListView = (ListView) findViewById(R.id.gamePlayersEditListView);
    final SparseBooleanArray checkedPlayers = playerListView.getCheckedItemPositions();
    if (checkedPlayers != null) {
      for(int i = 0; i < checkedPlayers.size(); i++){
        if (checkedPlayers.valueAt(i)) { 
          selectedPlayers.add(playerList.get(checkedPlayers.keyAt(i))); 
        }
      }
    }
    return selectedPlayers;
  }

  private void updateSelectedPlayers(List<ParseUser> selectedPlayersList){ 
    //get rid of former player records before saving the newly-selected ones
    for (ParseObject item: currentPlayers) {
      item.deleteInBackground();
    }
    for(int h = 0; h < selectedPlayersList.size(); h++){
      final ParseObject gamePlayer= new ParseObject("gamePlayer");
      final ParseUser player = selectedPlayersList.get(h); 
      final Intent intent = getIntent();
      gamePlayer.put("gameId", intent.getStringExtra("gameInfoId"));
      gamePlayer.put("playerId", player.getObjectId());
      gamePlayer.saveInBackground(new SaveCallback(){
        public void done(ParseException e) {
          if (e== null){
            Log.d("ScavengerHuntApp", "PlayerID saved: "+player.getObjectId().toString());
          }
          else {
            Log.d("ScavengerHuntApp", "PlayerID not saved: "+Log.getStackTraceString(e));
          }
        }   
      });
    }
  }    
 

    

}


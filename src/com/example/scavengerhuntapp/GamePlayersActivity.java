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
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class GamePlayersActivity extends Activity {  
  private Button doneButton;
  private Button cancelButton;
  private List<ParseUser> playerList = new ArrayList<ParseUser>();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameplayersmanage);
    listPotentialPlayers();
    setupButtonCallbacks();
  }
  
  private void listPotentialPlayers(){
    final ParseQuery<ParseUser> query = ParseUser.getQuery();
    final Context context = this;
    query.selectKeys(Arrays.asList("username"));
    query.findInBackground(new FindCallback<ParseUser>() {
      public void done(List<ParseUser> userList, ParseException e) {
        if (e == null) {
          if (userList != null) {        
            List<String> usernameList = new ArrayList<String>();
            for(int i = 0; i < userList.size(); i++){
              try{             
                usernameList.add(userList.get(i).getString("username"));
              }
              catch (Exception exc) {
                Log.d("ScavengerHuntApp", "Problem building user list: " + Log.getStackTraceString(exc));
              }
            }
            playerList = userList;
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, usernameList); 
            final ListView playerListView = (ListView) findViewById(R.id.gamePlayersListView);
            playerListView.setAdapter(adapter);
            playerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
          }
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
    
    doneButton = (Button) findViewById(R.id.managePlayersButton_done); 
    doneButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        saveSelectedPlayers(getSelectedPlayers());             
        final Context context = getApplicationContext();   
        CharSequence text = "New Game Saved";
        int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
        finish();
        final Intent i = new Intent(GamePlayersActivity.this, MainMenuActivity.class);
        GamePlayersActivity.this.startActivity(i);
      } 
    }); 
       
    cancelButton = (Button) findViewById(R.id.managePlayersButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
        Intent i = new Intent(GamePlayersActivity.this, MainMenuActivity.class);
        GamePlayersActivity.this.startActivity(i);
      }
    });
  }
  
  private List<ParseUser> getSelectedPlayers() {
    final List<ParseUser> selectedPlayers = new ArrayList<ParseUser>();
    final ListView playerListView = (ListView) findViewById(R.id.gamePlayersListView);
    final SparseBooleanArray checkedPlayers = playerListView.getCheckedItemPositions();
    if (checkedPlayers != null) {
      for(int i = 0; i < checkedPlayers.size(); i++){
        if (checkedPlayers.valueAt(i)) { //was getting an invalid index 0 error, so check here
          selectedPlayers.add(playerList.get(checkedPlayers.keyAt(i))); 
        }
      }
    }
    return selectedPlayers;
  }

  private void saveSelectedPlayers(final List<ParseUser> selectedPlayersList){
    final Intent intent = getIntent();
    for(int i = 0; i < selectedPlayersList.size(); i++){
      final ParseObject gamePlayer= new ParseObject("gamePlayer");
      final ParseUser player = selectedPlayersList.get(i); 
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
    inviteSelectedPlayers(intent.getStringExtra("gameInfoId"), selectedPlayersList);
  } 
  
  private void inviteSelectedPlayers(final String gameId, final List<ParseUser> selectedPlayersList) {
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");   
    query.getInBackground(gameId, new GetCallback<ParseObject>() {
      public void done(final ParseObject game, final ParseException e) {
        if (e == null) {
          for(final ParseUser invitee : selectedPlayersList) {
            //see https://parse.com/questions/push-notifications-on-android-to-specific-users
            ParsePush parsePush = new ParsePush();
            ParseQuery<ParseInstallation> pQuery = ParseInstallation.getQuery();
            pQuery.whereEqualTo("username", invitee);
            parsePush.setQuery(pQuery);
            parsePush.setMessage("You've been invited to play scavenger hunt: " + game.getString("gameName") + ", by " + ParseUser.getCurrentUser().getString("username"));
            parsePush.sendInBackground();
          }
        }
      }  
    });
  }  

}


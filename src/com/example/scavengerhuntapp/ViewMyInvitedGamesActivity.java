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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ViewMyInvitedGamesActivity extends Activity {  
  private Button backButton;
  final ParseUser currentUser = ParseUser.getCurrentUser();
  final List<ParseObject> myInvitedGames = new ArrayList<ParseObject>();
  final Context context = this;
  
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.viewmyinvitedgames);
    listMyInvitedGames();
    setupButtonCallbacks();
  }
  
  private void listMyInvitedGames(){
    //see http://stackoverflow.com/questions/13573847/trying-to-populate-listview-in-android-using-objects-from-parse
    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
    final ListView listview = (ListView) findViewById(R.id.listview_myInvitedGames);
    listview.setAdapter(adapter);
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gamePlayer");
    query.whereEqualTo("playerId", currentUser.getObjectId());
    query.findInBackground(new FindCallback<ParseObject>() {
      public void done(final List<ParseObject> gamePlayers, ParseException e) {
        if (e == null) {
          Log.d("ScavengerHuntApp","Player records retrieved: "+ gamePlayers.toString());
          for (final ParseObject gamePlayer : gamePlayers) {
            final ParseQuery<ParseObject> game_query = ParseQuery.getQuery("gameInfo");
            game_query.getInBackground(gamePlayer.getString("gameId"), new GetCallback<ParseObject>() {
              public void done(final ParseObject gameInfo, ParseException e) {
                if (e == null) { 
                  Log.d("ScavengerHuntApp", "Game record retrieved: " + gameInfo.getString("gameName"));  
                  adapter.add(gameInfo.getString("gameName"));
                  adapter.notifyDataSetChanged();
                  myInvitedGames.add(gameInfo);
                  
                }
                else {
                  Log.d("ScavengerHuntApp", "Problem retrieving game: " + Log.getStackTraceString(e));
                }
              }
            }); 
          }

          listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view, int place, long id) {
              final ParseObject game = myInvitedGames.get(place);
              final Intent i = new Intent(ViewMyInvitedGamesActivity.this, PlayGameActivity.class);
              final String gameInfoId = game.getObjectId();
              i.putExtra("gameInfoId", gameInfoId);
              ViewMyInvitedGamesActivity.this.startActivity(i);
            }
          });  
        }
        else {
          Log.d("ScavengerHuntApp", "Problem retrieving games: "+Log.getStackTraceString(e));  
        }     
      }  
    });
  }    
   
  private void setupButtonCallbacks() { 
    backButton = (Button) findViewById(R.id.viewMyInvitedGamesButton_back); 
    backButton.setOnClickListener(new OnClickListener() {
      public void onClick(final View v) {
        finish();
        Intent i = new Intent(ViewMyInvitedGamesActivity.this, MainMenuActivity.class);
        ViewMyInvitedGamesActivity.this.startActivity(i);
      }
    });
  }    

}




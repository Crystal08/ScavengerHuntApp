package com.example.scavengerhuntapp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.Toast;

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
    
    //get all games user has been invited to
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gamePlayer");
    query.whereEqualTo("playerId", currentUser.getObjectId());
    query.findInBackground(new FindCallback<ParseObject>() {
      public void done(final List<ParseObject> gamePlayers, ParseException e) {
        if (e == null) {
          
          if (gamePlayers.isEmpty()) {
            final CharSequence text = "No games available right now- why not start your own?";
            final int duration = Toast.LENGTH_LONG;                     
            Toast.makeText(context, text, duration).show();
            finish();
            final Intent i = new Intent(ViewMyInvitedGamesActivity.this, MainMenuActivity.class);
            ViewMyInvitedGamesActivity.this.startActivity(i);       
          }
          
          Log.d("ScavengerHuntApp","Player records retrieved: "+ gamePlayers.toString());
          for (final ParseObject gamePlayer : gamePlayers) {
            final ParseQuery<ParseObject> game_query = ParseQuery.getQuery("gameInfo");
            game_query.getInBackground(gamePlayer.getString("gameId"), new GetCallback<ParseObject>() {
              public void done(final ParseObject game, ParseException e) {
                
                if (e == null) { 
                  Log.d("ScavengerHuntApp", "Game record retrieved: " + game.getString("gameName"));
                  //check for game status: Game Open: Your finds- xxx; Game Expired on enddatetime; Game Won by username
                  final SimpleDateFormat sdf_datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm");                
                  final String game_endDateTime = sdf_datetime.format(game.getDate("gameEndDateTime"));
                  final String game_startDateTime = sdf_datetime.format(game.getDate("gameStartDateTime"));
                  final Date today = new Date();
                  
                  try {
                    switch (game.getDate("gameEndDateTime").compareTo(today)){
                      case -1: //game end date was before today 
                        adapter.add(game.getString("gameName") + "- Game Over (expired " + game_endDateTime + ")");
                        adapter.notifyDataSetChanged();
                        myInvitedGames.add(game);
                      break;
                      case 0: //game end date equal to today  
                        adapter.add(game.getString("gameName") + "- Game Open (until " + game_endDateTime + ")");
                        adapter.notifyDataSetChanged();
                        myInvitedGames.add(game);
                      break;
                      case 1: //game end date is after today
                        switch (game.getDate("gameStartDateTime").compareTo(today)) {
                          case -1: case 0: //game start date was before today or equal to today
                            adapter.add(game.getString("gameName") + "- Game Open (until " + game_endDateTime + ")");
                            adapter.notifyDataSetChanged();
                            myInvitedGames.add(game);
                          break;
                          case 1: //game start date is after today
                            adapter.add(game.getString("gameName") + "- Game Pending (opens on " + game_startDateTime + ")");
                            adapter.notifyDataSetChanged();
                          break; 
                          default:
                            final CharSequence text = "Sorry, there was a problem with the game dates.";
                            final int duration = Toast.LENGTH_SHORT;                     
                            Toast.makeText(context, text, duration).show();
                            finish();
                            startActivity(getIntent());     
                        }
                      break;
                      default: 
                        final CharSequence text = "Sorry, there was a problem with the game dates.";
                        final int duration = Toast.LENGTH_SHORT;                     
                        Toast.makeText(context, text, duration).show();
                        finish();
                        startActivity(getIntent());  
                    }
                  }
                  
                  catch (final NullPointerException null_exc) {
                    final CharSequence text = "Sorry, there was a problem with the game dates.";
                    final int duration = Toast.LENGTH_SHORT;                     
                    Toast.makeText(context, text, duration).show();
                    finish();
                    startActivity(getIntent());   
                  }
                  
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
          Log.d("ScavengerHuntApp", "Problem retrieving gamePlayers: "+Log.getStackTraceString(e));  
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




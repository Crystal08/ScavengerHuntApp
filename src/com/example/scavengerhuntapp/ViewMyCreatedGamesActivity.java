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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ViewMyCreatedGamesActivity extends Activity {  
  private Button backButton;
  final ParseUser currentUser = ParseUser.getCurrentUser();
  List<ParseObject> myCreatedGames;
  List<String> gameNamesList;
  final Context context = this;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.viewmycreatedgames);
    listMyGames();
    setupButtonCallbacks();
  }
  
  private void listMyGames(){
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
    query.whereEqualTo("gameOwner", currentUser.getObjectId());
    query.findInBackground(new FindCallback<ParseObject>() {
      public void done(List<ParseObject> gamesList, ParseException e) {
        if (e == null) {
          Log.d("ScavengerHuntApp","Games retrieved: "+ gamesList.toString());
          myCreatedGames = gamesList;
          gameNamesList = new ArrayList<String>();
          for (final ParseObject game : myCreatedGames){
            gameNamesList.add(game.getString("gameName"));
          }  
        }
        else {
          Log.d("ScavengerHuntApp", "Problem retrieving games: "+Log.getStackTraceString(e));  
        }     
      
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, gameNamesList);
        final ListView listview = (ListView) findViewById(R.id.listview_myCreatedGames);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, final View view, int place, long id) {
            final ParseObject game = myCreatedGames.get(place);
            Intent i = new Intent(ViewMyCreatedGamesActivity.this, EditGameActivity.class);
            final String gameInfoId = game.getObjectId();
            i.putExtra("gameInfoId", gameInfoId);
            ViewMyCreatedGamesActivity.this.startActivity(i);
          }
        });
      }  
    });
  }    
   
  private void setupButtonCallbacks() { 
    backButton = (Button) findViewById(R.id.manageItemsButton_done); 
    backButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
        Intent i = new Intent(ViewMyCreatedGamesActivity.this, MainMenuActivity.class);
        ViewMyCreatedGamesActivity.this.startActivity(i);
      }
    });
  }    

}




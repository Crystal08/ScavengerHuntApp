package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class GamePlayersActivity extends Activity {  
  private Button doneButton;
  private Button cancelButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameplayersmanage);
    listCurrentPlayers();
    setupButtonCallbacks();
  }
  
  private void listCurrentPlayers(){
    final ParseQuery<ParseUser> query = ParseUser.getQuery();
    final Context context = this;
    query.selectKeys(Arrays.asList("username"));
    query.findInBackground(new FindCallback<ParseUser>() {
      public void done(List<ParseUser> userList, ParseException e) {
        if (e == null) {
          if (userList != null) {        
            //Now have to convert JSONArray 'userList' to String Array 'usernameList' so that ArrayAdapter will accept it as argument
            List<String> usernameList = new ArrayList<String>();
            for(int i = 0; i < userList.size(); i++){
              try{             
                usernameList.add(userList.get(i).getString("username"));
              }
              catch (Exception exc) {
                Log.d("ScavengerHuntApp", "Problem building user list: " + Log.getStackTraceString(exc));
              }
            }
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
        Context context = getApplicationContext();
        CharSequence text = "New Game Saved";
        int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
        finish();
        Intent i = new Intent(GamePlayersActivity.this, MainMenuActivity.class);
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

}


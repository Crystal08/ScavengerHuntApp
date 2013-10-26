package com.example.scavengerhuntapp;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class NewGameActivity extends Activity {  
  private EditText userInput;
  private Button newGameButton;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.newgamecreate);
    setupButtonCallback();
  }
   
  private void setupButtonCallback() {
   userInput = (EditText) findViewById(R.id.enterText);
   newGameButton = (Button) findViewById(R.id.mainMenuButton_newGame);
   newGameButton.setOnClickListener(new OnClickListener() {
     public void onClick(View v) {
       final ParseObject gameInfo = new ParseObject("gameInfo");
       final String game_name = userInput.getText().toString().trim(); 
       gameInfo.put("gameName", game_name); 
       gameInfo.saveInBackground(
           new SaveCallback() {
           public void done(final ParseException e) {
             if (e == null) {
               final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
               query.findInBackground(new FindCallback<ParseObject>() { 
                 public void done(List<ParseObject> objects, final ParseException e) {
                   if (e == null) {
                     Log.d("ScavengerHuntApp", objects.toString());
                   }
                   else{
                     Log.d("ScavengerHuntApp", "ParseObject retrieval error: " + Log.getStackTraceString(e));
                   }
                 }
               });            
             }
             else {}
           }  
       });    
     } 
    });  
  }    

  
}




package com.example.scavengerhuntapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
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
                  Context context = getApplicationContext();
                  CharSequence text = "New Game Saved";
                  int duration = Toast.LENGTH_SHORT;                     
                  Toast.makeText(context, text, duration).show();
                  final String gameInfoId = gameInfo.getObjectId();
                  Intent i = new Intent(NewGameActivity.this, GameItemsActivity.class);
                  i.putExtra("gameInfoId", gameInfoId);
                  NewGameActivity.this.startActivity(i);
                }
                else{
                  Context context = getApplicationContext();
                  CharSequence text = "Sorry, game did not save. Please try again.";
                  int duration = Toast.LENGTH_SHORT;                     
                  Toast.makeText(context, text, duration).show();
                }
              }  
            });      
      }
    });   
  } 

}    

  





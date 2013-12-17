package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class GameItemsActivity extends Activity {  
  private EditText userInput;
  private Button addItemButton;
  private Button doneButton;
  private Button cancelButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameitemsmanage);
    listCurrentItems();
    setupButtonCallbacks();
  }
  
  private void listCurrentItems(){
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
    final Intent i = getIntent(); 
    final Context context = this;
    query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
      public void done(ParseObject gameInfo, ParseException e) {
        if (e == null) {
          JSONArray items = gameInfo.getJSONArray("itemsList"); 
          if (items != null) {        
            //Now have to convert JSONArray 'items' to String Array 'itemsList' so that ArrayAdapter will accept it as argument
            List<String> itemsList = new ArrayList<String>();
            for(int i = 0; i < items.length(); i++){
              try{             
                itemsList.add(items.getString(i));
              }
              catch (Exception exc) {
                Log.d("ScavengerHuntApp", "JSONObject exception: " + Log.getStackTraceString(exc));
              }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList); 
            ListView listView = (ListView) findViewById(R.id.listView1);
            listView.setAdapter(adapter);  
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
    userInput = (EditText) findViewById(R.id.enterText);
    addItemButton = (Button) findViewById(R.id.manageItemsButton_addItem);
    addItemButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
        final Intent i = getIntent();    
        query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
          public void done(ParseObject gameInfo, ParseException e) {
            if (e == null) {
              final String new_item = userInput.getText().toString().trim(); 
              JSONArray items = gameInfo.getJSONArray("itemsList"); 
              if (items != null) {
                items.put(new_item); 
                gameInfo.put("itemsList", items);   
                gameInfo.saveInBackground();
                finish();
                startActivity(getIntent()); 
              }  
              else { 
                JSONArray new_items = new JSONArray();
                new_items.put(new_item);
                gameInfo.put("itemsList", new_items);
                gameInfo.saveInBackground();
                finish();
                startActivity(getIntent());
             }
            }    
            else{
              Context context = getApplicationContext();
              CharSequence text = "Sorry, item did not save. Please try again.";
              int duration = Toast.LENGTH_SHORT;                     
              Toast.makeText(context, text, duration).show();
              Log.d("ScavengerHuntApp", "ParseObject retrieval error: " + Log.getStackTraceString(e));
              finish();
              startActivity(getIntent());
            }
          }  
        });    
      } 
    }); 
    doneButton = (Button) findViewById(R.id.manageItemsButton_done); 
    doneButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Context context = getApplicationContext();
        CharSequence text = "New Game Saved";
        int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
        finish();
        Intent i = new Intent(GameItemsActivity.this, MainMenuActivity.class);
        GameItemsActivity.this.startActivity(i);
      } 
    });
    cancelButton = (Button) findViewById(R.id.manageItemsButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
        Intent i = new Intent(GameItemsActivity.this, MainMenuActivity.class);
        GameItemsActivity.this.startActivity(i);
      }
    });
  }    

}




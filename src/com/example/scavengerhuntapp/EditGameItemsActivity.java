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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class EditGameItemsActivity extends Activity {  
  private EditText userInput;
  private Button addItemButton;
  private Button doneButton;
  private Button cancelButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.gameitemsedit);
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
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, itemsList); 
            ListView listView = (ListView) findViewById(R.id.gameItemsEditListView);
            listView.setAdapter(adapter); 
            //click on an item to remove it
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                adapter.remove(item);
                adapter.notifyDataSetChanged();
                view.setAlpha(1);
                //remove item from Parse by saving only what is now displayed in list
                //set list
                final ArrayList<String> currentItemList = new ArrayList<String>();
                for(int i = 0; i < (adapter.getCount()); i++) {
                  currentItemList.add(adapter.getItem(i));
                }
                //save list
                final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
                final Intent i = getIntent();    
                query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
                  public void done(ParseObject gameInfo, ParseException e) {
                    if (e == null) { 
                      gameInfo.put("itemsList", currentItemList);
                      gameInfo.saveInBackground();
                      finish();
                      startActivity(getIntent()); 
                    }     
                    else{
                      Context context = getApplicationContext();
                      CharSequence text = "Sorry, there was a problem saving that update. Please try again.";
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
    userInput = (EditText) findViewById(R.id.enterItem);
    addItemButton = (Button) findViewById(R.id.editItemsButton_addItem);
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
    doneButton = (Button) findViewById(R.id.editItemsButton_continue); 
    doneButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Context context = getApplicationContext();
        CharSequence text = "Game Items Updated";
        int duration = Toast.LENGTH_SHORT;                     
        Toast.makeText(context, text, duration).show();
        finish();
        final Intent intent = getIntent();    
        final String gameInfoId = intent.getStringExtra("gameInfoId");
        Log.d("ScavengerHuntApp", "gameInfoId: " + gameInfoId);
        final Intent i = new Intent(EditGameItemsActivity.this, EditGamePlayersActivity.class);
        i.putExtra("gameInfoId", gameInfoId);  
        EditGameItemsActivity.this.startActivity(i);
      } 
    });
    cancelButton = (Button) findViewById(R.id.editItemsButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
        Intent i = new Intent(EditGameItemsActivity.this, MainMenuActivity.class);
        EditGameItemsActivity.this.startActivity(i);
      }
    });
  }    

}




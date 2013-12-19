package com.example.scavengerhuntapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ViewMyGamesActivity extends Activity {  
  private EditText userInput;
  private Button backButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.viewmygames);
    listMyGames();
    setupButtonCallbacks();
  }
  
  private void listMyGames(){
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
    final Intent i = getIntent(); 
    final Context context = this;
    //below is not good yet- just copied over from GameItemsActivity
    //query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
    //  public void done(ParseObject gameInfo, ParseException e) {
    //    if (e == null) {
    //      JSONArray items = gameInfo.getJSONArray("itemsList"); 
    //      if (items != null) {        
            //Now have to convert JSONArray 'items' to String Array 'itemsList' so that ArrayAdapter will accept it as argument
    //        List<String> itemsList = new ArrayList<String>();
    //        for(int i = 0; i < items.length(); i++){
    //          try{             
    //            itemsList.add(items.getString(i));
    //          }
    //          catch (Exception exc) {
    //            Log.d("ScavengerHuntApp", "JSONObject exception: " + Log.getStackTraceString(exc));
    //          }
    //        }
            //below is good, except that myGamesList isn't filled yet. Above is not good- just copied over from GameItemsActivity
    //        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, myGamesList); 
    //        ListView listView = (ListView) findViewById(R.id.myGamesListView);
    //        listView.setAdapter(adapter);  
    //      }
    //    }
    //    else {
    //      CharSequence text = "Sorry, there was a problem. Just a sec.";
    //      int duration = Toast.LENGTH_SHORT;                     
    //      Toast.makeText(context, text, duration).show();
    //      finish();
    //      startActivity(getIntent()); 
    //    }
    //  }
    //});  
  }
  
  private void setupButtonCallbacks() { 
    backButton = (Button) findViewById(R.id.viewMyGamesButton_back); 
    backButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
        Intent i = new Intent(ViewMyGamesActivity.this, MainMenuActivity.class);
        ViewMyGamesActivity.this.startActivity(i);
      }
    });
  }    

}




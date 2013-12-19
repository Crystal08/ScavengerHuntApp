package com.example.scavengerhuntapp;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewGameActivity extends Activity {  
  private EditText userInputGameName;
  private Button newGameButton, cancelButton;
  
  private Button setStartDateButton, setStartTimeButton;
  private Button setEndDateButton, setEndTimeButton;
  static final int STARTDATE_DIALOG_ID = 0;
  static final int STARTTIME_DIALOG_ID = 1;
  static final int ENDDATE_DIALOG_ID = 0;
  static final int ENDTIME_DIALOG_ID = 1;
  //variables to save user-selected date and time
  public int year, month, day, hour, minute;
  private int mYear, mMonth, mDay, mHour, mMinute;
  
  //assign current Date and Time values to variables
  public NewGameActivity(){
  final Calendar c = Calendar.getInstance();
  mYear = c.get(Calendar.YEAR);
  mMonth = c.get(Calendar.MONTH);
  mDay = c.get(Calendar.DAY_OF_MONTH);
  mHour = c.get(Calendar.HOUR_OF_DAY);
  mMinute = c.get(Calendar.MINUTE);
  } 
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.newgamecreate);
    
    //get the references of date-and-time-set buttons
    setStartDateButton = (Button)findViewById(R.id.newGameButton_setStartDate);
    setStartTimeButton = (Button)findViewById(R.id.newGameButton_setStartTime);
    setEndDateButton = (Button)findViewById(R.id.newGameButton_setEndDate);
    setEndTimeButton = (Button)findViewById(R.id.newGameButton_setEndTime);
    
    //Set ClickListener on setStartDateButton
    setStartDateButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
      //Show the DatePicker Dialog
        showDialog(STARTDATE_DIALOG_ID);
      }
    });
    //Set ClickListener on setStartTimeButton
    setStartTimeButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
      //Show the TimePicker Dialog
        showDialog(STARTTIME_DIALOG_ID);
      }
    });
    //Set ClickListener on setEndDateButton
    setEndDateButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
      //Show the DatePicker Dialog
        showDialog(ENDDATE_DIALOG_ID);
      }
    });
    //Set ClickListener on setEndTimeButton
    setEndTimeButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
      //Show the TimePicker Dialog
        showDialog(ENDTIME_DIALOG_ID);
      }
    });
    //call to code for creating new game or selecting back button 
    setupButtonCallbacks();
  }
  
  //register DatePickerDialog listener
  private DatePickerDialog.OnDateSetListener mDateSetListener =
      new DatePickerDialog.OnDateSetListener(){
      //the callback received when the user "sets" the Date in the DatePickerDialog
      // I'll want to add to this- probably to set variable to pass to Parse?
        public void onDateSet(DatePicker view, int yearSelected, 
          int monthOfYear, int dayOfMonth) {
        year = yearSelected;
        month = monthOfYear+1; //adjust start point of picker for month
        day = dayOfMonth;
        //set the selected date in the Select date Button
        setStartDateButton.setText("Date selected: "+day+"-"+month+"-"+year);
      }
  };
  // register  TimePickerDialog listener                
  private TimePickerDialog.OnTimeSetListener mTimeSetListener =
      new TimePickerDialog.OnTimeSetListener() {
      // the callback received when the user "sets" the TimePickerDialog in the dialog
      // I'll want to add to this- probably to set variable to pass to Parse?
         public void onTimeSet(TimePicker view, int hourOfDay, 
              int min) {
              hour = hourOfDay;
              minute = min;
              final String selected_time = String.format("%02d:%02d", hour, minute);
              // Set the Selected Date in Select date Button
              setStartTimeButton.setText("Time selected: "+selected_time);
          }
  };
  
  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
    case STARTDATE_DIALOG_ID:
// create a new DatePickerDialog with values you want to show
        return new DatePickerDialog(this,
                    mDateSetListener,
                    mYear, mMonth, mDay);
// create a new TimePickerDialog with values you want to show
    case STARTTIME_DIALOG_ID:
        return new TimePickerDialog(this,
                mTimeSetListener, mHour, mMinute, false);   
    }
    return null;
  }
  
  private void setupButtonCallbacks() {
    
    //user input for game name
    userInputGameName = (EditText) findViewById(R.id.newGame_name);
      
    //create new game, then go to GameItemsActivity to choose items
    newGameButton = (Button) findViewById(R.id.newGameButton_continue);
    newGameButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        final ParseObject gameInfo = new ParseObject("gameInfo");
        final String gameName = userInputGameName.getText().toString().trim(); 
        
        gameInfo.put("gameOwner", ParseUser.getCurrentUser());
        gameInfo.put("gameName", gameName);
        
        gameInfo.saveInBackground(
            new SaveCallback() {
              public void done(final ParseException e) {
                if (e == null) {           
                  final String gameInfoId = gameInfo.getObjectId();
                  Intent i = new Intent(NewGameActivity.this, GameItemsActivity.class);
                  i.putExtra("gameInfoId", gameInfoId);
                  NewGameActivity.this.startActivity(i);
                }
                else{
                  Context context = getApplicationContext();
                  CharSequence text = "Sorry, app has encountered a problem.";
                  int duration = Toast.LENGTH_SHORT;                     
                  Toast.makeText(context, text, duration).show();
                  Log.d("ScavengerHuntApp", Log.getStackTraceString(e));
                  finish();
                  startActivity(getIntent());
                }
              }  
            });      
      }
    });
    
    //cancel and return to MainMenu
    cancelButton = (Button) findViewById(R.id.newGameButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
        Intent i = new Intent(NewGameActivity.this, MainMenuActivity.class);
        NewGameActivity.this.startActivity(i);
      }
    });
  } 

} 


  





package com.example.scavengerhuntapp;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
  private ParseObject gameInfo;
  private Button newGameButton, cancelButton;
  private Button setStartDateButton, setStartTimeButton;
  private Button setEndDateButton, setEndTimeButton;
  
  private Date startDateTime, endDateTime;
  static final int STARTDATE_DIALOG_ID = 0;
  static final int STARTTIME_DIALOG_ID = 1;
  static final int ENDDATE_DIALOG_ID = 2;
  static final int ENDTIME_DIALOG_ID = 3;
  int target_dialog_id = -1;
  //variables to save user-selected date and time
  public int year, month, day, hour, minute;
  public int startYear, startMonth, startDay, startHour, startMinute;
  public int endYear, endMonth, endDay, endHour, endMinute;
  //variables for current date and time
  private int mYear, mMonth, mDay, mHour, mMinute;
  //get new game, and assign current Date and Time values for default date/time picker displays
  public NewGameActivity(){
  gameInfo = new ParseObject("gameInfo");  
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
    
    //set up buttons to launch date and time pickers:
    
    setStartDateButton = (Button)findViewById(R.id.newGameButton_setStartDate);
    setStartTimeButton = (Button)findViewById(R.id.newGameButton_setStartTime);
    setEndDateButton = (Button)findViewById(R.id.newGameButton_setEndDate);
    setEndTimeButton = (Button)findViewById(R.id.newGameButton_setEndTime);
    
    //Set listeners on buttons to launch date and time pickers:
    
    setStartDateButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
        showDialog(STARTDATE_DIALOG_ID);
      }
    });
    setStartTimeButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
        showDialog(STARTTIME_DIALOG_ID);
      }
    });
    setEndDateButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
        showDialog(ENDDATE_DIALOG_ID);
      }
    });
    setEndTimeButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
        showDialog(ENDTIME_DIALOG_ID);
      }
    });
    
    //set up buttons to create new game or to go back 
    setupButtonCallbacks();
  }
  
  //methods to support code in onCreate:
  
  //register DatePickerDialog listener
  public DatePickerDialog.OnDateSetListener mDateSetListener =
      new DatePickerDialog.OnDateSetListener(){
      //the callback received when the user "sets" the Date in the DatePickerDialog
        public void onDateSet(DatePicker view, int yearSelected, 
          int monthOfYear, int dayOfMonth) {
        day = dayOfMonth;
        month = monthOfYear;
        final int display_month = month + 1; //java uses 0-based month system
        year = yearSelected;
        switch(target_dialog_id) {
        case STARTDATE_DIALOG_ID:
          //display the selected date on the start date button
          setStartDateButton.setText("Date selected: "+day+"-"+display_month+"-"+year);
          //fill startYear, startMonth, startDay instance variables with user-selected values
          setStartDate(year, month, day); 
          break;
        case ENDDATE_DIALOG_ID:
          //display the selected date in the end date button
          setEndDateButton.setText("Date selected: "+day+"-"+display_month+"-"+year);
          //fill endYear, endMonth, endDay instance variables with user-selected values
          setEndDate(year, month, day);
          break;
        default:
          Context context = getApplicationContext();
          CharSequence text = "Sorry, there was a problem saving the date.";
          int duration = Toast.LENGTH_SHORT;                     
          Toast.makeText(context, text, duration).show();
        }
      }
  };
  // register  TimePickerDialog listener                
  private TimePickerDialog.OnTimeSetListener mTimeSetListener =
      new TimePickerDialog.OnTimeSetListener() {
      // the callback received when the user "sets" the Time in the TimePickerDialog
         public void onTimeSet(TimePicker view, int hourOfDay, 
              int min) {
              hour = hourOfDay;
              minute = min;
              String selected_time = String.format(Locale.US, "%02d:%02d", hour, minute);
              switch(target_dialog_id) {
              case STARTTIME_DIALOG_ID:
                //display the selected time on the start time button
                setStartTimeButton.setText("Time selected: "+selected_time);
                //fill startHour, startMinute instance variables with user-selected values
                setStartTime(hour, minute);
                break;
              case ENDTIME_DIALOG_ID:
                //display the selected time on the end time button
                setEndTimeButton.setText("Time selected: "+selected_time);
                //fill endHour, endMinute instance variables with user-selected values
                setEndTime(hour, minute);
                break;
              default:
                Context context = getApplicationContext();
                CharSequence text = "Sorry, there was a problem saving the time.";
                int duration = Toast.LENGTH_SHORT;                     
                Toast.makeText(context, text, duration).show();
              }
          }
  };
  
  @Override
  protected Dialog onCreateDialog(int id) {
    target_dialog_id = id;
    switch (id) {
    case STARTDATE_DIALOG_ID:
    case ENDDATE_DIALOG_ID:
    // create a new DatePickerDialog with default values displayed
        return new DatePickerDialog(this,
                    mDateSetListener,
                    mYear, mMonth, mDay);
    // create a new TimePickerDialog with default values displayed
    case STARTTIME_DIALOG_ID:
    case ENDTIME_DIALOG_ID:
        return new TimePickerDialog(this,
                mTimeSetListener, mHour, mMinute, false);   
    }
    return null;
  }
  
  private void setStartDate(int year, int month, int day){
    startYear = year;
    startMonth = month;
    startDay = day;
  };
  
  private void setEndDate(int year, int month, int day){
    endYear = year;
    endMonth = month;
    endDay = day;
  };
  
  private void setStartTime(int hour, int minute){
    startHour = hour;
    startMinute = minute;
  };
  
  private void setEndTime(int hour, int minute){
    endHour = hour;
    endMinute = minute;
  };
  
  //compile start and end Date objects with selected start and end datetimes
  private Date returnStartDateTime(){
    Calendar c1 = Calendar.getInstance();
    //fill Calendar instance with user-inputted values, then set
    c1.set(startYear, startMonth, startDay, startHour, startMinute);
    startDateTime = c1.getTime();
    return startDateTime;
  };
  
  private Date returnEndDateTime(){
    Calendar c1 = Calendar.getInstance();
    //fill Calendar instance with user-inputted values, then set
    c1.set(endYear, endMonth, endDay, endHour, endMinute);
    endDateTime = c1.getTime();
    return endDateTime;
  };
    
  private void setupButtonCallbacks() {
    
    //user input for game name
    userInputGameName = (EditText) findViewById(R.id.newGame_name);
      
    //put owner, name, and start/end datetimes to game, then go to GameItemsActivity to choose items
    newGameButton = (Button) findViewById(R.id.newGameButton_continue);
    newGameButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) { 
        gameInfo.put("gameOwner", ParseUser.getCurrentUser().getObjectId());
        gameInfo.put("gameName", userInputGameName.getText().toString().trim());
        gameInfo.put("gameStartDateTime", returnStartDateTime());
        gameInfo.put("gameEndDateTime", returnEndDateTime());
        gameInfo.saveInBackground(
            new SaveCallback() {
              public void done(final ParseException e) {
                if (e == null) {   
                  Log.d("ScavengerHuntApp", startDateTime.toString()); 
                  final String gameInfoId = gameInfo.getObjectId();
                  final Intent i = new Intent(NewGameActivity.this, GameItemsActivity.class);
                  i.putExtra("gameInfoId", gameInfoId);
                  NewGameActivity.this.startActivity(i);
                }
                else{
                  Context context = getApplicationContext();
                  CharSequence text = "Sorry, app has encountered a problem.";
                  final int duration = Toast.LENGTH_SHORT;                     
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


  





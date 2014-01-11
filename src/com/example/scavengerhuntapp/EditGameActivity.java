package com.example.scavengerhuntapp;

import java.text.SimpleDateFormat;
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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class EditGameActivity extends Activity {  
  private EditText userInputGameName;
  private ParseObject gameInfo;
  private Button editGameButton, cancelButton;
  private Button editStartDateButton, editStartTimeButton;
  private Button editEndDateButton, editEndTimeButton;
  
  private Date startDateTime, endDateTime;
  static final int STARTDATE_DIALOG_ID = 0;
  static final int STARTTIME_DIALOG_ID = 1;
  static final int ENDDATE_DIALOG_ID = 2;
  static final int ENDTIME_DIALOG_ID = 3;
  int target_dialog_id = -1;
  //variables for date and time from current game
  private int sYear, sMonth, sDay, eYear, eMonth, eDay;
  private int sHour, sMinute, eHour, eMinute;
  //variables for date and time for edited game
  public int year, month, day, hour, minute;
  public int editstartYear, editstartMonth, editstartDay, editstartHour, editstartMinute;
  public int editendYear, editendMonth, editendDay, editendHour, editendMinute;
  //variables for date and time for datepicker default display
  //private int mYear, mMonth, mDay, mHour, mMinute;
  //assign datepicker default date and time to now
  //public EditGameActivity(){ 
  //  final Calendar c = Calendar.getInstance();
  //  mYear = c.get(Calendar.YEAR);
  //  mMonth = c.get(Calendar.MONTH);
  //  mDay = c.get(Calendar.DAY_OF_MONTH);
  //  mHour = c.get(Calendar.HOUR_OF_DAY);
  //  mMinute = c.get(Calendar.MINUTE);
  //} 
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.editgame);
    getGame();
    
    //set up buttons to launch date and time pickers:
    
    editStartDateButton = (Button)findViewById(R.id.editGameButton_setStartDate);
    editStartTimeButton = (Button)findViewById(R.id.editGameButton_setStartTime);
    editEndDateButton = (Button)findViewById(R.id.editGameButton_setEndDate);
    editEndTimeButton = (Button)findViewById(R.id.editGameButton_setEndTime);
    
    //Set listeners on buttons to launch date and time pickers:
    
    editStartDateButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
        showDialog(STARTDATE_DIALOG_ID);
      }
    });
    editStartTimeButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
        showDialog(STARTTIME_DIALOG_ID);
      }
    });
    editEndDateButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
        showDialog(ENDDATE_DIALOG_ID);
      }
    });
    editEndTimeButton.setOnClickListener(new View.OnClickListener(){
      public void onClick(View v){
        showDialog(ENDTIME_DIALOG_ID);
      }
    });
    
    //set up buttons to continue game edit or to go back 
    setupButtonCallbacks();
  }
  
  //methods to support code in onCreate:
  
  private void getGame(){
    final ParseQuery<ParseObject> query = ParseQuery.getQuery("gameInfo");
    final Intent i = getIntent();    
    query.getInBackground(i.getStringExtra("gameInfoId"), new GetCallback<ParseObject>() {
      public void done(ParseObject game, ParseException e) {
        if (e == null) {
          EditText gameName = (EditText) findViewById(R.id.editGame_name);
          gameName.setText(game.getString("gameName"));
          
          SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");
          SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
         
          String startdate = sdf_date.format(game.getDate("gameStartDateTime"));
          String starttime = sdf_time.format(game.getDate("gameStartDateTime"));
          String enddate = sdf_date.format(game.getDate("gameEndDateTime"));
          String endtime = sdf_time.format(game.getDate("gameEndDateTime"));
          
          editStartDateButton.setText("Change start date? ("+startdate+")");
          editStartTimeButton.setText("Change start time? ("+starttime+")");
          editEndDateButton.setText("Change end date? ("+enddate+")");
          editEndTimeButton.setText("Change end time? ("+endtime+")");
          
          gameInfo = game; 
        }
        else {
          Log.d("ScavengerHuntApp","Problem retrieving game.");
        }
      }
    });    
  }
  
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
          editStartDateButton.setText("Date selected: "+day+"-"+display_month+"-"+year);
          //fill editstartYear, editstartMonth, startDay instance variables with user-selected values
          setStartDate(year, month, day); 
          break;
        case ENDDATE_DIALOG_ID:
          //display the selected date in the end date button
          editEndDateButton.setText("Date selected: "+day+"-"+display_month+"-"+year);
          //fill editendYear, editendMonth, editendDay instance variables with user-selected values
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
                editStartTimeButton.setText("Time selected: "+selected_time);
                //fill editstartHour, editstartMinute instance variables with user-selected values
                setStartTime(hour, minute);
                break;
              case ENDTIME_DIALOG_ID:
                //display the selected time on the end time button
                editEndTimeButton.setText("Time selected: "+selected_time);
                //fill editendHour, editendMinute instance variables with user-selected values
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
    // create a new DatePickerDialog for Start Date with current values displayed
      SimpleDateFormat sdf_syear = new SimpleDateFormat("yyyy");
      SimpleDateFormat sdf_smonth = new SimpleDateFormat("MM");
      SimpleDateFormat sdf_sday = new SimpleDateFormat("dd");
     
      String syear = sdf_syear.format(gameInfo.getDate("gameStartDateTime"));
      String smonth = sdf_smonth.format(gameInfo.getDate("gameStartDateTime"));
      String sday = sdf_sday.format(gameInfo.getDate("gameStartDateTime"));
      
      sYear = Integer.valueOf(syear);
      sMonth = Integer.valueOf(smonth) - 1; //java uses 0-based month system
      sDay = Integer.valueOf(sday);
      
       return new DatePickerDialog(this,
                    mDateSetListener,
                    sYear, sMonth, sDay);    
    case ENDDATE_DIALOG_ID:
    // create a new DatePickerDialog for End Date with current values displayed
      SimpleDateFormat sdf_eyear = new SimpleDateFormat("yyyy");
      SimpleDateFormat sdf_emonth = new SimpleDateFormat("MM");
      SimpleDateFormat sdf_eday = new SimpleDateFormat("dd");
     
      String eyear = sdf_eyear.format(gameInfo.getDate("gameEndDateTime"));
      String emonth = sdf_emonth.format(gameInfo.getDate("gameEndDateTime"));
      String eday = sdf_eday.format(gameInfo.getDate("gameEndDateTime"));
      
      eYear = Integer.valueOf(eyear);
      eMonth = Integer.valueOf(emonth) - 1; //java uses 0-based month system
      eDay = Integer.valueOf(eday);
      
      return new DatePickerDialog(this,
                    mDateSetListener,
                    eYear, eMonth, eDay);
    
    case STARTTIME_DIALOG_ID:
    // create a new TimePickerDialog for Start Time with current values displayed  
      SimpleDateFormat sdf_shour = new SimpleDateFormat("HH");
      SimpleDateFormat sdf_sminute = new SimpleDateFormat("mm");
     
      String shour = sdf_shour.format(gameInfo.getDate("gameStartDateTime"));
      String sminute = sdf_sminute.format(gameInfo.getDate("gameStartDateTime"));
      
      sHour = Integer.valueOf(shour);
      sMinute = Integer.valueOf(sminute);
      
      return new TimePickerDialog(this,
          mTimeSetListener, sHour, sMinute, false); 
      
    case ENDTIME_DIALOG_ID:
    // create a new TimePickerDialog for End Time with current values displayed  
      SimpleDateFormat sdf_ehour = new SimpleDateFormat("HH");
      SimpleDateFormat sdf_eminute = new SimpleDateFormat("mm");
     
      String ehour = sdf_ehour.format(gameInfo.getDate("gameEndDateTime"));
      String eminute = sdf_eminute.format(gameInfo.getDate("gameEndDateTime"));
      
      eHour = Integer.valueOf(ehour);
      eMinute = Integer.valueOf(eminute);
        
        return new TimePickerDialog(this,
                mTimeSetListener, eHour, eMinute, false);   
    }
    return null;
  }
  
  private void setStartDate(int year, int month, int day){
    editstartYear = year;
    editstartMonth = month;
    editstartDay = day;
  };
  
  private void setEndDate(int year, int month, int day){
    editendYear = year;
    editendMonth = month;
    editendDay = day;
  };
  
  private void setStartTime(int hour, int minute){
    editstartHour = hour;
    editstartMinute = minute;
  };
  
  private void setEndTime(int hour, int minute){
    editendHour = hour;
    editendMinute = minute;
  };
  
  //compile start and end Date objects with selected start and end datetimes
  private Date returnStartDateTime(){
    Calendar c1 = Calendar.getInstance();
    //fill Calendar instance with user-inputted values, then set
    c1.set(editstartYear, editstartMonth, editstartDay, editstartHour, editstartMinute);
    startDateTime = c1.getTime();
    return startDateTime;
  };
  
  private Date returnEndDateTime(){
    Calendar c1 = Calendar.getInstance();
    //fill Calendar instance with user-inputted values, then set
    c1.set(editendYear, editendMonth, editendDay, editendHour, editendMinute);
    endDateTime = c1.getTime();
    return endDateTime;
  };
    
  private void setupButtonCallbacks() {
    
    //user input for game name
    userInputGameName = (EditText) findViewById(R.id.editGame_name);
      
    //put name, and start/end datetimes to game, then continue to EditGameItemsActivity to edit items
    editGameButton = (Button) findViewById(R.id.editGameButton_continue);
    editGameButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) { 
        gameInfo.put("gameName", userInputGameName.getText().toString().trim());
        gameInfo.put("gameStartDateTime", returnStartDateTime());
        gameInfo.put("gameEndDateTime", returnEndDateTime());
        gameInfo.saveInBackground(
            new SaveCallback() {
              public void done(final ParseException e) {
                if (e == null) {   
                  final String gameInfoId = gameInfo.getObjectId();
                  Intent i = new Intent(EditGameActivity.this, EditGameItemsActivity.class);
                  i.putExtra("gameInfoId", gameInfoId);
                  EditGameActivity.this.startActivity(i);
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
    cancelButton = (Button) findViewById(R.id.editGameButton_cancel); 
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
        Intent i = new Intent(EditGameActivity.this, MainMenuActivity.class);
        EditGameActivity.this.startActivity(i);
      }
    });
  } 

} 
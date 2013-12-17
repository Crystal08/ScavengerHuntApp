package com.example.scavengerhuntapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.content.Intent;

import com.parse.ParseUser;

public class MainMenuActivity extends Activity {
  private Button newGameButton;
  private Button joinGameButton;
  private Button myGamesButton;
  @SuppressWarnings("unused")
  // This member will be used for actual game play, which is why it's
  // but since no game play code exists yet, it's unused in this activity
  private ParseUser currentUser;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.mainmenu);
    setupButtonCallbacks();
  }

  public void onResume() {
    super.onResume();
    currentUser = ParseUser.getCurrentUser();
  }

  /**
   * Setup the Screen callbacks
   */
  private void setupButtonCallbacks() {
    newGameButton = (Button) findViewById(R.id.newGameButton_name);
    newGameButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // XXX open NewGameActivity
        // Intent i = new Intent(mThisActivity, NewGameActivity.class);
        Intent i = new Intent(MainMenuActivity.this, NewGameActivity.class);
        MainMenuActivity.this.startActivity(i);
      }
    });

    joinGameButton = (Button) findViewById(R.id.mainMenuButton_joinGame);
    joinGameButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // XXX open JoinGameActivity
        // Intent i = new Intent(mThisActivity, JoinGameActivity.class);
        // mThisActivity.startActivity(i);
      }
    });

    myGamesButton = (Button) findViewById(R.id.mainMenuButton_myGames);
    myGamesButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // XXX open MyGamesActivity
        // Intent i = new Intent(mThisActivity, MyGamesActivity.class);
        // mThisActivity.startActivity(i);
      }
    });

  }

  // /////////////////////////////////////////////////////
  // Menu Handler
  // /////////////////////////////////////////////////////

  /**
   * The create options menu event listener. Invoked at the time to create the
   * menu.
   * 
   * @param the
   *            menu to create
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.mainmenu, menu);
    return true;
  }

  /**
   * The options item selected event listener. Invoked when a menu item has
   * been selected.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.menuitem_prefs:
      // Intent i = new Intent(mThisActivity, PrefsActivity.class);
      // mThisActivity.startActivity(i);
      return true;
    case R.id.menuitem_logout:
      ParseUser.logOut();
      finish();
      return true;
    default:
      break;
    }
    return false;
  }

}

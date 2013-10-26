package com.example.scavengerhuntapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well...and then does login or signup using Parse.com API
 */
public class LoginActivity extends Activity {
  private static final String TAG = "LoginActivity";

  private Button continueButton;
  private Button cancelButton;
  private EditText usernameEditText;
  private EditText passwordEditText;
  private EditText emailEditText;

  private String username;
  private String password;
  private String email;

  private ProgressDialog progressDialog;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // The user interface/content view is defined
    // in /res/layout/login.xml
    setContentView(R.layout.login);
    setupButtonCallbacks();
  }

  @Override
  public void onResume() {
    super.onResume();
    // Get currentUser from ParseUser
    ParseUser currentUser = ParseUser.getCurrentUser();
    // If the currentUser is not null...and the
    // currentUser.getObject() is not null (getObjectId is
    // null when anonymous user is logged in), then we
    // are already logged in, and we simply proceed to the Main Menu
    // activity. Otherwise (not logged in or anonymous
    // user only), then the LoginActivity (this) is shown, and
    // users are requested to enter in a username, password,
    // and email (see /res/layout/login.xml for user
    // user interface)
    if (currentUser != null && currentUser.getObjectId() != null) {
      username = currentUser.getUsername();
      usernameEditText.setText(username);
      email = currentUser.getEmail();
      emailEditText.setText(email);
      // If active currentUser, no need to login, go to main menu
      startActivity(new Intent(this, MainMenuActivity.class));
      finish();
    }
    // Otherwise, we let the user fill out username, password
    // and press 'Continue' button. See setupButtonCallbacks
  }

  private void dismissProgressDialog() {
    if (progressDialog != null) {
      progressDialog.dismiss();
      progressDialog = null;
    }
  }

  private final FindCallback<ParseUser> userFindCallback = new FindCallback<ParseUser>() {
    @Override
    public void done(List<ParseUser> arg0, ParseException arg1) {
      // First, get rid of progress dialog
      // if it's still being displayed
      dismissProgressDialog();
      // No exception means query succeeded
      if (arg1 == null) {
        // If the arg0 list has more than one entry
        if (arg0 != null && arg0.size() > 0) {
          // Get the first one (should be only one)
          ParseUser user = (ParseUser) arg0.get(0);
          // Get the user name (should match username searched for)
          // If the user is found then login, if not, then sign up
          if (username != null) {
            String existingUsername = user.getUsername();
            if (!username.equals(existingUsername)) {
              // clear the username field
              usernameEditText.setText("");
              usernameEditText.requestFocus();
              // set the username to null
              username = null;
              // Tell user to choose new username
              showToast(getString(R.string.label_loginUsernameAlreadyExists));
              return;
            }
          }
          if (email != null) {
            String existingEmail = user.getEmail();
            if (!email.equals(existingEmail)) {
              // clear the email field
              emailEditText.setText("");
              emailEditText.requestFocus();
              // set the email to null
              email = null;
              // Tell user to choose new email
              showToast(getString(R.string.label_loginEmailAlreadyExists));
              return;
            }
          }
          // if we get here, the username and email both match, which
          // means we login
          doLogin();
        } else
          doSignUp();
        // If no users found with this username, then signup
        // a new user
        // Some problem with query
      } else
        showToast(getString(R.string.label_signupErrorMessage) + " "
            + getString(R.string.label_loginPleaseTryAgainMessage));
    }
  };

  private void showToast(String message) {
    ScavengerHuntApplication.getInstance().showToast(LoginActivity.this,
        message);
  }

  /**
   * Method to setup the UI button callbacks
   */
  private void setupButtonCallbacks() {
    usernameEditText = (EditText) findViewById(R.id.textbox_loginUsername);
    passwordEditText = (EditText) findViewById(R.id.textbox_loginPassword);
    emailEditText = (EditText) findViewById(R.id.textbox_loginEmail);
    // Login button handler
    continueButton = (Button) findViewById(R.id.loginbutton_continue);
    // When continue is clicked...
    continueButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // Get the values of the username, password, and email
        // fields to the username, password, and email member
        // variables
        username = usernameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        email = emailEditText.getText().toString();
        // validate that the username is not null and has length
        // > 0. If it is null or empty, send toast message
        // to user interface and return
        if (username == null || username.length() == 0) {
          showToast(getString(R.string.hint_username));
          return;
        }
        // validate that the username is not null and has length
        // > 0. If it is null or empty, send toast message
        // to user interface and return
        if (password == null || password.length() == 0) {
          showToast(getString(R.string.hint_password));
          return;
        }
        // Query with username in order to see if we need to
        // simply login, or sign up a new user account
        progressDialog = ProgressDialog.show(LoginActivity.this,
            getString(R.string.label_login_please_wait),
            getString(R.string.label_query_in_progress) + " '"
                + username + "'");

        List<ParseQuery<ParseUser>> parseUserQueryList = new ArrayList<ParseQuery<ParseUser>>();

        ParseQuery<ParseUser> parseUsernameQuery = ParseUser.getQuery();
        // We are looking for users that have same username
        parseUsernameQuery.whereEqualTo("username", username);
        parseUserQueryList.add(parseUsernameQuery);
        ParseQuery<ParseUser> parseEmailQuery = ParseUser.getQuery();
        parseEmailQuery.whereEqualTo("email", email);
        parseUserQueryList.add(parseEmailQuery);
        // or the username and email queries
        ParseQuery<ParseUser> parseUserQuery = ParseQuery.or(parseUserQueryList);
        // Do the query in background, and callback is called
        // when complete. See userFindCallback
        parseUserQuery.findInBackground(userFindCallback);
      }
    });

    // Cancel button handler
    cancelButton = (Button) findViewById(R.id.loginbutton_cancel);
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // Logout with ParseUser.logOut()
        ParseUser.logOut();
        finish();
      }
    });
  }

  // create sign-in callback. The done method will be called
  // after the signUpInBackground has completed (successfully
  // or not
  private final SignUpCallback signinCallback = new SignUpCallback() {
    @Override
    public void done(ParseException arg0) {
      // First, get rid of progress dialog
      // if it's still being displayed
      dismissProgressDialog();
      // Then if no exception it was successful, and we're
      // logged in and go to/display MainMenuActivity
      if (arg0 == null) {
        Log.d(TAG + ".doSignUp",
            "Success!  User account created for username="
                + LoginActivity.this.username);
        startActivity(new Intent(LoginActivity.this,
            MainMenuActivity.class));
        finish();
      } else {
        // Show Error message
        showToast(getString(R.string.label_signupErrorMessage) + " "
            + getString(R.string.label_loginPleaseTryAgainMessage));
      }
    }
  };

  private void doSignUp() {
    // do sign up
    // first show progress dialog
    progressDialog = ProgressDialog.show(LoginActivity.this,
        getString(R.string.label_login_please_wait),
        getString(R.string.label_signup_in_progress));
    // to create new ParseUser, and
    // set username password and email
    ParseUser user = new ParseUser();
    user.setUsername(username);
    user.setPassword(password);
    user.setEmail(email);
    // Actually do the sign in...in background, with
    // signinCallback called when complete
    user.signUpInBackground(signinCallback);
  }

  private final LogInCallback loginCallback = new LogInCallback() {
    @Override
    public void done(ParseUser arg0, ParseException arg1) {
      // First, get rid of progress dialog
      // if it's still being displayed
      dismissProgressDialog();
      // Then if no exception it was successful, and we're
      // logged in and go to/display MainMenuActivity
      if (arg0 != null) {
        Log.d(TAG + ".doParseLogin",
            "Success!  Current User ObjectId: "
                + arg0.getObjectId());
        startActivity(new Intent(LoginActivity.this,
            MainMenuActivity.class));
        finish();
      } else {
        // Notify user that login failed and ask to try again
        Log.d(TAG + ".doParseLogin", "Failed", arg1);
        showToast(getString(R.string.label_loginErrorMessage) + " "
            + arg1.getMessage() + ".  "
            + getString(R.string.label_loginPleaseTryAgainMessage));
      }
    }
  };

  private void doLogin() {
    // Do login to parse.com (Remember: with given client id and
    // app id)
    progressDialog = ProgressDialog.show(LoginActivity.this,
        getString(R.string.label_login_please_wait),
        getString(R.string.label_login_in_progress) + " '" + username
            + "'");

    // Login with username and password given loginCallback
    // that will be notified when login completed or failed
    ParseUser.logInInBackground(username, password, loginCallback);
  }

}
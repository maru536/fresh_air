package com.perfect.freshair.View;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.perfect.freshair.API.GPSServerInterface;
import com.perfect.freshair.Callback.ResponseCallback;
import com.perfect.freshair.Common.PermissionEnumeration;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.PreferencesUtils;

/**
 * A login screen that offers login via id/password.
 */
public class SignInActivity extends AppCompatActivity {
    private AutoCompleteTextView mIdView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mFailText;
    Button mIdSignInButton;
    Button mIdSignUpButton;

    private String mId;
    private String mPasswd;
    private GPSServerInterface mServerInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        //permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PermissionEnumeration.MY_ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PermissionEnumeration.MY_ACCESS_FINE_LOCATION);
        }

        if (!PreferencesUtils.getUser(this).isEmpty()) {
            startActivity(new Intent(SignInActivity.this.getApplicationContext(), MainActivity.class));
        }
        // Set up the login form.
        mIdView = findViewById(R.id.id);
        mPasswordView = findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mFailText = findViewById(R.id.fail_text);
        mIdSignInButton = findViewById(R.id.sign_in_button);
        mIdSignUpButton = findViewById(R.id.sign_up_button);


        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptSignIn();
                    return true;
                }
                return false;
            }
        });

        mIdSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignIn();
            }
        });

        mIdSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this.getApplicationContext(), SignUpActivity.class));
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid id, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignIn() {
        // Reset errors.
        mIdView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mId = mIdView.getText().toString();
        mPasswd = mPasswordView.getText().toString();

        boolean isInvalid = false;
        View focusView = null;

        if (!isIdValid(mId)) {
            mIdView.setError(getString(R.string.error_invalid_id));
            focusView = mIdView;
            isInvalid = true;
        }

        if (!isPasswordValid(mPasswd)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            isInvalid = true;
        }

        if (isInvalid)
            focusView.requestFocus();
        else {
            showProgress(true);

            if (mServerInterface == null)
                mServerInterface = new GPSServerInterface();

            //request Id
            mServerInterface.signIn(mId, mPasswd, new ResponseCallback() {
                @Override
                public void responseCallback(int _resultCode) {
                    if (_resultCode == 200) {
                        PreferencesUtils.saveUser(SignInActivity.this.getApplicationContext(), mId);
                        startActivity(new Intent(SignInActivity.this.getApplicationContext(), MainActivity.class));
                    } else
                        mFailText.setVisibility(TextView.VISIBLE);

                    showProgress(false);
                }
            });
        }
    }

    private boolean isIdValid(String _id) {
        //TODO: Replace this with your own logic
        return _id.length() > 4;
    }

    private boolean isPasswordValid(String _password) {
        //TODO: Replace this with your own logic
        return _password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermissionEnumeration.MY_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this, "sorry, this app requires bluetooth feature.", Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PermissionEnumeration.MY_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this, "sorry, this app requires bluetooth feature.", Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}


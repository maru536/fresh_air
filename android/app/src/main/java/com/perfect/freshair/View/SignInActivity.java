package com.perfect.freshair.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.perfect.freshair.API.DustServerInterface;
import com.perfect.freshair.Callback.ResponseCallback;
import com.perfect.freshair.R;
import com.perfect.freshair.Utils.PreferencesUtils;

/**
 * A login screen that offers login via id/password.
 */
public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    private AutoCompleteTextView mIdView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextView mFailText;
    Button mIdSignInButton;
    Button mIdSignUpButton;
    SignInButton googleSignInButton;
    public static SignInActivity activity = null;
    private GoogleApiClient googleApiClient;
    private String mUserId;
    private String mPasswd;
    private DustServerInterface mServerInterface;
    private static final int GOOGLE_SIGN_IN_REQUEST_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        String userId;
        try {
            userId = getIntent().getData().getQueryParameter("userId");
        } catch (NullPointerException npe) {
            userId = "";
        }

        if (userId != null && userId.length() > 0) {
            PreferencesUtils.saveUser(this, userId);
            startMainActivity();
        }

        activity = this;

        if (!PreferencesUtils.getUser(this).isEmpty()) {
            startMainActivity();
        }

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e(TAG, connectionResult.getErrorMessage());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();


        // Set up the login form.
        mIdView = findViewById(R.id.id);
        mPasswordView = findViewById(R.id.password);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mFailText = findViewById(R.id.fail_text);
        mIdSignInButton = findViewById(R.id.sign_in_button);
        mIdSignUpButton = findViewById(R.id.sign_up_button);
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setScopes(googleSignInOptions.getScopeArray());
        googleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(googleSignInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);
            }
        });

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
        mUserId = mIdView.getText().toString();
        mPasswd = mPasswordView.getText().toString();

        boolean isInvalid = false;
        View focusView = null;

        if (!isIdValid(mUserId)) {
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
            successLogin(mUserId, mPasswd);
        }
    }

    private void successLogin(String userId, String passwd) {
        if (mServerInterface == null)
            mServerInterface = new DustServerInterface();

        mUserId = userId;
        //request Id
        mServerInterface.signIn(userId, passwd, new ResponseCallback() {
            @Override
            public void responseCallback(int _resultCode) {
                if (_resultCode == 200) {
                    PreferencesUtils.saveUser(SignInActivity.this.getApplicationContext(), mUserId);
                    startMainActivity();
                } else
                    mFailText.setVisibility(TextView.VISIBLE);

                showProgress(false);
            }
        });
    }

    private boolean isIdValid(String _id) {
        //TODO: Replace this with your own logic
        return _id.matches("^[a-z0-9]{5,20}$");
    }

    private boolean isPasswordValid(String _password) {
        //TODO: Replace this with your own logic
        return _password.matches("^[a-z0-9!@#$%^&*()-_+=|\\`~,./-]{6,20}$");
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

    private void startMainActivity() {
        startActivity(new Intent(SignInActivity.this.getApplicationContext(), MainActivity.class));
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (googleSignInResult.isSuccess()) {
                GoogleSignInAccount account = googleSignInResult.getSignInAccount();

                PreferencesUtils.saveUser(SignInActivity.this.getApplicationContext(), account.getEmail());
                startMainActivity();
            }
        }
    }
}


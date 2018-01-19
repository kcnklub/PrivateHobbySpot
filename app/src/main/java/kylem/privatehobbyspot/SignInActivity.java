package kylem.privatehobbyspot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.SyncCredentials;
import io.realm.SyncUser;
import kylem.privatehobbyspot.auth.facebook.FacebookAuth;
import kylem.privatehobbyspot.auth.google.GoogleAuth;

import static kylem.privatehobbyspot.PrivateHobbySpot.AUTH_URL;


public class SignInActivity extends AppCompatActivity implements SyncUser.Callback<SyncUser>{

    private static final String TAG = "SignInActivity";

    public static final String ACTION_IGNORE_CURRENT_USER = "action.ignoreCurrentUser";

    private AutoCompleteTextView usernameView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;
    private FacebookAuth facebookAuth;
    private GoogleAuth googleAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        usernameView = (AutoCompleteTextView) findViewById (R.id.username);
        passwordView = (EditText) findViewById (R.id.password);

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_NULL){
                    Log.d(TAG, "ATTEMPTING TO LOGIN WITH USERNAME & PASSWORD");
                    attemptLogin();
                    return true;
                }
                Log.d(TAG, "FAILED ATTEMPT TO LOGIN WITH USERNAME & PASSWORD");
                return false;
            }
        });

        final Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "ATTEMPTING TO LOGIN WITH USERNAME & PASSWORD");
                attemptLogin();
            }
        });

        final Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });

        loginFormView = findViewById(R.id.sign_in_form);
        progressView = findViewById(R.id.sign_in_progress);

        if(savedInstanceState == null){
            if(!ACTION_IGNORE_CURRENT_USER.equals(getIntent().getAction())){
                final SyncUser user = SyncUser.currentUser();
                if(user != null){
                    loginComplete(user);
                }
            }
        }

        facebookAuth = new FacebookAuth((LoginButton) findViewById(R.id.login_button)) {
            @Override
            public void onRegistrationComplete(LoginResult loginResult) {
                UserManager.setAuthMode(UserManager.AUTH_MODE.FACEBOOK);
                SyncCredentials credentials = SyncCredentials.facebook(loginResult.getAccessToken().getToken());
                SyncUser.loginAsync(credentials, AUTH_URL, SignInActivity.this);
            }
        };

        googleAuth = new GoogleAuth((SignInButton) findViewById(R.id.google_sign_in_button), this) {
            @Override
            public void onRegistrationComplete(GoogleSignInResult result) {
                UserManager.setAuthMode(UserManager.AUTH_MODE.GOOGLE);
                GoogleSignInAccount acct = result.getSignInAccount();
                SyncCredentials credentials = SyncCredentials.google(acct.getIdToken());
                SyncUser.loginAsync(credentials, AUTH_URL, SignInActivity.this);
            }

            @Override
            public void onError(String s){
                super.onError(s);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        googleAuth.onActivityResult(requestCode, resultCode, data);
        facebookAuth.onActivityResult(requestCode, resultCode, data);
    }

    private void loginComplete(SyncUser user){

        Log.d(TAG, "LOGIN COMPLETE");
        UserManager.setActiveUser(user);

        createInitialDataIfNeeded();

        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    private void attemptLogin() {
        usernameView.setError(null);
        passwordView.setError(null);

        final String email = usernameView.getText().toString();
        final String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)){
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)){
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        } else {
            showProgress(true);
            Log.d(TAG, "LOGGING IN NOW");
            SyncUser.loginAsync(SyncCredentials.usernamePassword(email, password, false), PrivateHobbySpot.AUTH_URL, this);
        }
    }

    private void showProgress(final boolean show){
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        loginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.GONE : View.VISIBLE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onSuccess(SyncUser result) {
        Log.d(TAG, "SUCCESS");
        showProgress(false);
        loginComplete(result);
    }

    @Override
    public void onError(ObjectServerError error) {
        Log.d(TAG, "Failed but not running this.");
        showProgress(false);
        String errorMsg;
        switch (error.getErrorCode()){
            case UNKNOWN_ACCOUNT:
                errorMsg = "Account does not exist.";
                break;
            case INVALID_CREDENTIALS:
                errorMsg = "The provided credentials are invalid!";
                break;
            default:
                errorMsg = error.toString();
        }
        Log.d(TAG, error.toString());

        Toast.makeText(SignInActivity.this, errorMsg, Toast.LENGTH_LONG);
    }

    private static void createInitialDataIfNeeded(){
        Log.d(TAG, "For some Reason we are here.");
        final Realm realm = Realm.getDefaultInstance();
        try {
        } finally {
            realm.close();
        }
    }
}

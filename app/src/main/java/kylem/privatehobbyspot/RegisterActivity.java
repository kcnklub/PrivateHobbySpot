package kylem.privatehobbyspot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncCredentials;
import io.realm.ObjectServerError;
import io.realm.SyncUser;
import kylem.privatehobbyspot.auth.facebook.FacebookAuth;
import kylem.privatehobbyspot.auth.google.GoogleAuth;
import kylem.privatehobbyspot.entities.User;

import static android.text.TextUtils.isEmpty;
import static kylem.privatehobbyspot.PrivateHobbySpot.AUTH_URL;

public class RegisterActivity extends AppCompatActivity implements SyncUser.Callback<SyncUser> {

    private AutoCompleteTextView usernameView;
    private EditText passwordView;
    private EditText passwordConfirmationView;
    private View progressView;
    private View registerFormView;
    private FacebookAuth facebookAuth;
    private GoogleAuth googleAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameView = (AutoCompleteTextView) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        passwordConfirmationView = (EditText) findViewById(R.id.password_confirmation);
        passwordConfirmationView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });


        final Button mailRegisterButton = (Button) findViewById(R.id.email_register_button);
        mailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        registerFormView = findViewById(R.id.register_form);
        progressView = findViewById(R.id.register_progress);

        // Setup Facebook Authentication
        facebookAuth = new FacebookAuth((LoginButton) findViewById(R.id.login_button)) {
            @Override
            public void onRegistrationComplete(final LoginResult loginResult) {
                UserManager.setAuthMode(UserManager.AUTH_MODE.FACEBOOK);
                final SyncCredentials credentials = SyncCredentials.facebook(loginResult.getAccessToken().getToken());

                Realm realm = Realm.getDefaultInstance();
                try{
                    RealmResults<User> checkUser = realm.where(User.class).equalTo("DisplayName", credentials.getIdentityProvider()).findAll();
                    if(checkUser.isEmpty()){
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                User savedUser = realm.createObject(User.class, credentials.getUserIdentifier());
                                savedUser.setDisplayName(credentials.getIdentityProvider());
                            }
                        });
                        SyncUser.loginAsync(credentials, AUTH_URL, RegisterActivity.this);
                    } else {
                        Toast.makeText(RegisterActivity.this, "This email has already been used.", Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    realm.close();
                }

            }
        };

        // Setup Google Authentication
        googleAuth = new GoogleAuth((SignInButton) findViewById(R.id.sign_in_button), this) {
            @Override
            public void onRegistrationComplete(GoogleSignInResult result) {
                UserManager.setAuthMode(UserManager.AUTH_MODE.GOOGLE);
                GoogleSignInAccount acct = result.getSignInAccount();
                final SyncCredentials credentials = SyncCredentials.google(acct.getIdToken());
                Realm realm = Realm.getDefaultInstance();
                try{
                    RealmResults<User> checkUser = realm.where(User.class).equalTo("DisplayName", credentials.getIdentityProvider()).findAll();
                    if(checkUser.isEmpty()){
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                User savedUser = realm.createObject(User.class, credentials.getUserIdentifier());
                                savedUser.setDisplayName(credentials.getIdentityProvider());
                            }
                        });
                        SyncUser.loginAsync(credentials, AUTH_URL, RegisterActivity.this);

                    } else {
                        Toast.makeText(RegisterActivity.this, "You have already used this google account to register before", Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    realm.close();
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        googleAuth.onActivityResult(requestCode, resultCode, data);
        facebookAuth.onActivityResult(requestCode, resultCode, data);
    }

    private void attemptRegister() {
        usernameView.setError(null);
        passwordView.setError(null);
        passwordConfirmationView.setError(null);

        final String username = usernameView.getText().toString();
        final String password = passwordView.getText().toString();
        final String passwordConfirmation = passwordConfirmationView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (isEmpty(username)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        }

        if (isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        if (isEmpty(passwordConfirmation)) {
            passwordConfirmationView.setError(getString(R.string.error_field_required));
            focusView = passwordConfirmationView;
            cancel = true;
        }

        if (!password.equals(passwordConfirmation)) {
            passwordConfirmationView.setError(getString(R.string.error_incorrect_password));
            focusView = passwordConfirmationView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            Realm realm = Realm.getDefaultInstance();
            try{
                RealmResults<User> checkUser = realm.where(User.class).equalTo("DisplayName", username).findAll();
                if(checkUser.isEmpty()){
                    showProgress(true);
                    SyncUser.loginAsync(SyncCredentials.usernamePassword(username, password, true), PrivateHobbySpot.AUTH_URL, new SyncUser.Callback<SyncUser>() {
                        @Override
                        public void onSuccess(final SyncUser user) {
                            Realm realm = Realm.getDefaultInstance();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    User savedUser = realm.createObject(User.class, user.getIdentity());
                                    savedUser.setDisplayName(username);
                                }
                            });
                            realm.close();

                            registrationComplete(user);
                        }

                        @Override
                        public void onError(ObjectServerError error) {
                            showProgress(false);
                            String errorMsg;
                            switch (error.getErrorCode()) {
                                case EXISTING_ACCOUNT: errorMsg = "Account already exists"; break;
                                default:
                                    errorMsg = error.toString();
                            }
                            Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "User with that email already exists", Toast.LENGTH_SHORT).show();
                }
            } finally {
                realm.close();
            }


        }
    }

    private void registrationComplete(SyncUser user) {
        UserManager.setActiveUser(user);
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showProgress(final boolean show) {
        final int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        registerFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onSuccess(SyncUser user) {
        registrationComplete(user);
    }

    @Override
    public void onError(ObjectServerError error) {
        String errorMsg;
        switch (error.getErrorCode()) {
            case EXISTING_ACCOUNT: errorMsg = "Account already exists"; break;
            default:
                errorMsg = error.toString();
        }
        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
    }
}

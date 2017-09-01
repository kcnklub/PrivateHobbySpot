package kylem.privatehobbyspot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.FileNotFoundException;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import kylem.privatehobbyspot.entities.Migration;
import kylem.privatehobbyspot.entities.User;

public class SignIn extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private Realm realm;

    private User userToSignIn;
    Intent moveToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mGoogleApiClient = ((PrivateHobbySpot) getApplication()).getmGoogleApiClient();

        moveToMain = new Intent(this, MainActivity.class);

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        mStatusTextView  = (TextView) findViewById(R.id.status);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(false);
                    }
                }
        );
    }

    @Override
    public void onConnectionFailed(ConnectionResult result){
        Log.d(TAG, "onConnectionFailed: " + result);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from launching the Intent from GoogleSignInAPI.getSignInIntent(...)
        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()){
            //Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            realm = Realm.getDefaultInstance();
            try{
                userToSignIn = new User(acct.getDisplayName(), acct.getEmail(), null);
                realm.executeTransactionAsync(new Realm.Transaction(){
                        @Override
                        public void execute(Realm realm) {
                            User user = realm.createObject(User.class, acct.getEmail());
                            user.setDisplayName(acct.getDisplayName());
                            user.setPassword(null);
                        }
                }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess(){
                                // Transaction was a success.
                                Log.d(TAG, "Successful Realm Transaction");

                            }
                }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error){
                                // Transaction didn't happen
                                Log.d(TAG, error.getMessage());
                            }
                                              }
                );
            } finally {
                realm.close();
            }
            updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn){
        if (signedIn){
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            moveToMain.putExtra("user", userToSignIn);
            startActivity(moveToMain);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        }
    }

    private String realmString(Realm realm){
        StringBuilder stringBuilder = new StringBuilder();
        for(User user : realm.where(User.class).findAll()){
            stringBuilder.append(user.toString()).append("\n");
        }
        return (stringBuilder.length() == 0) ? "<data was deleted>" : stringBuilder.toString();
    }

    private void showStatus(Realm realm){
        showStatus(realmString(realm));
    }

    private void showStatus(String txt){
        Log.i(TAG, txt);
    }

    private void resetRealm(){
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(0)
                .build();
        Realm.deleteRealm(config);
    }

}

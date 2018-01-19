package kylem.privatehobbyspot.auth.google;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import kylem.privatehobbyspot.R;

/**
 * Created by kylem on 1/18/2018.
 */

public abstract class GoogleAuth implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 10;

    public GoogleAuth(final SignInButton btnSignIn, final FragmentActivity fragmentActivity){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(fragmentActivity.getString(R.string.server_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(fragmentActivity)
                .enableAutoManage(fragmentActivity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                fragmentActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    public final void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public final void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        if(!connectionResult.hasResolution()){
            onError("Connection failed and has no resolution. code: " + connectionResult.getErrorCode());
        }
    }

    public abstract void onRegistrationComplete(final GoogleSignInResult result);

    public void onError(String s) {}

    private void handleSignInResult(GoogleSignInResult result){
        System.out.println("handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()){
            onRegistrationComplete(result);
        }else{

        }
    }
}

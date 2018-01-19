package kylem.privatehobbyspot.auth.facebook;

import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by kylem on 1/18/2018.
 */

public abstract class FacebookAuth {
    private final LoginButton loginButton;
    private final CallbackManager callbackManager;

    public FacebookAuth(final LoginButton loginBtn){
        callbackManager = CallbackManager.Factory.create();
        this.loginButton = loginBtn;
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                onRegistrationComplete(loginResult);
            }

            @Override
            public void onCancel() {
                onAuthCancelled();
            }

            @Override
            public void onError(FacebookException error) {
                onAuthError();
            }
        });
    }

    public void onAuthCancelled() {}

    public void onAuthError() {}

    public final void onActivityResult(int requestCode, int resultCode, Intent data){
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public abstract void onRegistrationComplete(final LoginResult loginResult);
}

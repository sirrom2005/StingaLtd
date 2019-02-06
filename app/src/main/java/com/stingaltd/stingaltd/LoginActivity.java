package com.stingaltd.stingaltd;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stingaltd.stingaltd.Common.Common;
import com.stingaltd.stingaltd.Interface.IAccount;
import com.stingaltd.stingaltd.Models.Account;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.stingaltd.stingaltd.Common.Common.BASE_URL;
import static com.stingaltd.stingaltd.Common.Common.LOG_TAG;
import static com.stingaltd.stingaltd.Common.Common.TIME_OUT;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements Callback<Account>
{
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private boolean isUp = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        RelativeLayout window = findViewById(R.id.window);
    }

    private void animiLogin(boolean hasFocus){
        if(hasFocus && isUp==false) {
            Animation up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_up);
            mLoginFormView.startAnimation(up);
            isUp = true;
        }else {
            Animation up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.login_down);
            mLoginFormView.startAnimation(up);
            isUp = false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Attempts to sign in or register the LoginUser specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail    = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(Common.isInternetAvailable()) {
                start(mEmail, mPassword);
            }else {
                try {
                    readLoginFile(mEmail, mPassword);
                } catch (IOException | ClassNotFoundException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(!aBoolean){
                Toast.makeText(getBaseContext(), "No internet connection found", Toast.LENGTH_LONG).show();
                this.onCancelled();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public void start(String username, String password) {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        IAccount account = retrofit.create(IAccount.class);

        Call<Account> call = account.repo(username,password);
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<Account> call, @NonNull Response<Account> response)
    {
        if (response.body()!=null) {
            try {
                String email = response.body().getEmail();
                int id = response.body().getTechnicianId();
                registerFbToken(email);
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_email), MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.preference_email_key), email);
                editor.putInt(getString(R.string.preference_id_key), id);
                editor.apply();

                Common.SaveObjectAsFile(getApplicationContext(), response.body(), Common.getFileNameFromEmail(email));

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } catch (IOException ex) {
                mPasswordView.setError(getString(R.string.error_unknown));
                mPasswordView.requestFocus();
                Log.e(LOG_TAG, ex.getMessage());
            }
        } else {
            mPasswordView.setError(getString(R.string.error_incorrect_login));
            mPasswordView.requestFocus();
        }

        mAuthTask = null;
        showProgress(false);
    }

    @Override
    public void onFailure(@NonNull Call<Account> call, @NonNull Throwable t) {
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG ).show();
        mAuthTask = null;
        showProgress(false);
        Log.e(LOG_TAG, t.getMessage());
    }

    private void readLoginFile(String email, String password) throws IOException, ClassNotFoundException {
        Account obj = (Account) Common.readObjectFromFile(getApplicationContext(), Common.getFileNameFromEmail(email));
        byte[] data = password.getBytes("UTF-8");
        password = Base64.encodeToString(data, Base64.NO_WRAP);

        if(obj.getPass().equals(password)){
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_email), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.preference_email_key), email);
            editor.putInt(getString(R.string.preference_id_key), obj.getTechnicianId());
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            this.runOnUiThread(new Runnable() {
                public void run() {
                    mPasswordView.setError(getString(R.string.error_incorrect_login));
                    mPasswordView.requestFocus();
                    mAuthTask = null;
                    showProgress(false);
                }
            });
        }
    }

    private void registerFbToken(final String email) {
        if(Common.isInternetAvailable()) {
            File f = new File(getApplicationContext().getFilesDir(), Common.getFileNameFromEmail(email));
            if (!f.exists()) {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(Common.LOG_TAG, "getInstanceId failed", task.getException());
                                    return;
                                }
                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                sendRegistrationToServer(email, token);
                            }
                        });
            }
        }
    }

    private void sendRegistrationToServer(final String Email, final String token)
    {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("email", Email)
                        .add("token", token)
                        .build();

                final Request request = new Request.Builder()
                        .url(Common.BASE_URL + "add_user_token")
                        .post(requestBody)
                        .build();

                try{
                    okhttp3.Response response = client.newCall(request).execute();
                    String body = null;
                    if (response.body() != null) {
                        body = response.body().string();
                    }
                    Log.d(LOG_TAG, body);
                } catch(
                        IOException ex)

                {
                    Log.e(LOG_TAG, ex.getMessage());
                }
                return null;
            }
        };
        task.execute();
    }
}


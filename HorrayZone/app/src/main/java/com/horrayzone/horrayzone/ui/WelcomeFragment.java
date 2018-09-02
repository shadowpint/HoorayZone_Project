package com.horrayzone.horrayzone.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.RequestError;
import com.horrayzone.horrayzone.model.Ticket;
import com.horrayzone.horrayzone.provider.HoorayZoneContract;
import com.horrayzone.horrayzone.sync.AccountAuthenticator;
import com.horrayzone.horrayzone.ui.widget.EmptyView;
import com.horrayzone.horrayzone.util.AccountUtils;
import com.horrayzone.horrayzone.util.SyncUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class WelcomeFragment extends Fragment implements
        View.OnClickListener{

    private static final String TAG = "WelcomeFragment";
    private static final int RC_GET_TOKEN = 9002;
    private Button signOutButton;

    public static WelcomeFragment newInstance() {
        return new WelcomeFragment();
    }
   private GoogleSignInButton signInButton;
    private AuthenticatorActivity mActivity;
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validateServerClientID();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AuthenticatorActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        EmptyView welcomeEmptyView = (EmptyView) rootView.findViewById(R.id.welcome_empty_view);
        welcomeEmptyView.setAction("Skip to home", (View view) -> {
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });
        rootView.findViewById(R.id.sign_in_button).setOnClickListener(v -> mActivity.switchToSignInFragment());
         signInButton = rootView.findViewById(R.id.google_sign_in_button);
        signInButton.setOnClickListener(this::onClick);

        rootView.findViewById(R.id.create_account_button).setOnClickListener(v -> mActivity.switchToSignUpFragment());
        rootView.findViewById(R.id.sign_in_button).setOnClickListener(v -> mActivity.switchToSignInFragment());

        return rootView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onStart() {
        super.onStart();
        if (mActivity != null) {
            //mActivity.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.theme_accent_dark));
//            mActivity.setActionBarShown(false);
//            mActivity.getSupportActionBar().setTitle(null);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    private void refreshIdToken() {
        // Attempt to silently refresh the GoogleSignInAccount. If the GoogleSignInAccount
        // already has a valid token this method may complete immediately.
        //
        // If the user has not previously signed in on this device or the sign-in has expired,
        // this asynchronous branch will attempt to sign in the user silently and get a valid
        // ID token. Cross-device single sign on will occur in this branch.
        mGoogleSignInClient.silentSignIn()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        handleSignInResult(task);
                    }
                });
    }

    // [START handle_sign_in_result]
    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            // TODO(developer): send ID Token to server and validate

            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "handleSignInResult:error", e);
            updateUI(null);
        }
    }
    // [END handle_sign_in_result]

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GET_TOKEN) {
            // [START get_id_token]
            // This task is always completed immediately, there is no need to attach an
            // asynchronous listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            // [END get_id_token]
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {

            String idToken = account.getIdToken();
            new VerifyTokenTask().execute(idToken);
Log.e("token",idToken );

        }
    }
    private class VerifyTokenTask extends AsyncTask<String, Void, String> {

        public VerifyTokenTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String idToken = params[0];



            String result = null;

            try {
                result= String.valueOf(SyncUtils.sWebService.verifyGoogleToken(idToken));


            } catch (RetrofitError error) {

                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    result = "No connection.";
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
                    Response response = error.getResponse();
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    RequestError reqError = new Gson().fromJson(json, RequestError.class);
                    result = reqError.getDescription();
                } else {
                    result = error.getLocalizedMessage();
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            JsonPrimitive js=null;
//            js = new Gson().fromJson(result.trim(), JsonPrimitive.class);
            JSONObject obj = null;
            Log.e("login_result",result);
            try {
                obj = new JSONObject(result);
        if(obj.has("given_name")){






            Bundle i = new Bundle();
            i.putString("username",obj.getString("username"));
            i.putString("email",obj.getString("email"));
            i.putString("name",obj.getString("name"));
            i.putString("picture",obj.getString("picture"));
            i.putString("first_name",obj.getString("given_name"));
            i.putString("last_name",obj.getString("family_name"));


            SignUpFragment frag = new SignUpFragment();
            frag.setArguments(i);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container
                            , frag,"signup_fragment")
                    .addToBackStack(null)
                    .commit();
        }
        else{


            Bundle i = new Bundle();
            i.putString("username",obj.getString("username"));
            i.putString("email",obj.getString("email"));
            SignInFragment frag = new SignInFragment();
            frag.setArguments(i);
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container
                            , frag,"signin_fragment")
                    .addToBackStack(null)
                    .commit();
        }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


    }
    /**
     * Validates that there is a reasonable server client ID in strings.xml, this is only needed
     * to make sure users of this sample follow the README.
     */
    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                getIdToken();
                break;
//            case R.id.sign_out_button:
//                signOut();
//                break;
//            case R.id.disconnect_button:
//                revokeAccess();
//                break;
//            case R.id.button_optional_action:
//                refreshIdToken();
//                break;
        }
    }
}

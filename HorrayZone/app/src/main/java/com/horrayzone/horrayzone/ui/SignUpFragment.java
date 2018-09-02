package com.horrayzone.horrayzone.ui;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.horrayzone.horrayzone.Config;
import com.horrayzone.horrayzone.R;
import com.horrayzone.horrayzone.model.AuthTokenResponse;
import com.horrayzone.horrayzone.model.RequestError;
import com.horrayzone.horrayzone.sync.AccountAuthenticator;
import com.horrayzone.horrayzone.util.SyncUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class SignUpFragment extends Fragment {

    private static final String TAG = "SignUpFragment";
    private String username,email,name,first_name,last_name,picture;
    private EditText mlastNameEditText,mEmailEditText,mUsernameEditText;
    private SignInTask mSignInTask;


    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    public static final String STATE_OPERATION_IN_PROGRESS = "state_operation_in_progress";

    //public static final int MIN_PASSWORD_LENGTH = 5;

    private AuthenticatorActivity mActivity;
    private TextInputLayout mFirstNameInputLayout;
    private TextInputLayout mLastNameInputLayout;
    private TextInputLayout mUserNameInputLayout;
    private TextInputLayout mCompanyInputLayout;
    private TextInputLayout mEmailInputLayout;
    private TextInputLayout mPasswordInputLayout;
    private TextInputEditText mfirstNameEditText;
    private CheckBox mTosCheckBox;
    private Button mCreateAccountButton;
    private View mCurrentFocusView;

    private SignUpTask mSignUpTask;
    private PasswordUpdateTask mPasswordUpdateTask;
    private boolean mOperationInProgress = false;
    private int mStatusBarColor;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mCreateAccountButton.setEnabled(validateFields());
        }
    };

    private View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mCurrentFocusView = v;
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AuthenticatorActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.getString("email") != null) {
                email = bundle.getString("email");
                username=bundle.getString("username");
                name = bundle.getString("name");
                first_name = bundle.getString("first_name");
                last_name = bundle.getString("last_name");
                picture = bundle.getString("picture");

                Log.e("email", email);
            }

        }
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mFirstNameInputLayout = (TextInputLayout) rootView.findViewById(R.id.first_name_input_layout);
        mFirstNameInputLayout.getEditText().addTextChangedListener(mTextWatcher);
        mFirstNameInputLayout.getEditText().setOnFocusChangeListener(mFocusChangeListener);

        mLastNameInputLayout = (TextInputLayout) rootView.findViewById(R.id.last_name_input_layout);
        mLastNameInputLayout.getEditText().addTextChangedListener(mTextWatcher);
        mLastNameInputLayout.getEditText().setOnFocusChangeListener(mFocusChangeListener);

        mUserNameInputLayout = (TextInputLayout) rootView.findViewById(R.id.username_input_layout);
        mUserNameInputLayout.getEditText().setOnFocusChangeListener(mFocusChangeListener);


        mCompanyInputLayout = (TextInputLayout) rootView.findViewById(R.id.company_input_layout);
        mCompanyInputLayout.getEditText().setOnFocusChangeListener(mFocusChangeListener);

        mEmailInputLayout = (TextInputLayout) rootView.findViewById(R.id.email_input_layout);
        mEmailInputLayout.getEditText().addTextChangedListener(mTextWatcher);
        mEmailInputLayout.getEditText().setOnFocusChangeListener(mFocusChangeListener);

        mPasswordInputLayout = (TextInputLayout) rootView.findViewById(R.id.password_input_layout);
        mPasswordInputLayout.getEditText().addTextChangedListener(mTextWatcher);
        mPasswordInputLayout.getEditText().setOnFocusChangeListener(mFocusChangeListener);

        mTosCheckBox = (CheckBox) rootView.findViewById(R.id.tos_checkbox);
        mTosCheckBox.setOnCheckedChangeListener(
                (button, isChecked) -> mCreateAccountButton.setEnabled(validateFields()));

        mCreateAccountButton = (Button) rootView.findViewById(R.id.create_account_button);
        if(email!=null){
            mCreateAccountButton.setOnClickListener((View v) -> {
                mPasswordUpdateTask = new PasswordUpdateTask();
                mPasswordUpdateTask.execute(

                        mUserNameInputLayout.getEditText().getText().toString(),
                        mEmailInputLayout.getEditText().getText().toString(),
                        mPasswordInputLayout.getEditText().getText().toString());
            });


        }
        else{


            mCreateAccountButton.setOnClickListener((View v) -> {
                mSignUpTask = new SignUpTask();
                mSignUpTask.execute(
                        mFirstNameInputLayout.getEditText().getText().toString(),
                        mLastNameInputLayout.getEditText().getText().toString(),
                        mUserNameInputLayout.getEditText().getText().toString(),
                        mCompanyInputLayout.getEditText().getText().toString(),
                        mEmailInputLayout.getEditText().getText().toString(),
                        mPasswordInputLayout.getEditText().getText().toString());
            });
        }


        mStatusBarColor = ContextCompat.getColor(getActivity(), R.color.theme_primary_dark);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUsernameEditText = (EditText) view.findViewById(R.id.username_edit_text);
        if(username!=null){
            mUsernameEditText.setText(username);

        }


        mfirstNameEditText = (TextInputEditText) view.findViewById(R.id.first_name_edit_text);
        if(first_name!=null){
            mfirstNameEditText.setText(first_name);
            mfirstNameEditText.setEnabled(false);
        }
        mlastNameEditText = (EditText) view.findViewById(R.id.last_name_edit_text);
        if(last_name!=null){
            mlastNameEditText.setText(last_name);
            mlastNameEditText.setEnabled(false);
        }
        mEmailEditText = (EditText) view.findViewById(R.id.email_edit_text);
        if(email!=null){
            mEmailEditText.setText(email);
            mEmailEditText.setEnabled(false);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.i(TAG, "onViewStateRestored");

        // We must wait until this call (onViewStateRestored), where the system already restored the
        // state of the views in this fragment, to additionally restore the entire form state.
        if (savedInstanceState != null) {
            mOperationInProgress = savedInstanceState.getBoolean(STATE_OPERATION_IN_PROGRESS, false);

            // The system restores the view's state after onCreateView happens, so any TextWatcher
            // associated with an EditText is triggered when the text is restored, in this case, our
            // TextWatcher enables/disables the submit button according to the form validation
            // state, so the submit button state is restored well. We only need to handle the case
            // of state restoration when the form is fully disabled due to an operation in progress.
            if (mOperationInProgress) {
                setFormEditable(!mOperationInProgress);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onStart() {
        super.onStart();
        if (mActivity != null) {
            mActivity.setStatusBarColor(mStatusBarColor);
            mActivity.setActionBarShown(true);
            mActivity.setActionBarTitle(getString(R.string.title_fragment_sign_up));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_OPERATION_IN_PROGRESS, mOperationInProgress);
    }

    @SuppressWarnings("ConstantConditions")
    private void setFormEditable(boolean editable) {
        if (mActivity != null) {
            mActivity.setDisplayHomeAsUpEnabled(editable);
            mActivity.setToolbarProgressEnabled(!editable);
        }

        // Clear the focus of the current focused text field.
        if (mCurrentFocusView != null) {
            mCurrentFocusView.clearFocus();
        }

        mFirstNameInputLayout.getEditText().setEnabled(editable);
        mLastNameInputLayout.getEditText().setEnabled(editable);
        mUserNameInputLayout.getEditText().setEnabled(editable);
        mCompanyInputLayout.getEditText().setEnabled(editable);
        mEmailInputLayout.getEditText().setEnabled(editable);
        mPasswordInputLayout.getEditText().setEnabled(editable);
        mTosCheckBox.setEnabled(editable);

        mCreateAccountButton.setEnabled(editable);
    }

    @SuppressWarnings("ConstantConditions")
    private boolean validateFields() {
        return !TextUtils.isEmpty(mFirstNameInputLayout.getEditText().getText())
                && !TextUtils.isEmpty(mLastNameInputLayout.getEditText().getText())
                && !TextUtils.isEmpty(mUserNameInputLayout.getEditText().getText())
                && isValidEmail(mEmailInputLayout.getEditText().getText())
                && isValidPassword(mPasswordInputLayout.getEditText().getText())
                && mTosCheckBox.isChecked();
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(CharSequence password) {
        return !TextUtils.isEmpty(password)/* && password.length() >= MIN_PASSWORD_LENGTH*/;
    }

    @SuppressWarnings("ConstantConditions")
    private void cleanUpForm() {
        mFirstNameInputLayout.getEditText().setText(null);
        mLastNameInputLayout.getEditText().setText(null);
        mUserNameInputLayout.getEditText().setText(null);
        mCompanyInputLayout.getEditText().setText(null);
        mEmailInputLayout.getEditText().setText(null);
        mPasswordInputLayout.getEditText().setText(null);
        mTosCheckBox.setChecked(false);

        setFormEditable(true);
        mCreateAccountButton.setEnabled(validateFields());
    }

    private void showSignUpSuccessDialog() {
        DialogFragment dialog = new SignUpSucceedDialogFragment();
        dialog.show(getActivity().getSupportFragmentManager(), "signUpSucceed");
    }
    private void showPasswordSuccessDialog() {
        DialogFragment dialog = new PasswordSucceedDialogFragment();
        dialog.show(getActivity().getSupportFragmentManager(), "passwordSucceed");
    }
    private class PasswordUpdateTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "PasswordUpdateTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setFormEditable(!(mOperationInProgress = true));
        }

        @Override
        protected String doInBackground(String... params) {

            String userName = params[0];

            String email = params[1];
            String password = params[2];

            String message = null;

            try {
                message= SyncUtils.sWebService.createPassword(Config.OAUTH_CLIENT_ID, Config.OAUTH_CLIENT_SECRET,
                        Config.OAUTH_GRANT_TYPE_PASSWORD,
                        userName,email, password);
//                Log.i(TAG, "doInBackground: response code " + response.getStatus());
            } catch (RetrofitError error) {
                error.printStackTrace();

                // TODO: Change this hardcoded string to string resources.
                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    message = "No Connection.";
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
                    Response response = error.getResponse();
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    RequestError reqError = new Gson().fromJson(json, RequestError.class);
                    message = reqError.getDescription();
                } else {
                    message = "Unknown error";
                }
            }

            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            JSONObject obj = null;

            try {
                obj = new JSONObject(message);
                Log.d("My App", obj.getString("message"));
                if ( obj.getString("message").equals("registered")) {
                    attemptSignIn();


                } else {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    setFormEditable(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // A null message means that the sign up process completed successfully.


            mOperationInProgress = false;
            mPasswordUpdateTask = null;
        }
    }
    private class SignUpTask extends AsyncTask<String, Void, String> {

        private static final String TAG = "SignUpTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setFormEditable(!(mOperationInProgress = true));
        }

        @Override
        protected String doInBackground(String... params) {
            String firstName = params[0];
            String lastName = params[1];
            String userName = params[2];
            String company = params[3];
            String email = params[4];
            String password = params[5];

            String message = null;

            try {
                Response response = SyncUtils.sWebService.createAccount(Config.OAUTH_CLIENT_ID, Config.OAUTH_CLIENT_SECRET,
                        Config.OAUTH_GRANT_TYPE_PASSWORD,
                         firstName, lastName, userName,email, password, company);
                Log.i(TAG, "doInBackground: response code " + response.getStatus());
            } catch (RetrofitError error) {
                error.printStackTrace();

                // TODO: Change this hardcoded string to string resources.
                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    message = "No Connection.";
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
                    Response response = error.getResponse();
                    String json = new String(((TypedByteArray) response.getBody()).getBytes());
                    RequestError reqError = new Gson().fromJson(json, RequestError.class);
                    message = reqError.getDescription();
                } else {
                    message = "Unknown error";
                }
            }

            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);

            // A null message means that the sign up process completed successfully.
            if (message == null) {
                cleanUpForm();
                showSignUpSuccessDialog();
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                setFormEditable(false);
            }

            mOperationInProgress = false;
            mSignUpTask = null;
        }
    }

    public static class SignUpSucceedDialogFragment extends DialogFragment {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO: Change this dialog hardcoded strings to string resources.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_HoorayZone_Dialog_Alert);
            builder.setTitle("Almost done!");
            builder.setMessage("You need to confirm your email");
            builder.setPositiveButton("Got it", null);
            return builder.create();
        }
    }
    public static class PasswordSucceedDialogFragment extends DialogFragment {
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // TODO: Change this dialog hardcoded strings to string resources.
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_HoorayZone_Dialog_Alert);
            builder.setTitle("Done!");
            builder.setMessage("Account Created Successfully");
            builder.setPositiveButton("Close", null);
            return builder.create();
        }
    }



    public void attemptSignIn() {
        if (mSignInTask != null || !validateFields()) {
            return;
        }

        mSignInTask = new SignInTask();
        mSignInTask.execute(
                mUserNameInputLayout.getEditText().getText().toString(),
                mPasswordInputLayout.getEditText().getText().toString());
    }

    private class SignInTask extends AsyncTask<String, Void, Intent> {
        private static final String TAG = "SignInTask";

        public static final String JSON_FIELD_ERROR = "error";
        public static final String JSON_FIELD_ERROR_DESCRIPTION = "error_description";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setFormEditable(!(mOperationInProgress = true));
        }

        @Override
        protected Intent doInBackground(String... params) {
            String user = params[0];
            String password = params[1];

            Bundle extras = new Bundle();

            try {
                AuthTokenResponse tokenResponse = SyncUtils.sWebService.getAuthToken(
                        user, password, Config.OAUTH_CLIENT_ID, Config.OAUTH_CLIENT_SECRET,
                        Config.OAUTH_GRANT_TYPE_PASSWORD);

                // TODO: Get the info of the user signed in.
                // UserInfo info = SyncUtils.sWebService.getUserInfo(tokenResponse.getAccessToken(), email);

                extras.putString(AccountManager.KEY_ACCOUNT_NAME, user);
                extras.putString(AccountManager.KEY_ACCOUNT_TYPE, AccountAuthenticator.ACCOUNT_TYPE);
                extras.putString(AccountManager.KEY_AUTHTOKEN, tokenResponse.getAccessToken());
                extras.putString(AuthenticatorActivity.EXTRA_REFRESH_TOKEN, tokenResponse.getRefreshToken());

            } catch (RetrofitError error) {
                error.printStackTrace();

                if (error.getKind() == RetrofitError.Kind.NETWORK) {
                    extras.putString(AuthenticatorActivity.EXTRA_ERROR_MESSAGE, "No connection.");
                } else if (error.getKind() == RetrofitError.Kind.HTTP) {
                    Response response = error.getResponse();

                    String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                    JsonObject errorJson = new JsonParser().parse(jsonString).getAsJsonObject();
                    extras.putString(AuthenticatorActivity.EXTRA_ERROR_MESSAGE,
                            errorJson.get(JSON_FIELD_ERROR_DESCRIPTION).getAsString());
                    Log.d(TAG, "doInBackground() > ErrorJsonResponse: " + jsonString);
                } else {
                    extras.putString(AuthenticatorActivity.EXTRA_ERROR_MESSAGE,
                            error.getLocalizedMessage());
                }
            }

            Intent result = new Intent();
            result.putExtras(extras);

            return result;
        }

        @Override
        protected void onPostExecute(Intent resultIntent) {
            super.onPostExecute(resultIntent);
            Log.d(TAG, "onPostExecute");

            mSignInTask = null;
            mOperationInProgress = false;

            if (resultIntent.hasExtra(AuthenticatorActivity.EXTRA_ERROR_MESSAGE)) {
                // Set form as editable only if some error occurred.
                setFormEditable(!mOperationInProgress);

                Toast.makeText(mActivity,
                        resultIntent.getStringExtra(AuthenticatorActivity.EXTRA_ERROR_MESSAGE),
                        Toast.LENGTH_LONG).show();
                return;
            }

            mActivity.finishSignIn(resultIntent);
        }
    }
}

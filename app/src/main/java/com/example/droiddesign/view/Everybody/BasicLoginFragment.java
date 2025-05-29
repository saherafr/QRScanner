package com.example.droiddesign.view.Everybody;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.droiddesign.R;
import com.example.droiddesign.controller.MessageEvent;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.example.droiddesign.model.UsersDB;
import com.example.droiddesign.view.AttendeeAndOrganizer.AddProfilePictureActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * BasicLoginFragment is a dialog fragment that allows the user to create an account
 * by entering their name, email, role, company, and phone number. The user can also
 * skip account creation and navigate to the role selection activity. The user can also
 * set a profile picture by clicking on the profile picture button.
 */
public class BasicLoginFragment extends DialogFragment {

    public FirebaseAuth mAuth;
    private EditText editUserName;
    private EditText editEmail;
    private EditText editCompany;
    private EditText editPhoneNumber;
    private Spinner roleSpinner;
    private UserCreationListener listener;
    private SharedPreferenceHelper prefsHelper;
    private String profilePicUrl;

    /**
     * On receipt of a message event, set the profile picture URL
     * @param event The message event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        profilePicUrl = event.getMessage();
        Log.d("BasicLoginFragment", "Received profile picture URL: " + profilePicUrl);
    }

    /**
     * On attach of the context, check if the context implements the UserCreationListener
     * @param context The context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserCreationListener) {
            listener = (UserCreationListener) context;
        } else {
            throw new RuntimeException(context + " must implement UserCreationListener");
        }
        prefsHelper = new SharedPreferenceHelper(requireContext());
    }

    /**
     * Create the view for the BasicLoginFragment
     * @param inflater The layout inflater
     * @param container The view group container
     * @param savedInstanceState The saved instance state
     * @return The view for the BasicLoginFragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_up, container, false);
        if (view != null) {
            Dialog dialog = getDialog();
            if (dialog != null && dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
        assert view != null;
        initializeViews(view);
        setupPhoneNumberValidation();
        registerEventBus();
        setupListeners(view);

        return view;
    }

    /**
     * Create a new instance of the BasicLoginFragment
     * @return The BasicLoginFragment
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        return dialog;
    }

    /**
     * Create a new user
     * @param userName The user's name
     * @param email The user's email
     * @param role The user's role
     * @param company The user's company
     * @param phoneNumber The user's phone number
     */
    public void createUser(String userName, String email, String role, String company, String phoneNumber) {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            UsersDB userdb = new UsersDB(firestore);
            mAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                User newUser = null;
                                if (user != null) {
                                    newUser = new User(user.getUid(), role);
                                    newUser.setUserName(userName);
                                    newUser.setEmail(email);
                                    newUser.setRegistered(String.valueOf(true));
                                    newUser.setCompany(company);
                                    newUser.setPhone(phoneNumber);

                                    // Set default settings
                                    newUser.setGeolocation(false);
                                    newUser.setNotificationPreference("Selected Events");

                                    profilePicUrl = determineProfilePicUrl(userName);

                                    newUser.setProfilePic(profilePicUrl);
                                    prefsHelper.saveUserProfile(user.getUid(), role, email);
                                }

                                assert newUser != null;
                                userdb.addUser(newUser);
                                listener.userCreated();
                                navigateToEventMenu();
                            } else {
                                Log.e("BasicLoginFragment", "Authentication failed.", task.getException());
                                Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error creating user", e);
            Toast.makeText(getContext(), "Error creating account", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Initialize the views
     * @param view The view
     */
    private void initializeViews(View view) {
        editUserName = view.findViewById(R.id.edit_user_name);
        editEmail = view.findViewById(R.id.edit_email);
        editCompany = view.findViewById(R.id.edit_company);
        editPhoneNumber = view.findViewById(R.id.edit_phone_number);
        roleSpinner = view.findViewById(R.id.spinner_role);
        // Create an ArrayAdapter using the custom layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.role_options, R.layout.spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        // Apply the adapter to the spinner
        roleSpinner.setAdapter(adapter);
    }

    /**
     * Register the EventBus
     */
    private void registerEventBus() {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error registering EventBus", e);
        }
    }

    /**
     * Setup listeners for the buttons
     * @param view The view
     */
    private void setupListeners(View view) {
        Button createAccountButton = view.findViewById(R.id.button_create_account);
        Button skipButton = view.findViewById(R.id.skip_account_creation);
        Button profilePicButton = view.findViewById(R.id.button_profile_picture);

        createAccountButton.setOnClickListener(v -> {
            try {
                String userName = editUserName.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String company = editCompany.getText().toString().trim();
                String phoneNumber = editPhoneNumber.getText().toString().trim();
                String role = roleSpinner.getSelectedItem().toString();


                // Check for valid user name
                if (userName.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;  // Stop further execution
                } else if (userName.length() == 1) {
                    Toast.makeText(getContext(), "Username must be more than one character", Toast.LENGTH_SHORT).show();
                    return;  // Stop further execution
                }

                // Check for valid phone number length
                if (phoneNumber.length() != 10) {
                    Toast.makeText(getContext(), "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
                    return;  // Stop further execution
                }

                // If all validations pass, proceed to create the user account
                createUser(userName, email, role, company, phoneNumber);
                Toast.makeText(getContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                dismiss();

            } catch (Exception e) {
                Log.e("BasicLoginFragment", "Error creating user account", e);
                Toast.makeText(getContext(), "Error creating account", Toast.LENGTH_SHORT).show();
            }
        });


        profilePicButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getActivity(), AddProfilePictureActivity.class);
                intent.putExtra("image_url", profilePicUrl);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("BasicLoginFragment", "Error opening profile picture activity", e);
                Toast.makeText(getContext(), "Error setting profile picture", Toast.LENGTH_SHORT).show();
            }
        });

        skipButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getActivity(), RoleSelectionActivity.class);
                startActivity(intent);
                requireActivity().finish();
            } catch (Exception e) {
                Log.e("BasicLoginFragment", "Error skipping account creation", e);
                Toast.makeText(getContext(), "Error skipping account creation", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Determine the profile picture URL based on the user's name
     * @param userName The user's name
     * @return The URL of the profile picture
     */
    private String determineProfilePicUrl(String userName) {
        try {
            if (profilePicUrl == null || profilePicUrl.isEmpty()) {
                return "https://ui-avatars.com/api/?name=" + userName + "&background=random";
            }
            return profilePicUrl;
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error determining profile picture URL", e);
            return "";
        }
    }


    /**
     * Navigate to the event menu activity
     */
    private void navigateToEventMenu() {
        try {
            if (isAdded() && getActivity() != null) {
                Intent intent = new Intent(getActivity(), EventMenuActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error navigating to event menu", e);
            Toast.makeText(getContext(), "Error navigating to event menu", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Unregister the EventBus
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error unregistering EventBus", e);
        }
    }

    /**
     * Interface for user creation listener
     */
    interface UserCreationListener {
        void userCreated();
    }

    /**
     * Setup phone number validation
     */
    private void setupPhoneNumberValidation() {
        editPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = s.toString().trim();
                if (phoneNumber.length() != 10) {
                    editPhoneNumber.setError("Phone number must be 10 digits");
                } else {
                    editPhoneNumber.setError(null);
                }
            }
        });
    }
}

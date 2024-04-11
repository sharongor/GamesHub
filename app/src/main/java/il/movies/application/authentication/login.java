package il.movies.application.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import il.movies.application.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class login extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    final String[] email = {""};
    final String[] password = {""};
    final String[] phone = {""};
    final String[] name = {""};


    public login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment login.
     */
    // TODO: Rename and change types and number of parameters
    public static login newInstance(String param1, String param2) {
        login fragment = new login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //if the user is logged in, navigate to introduction screen
        if(currentUser!=null){
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_login_to_introduction);
        }
        TextView registerBtn = view.findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegister(view);
            }
        });

        Button loginBtn = view.findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_btn(view);
            }
        });

        TextInputLayout emailEditText = view.findViewById(R.id.login_email);
        TextInputLayout passwordEditText = view.findViewById(R.id.password_login);

        TextWatcher emailTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email[0] = emailEditText.getEditText().getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextWatcher passwordTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password[0] = passwordEditText.getEditText().getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        emailEditText.getEditText().addTextChangedListener(emailTextWatcher);
        passwordEditText.getEditText().addTextChangedListener(passwordTextWatcher);


        LinearLayout root = (LinearLayout)view.findViewById(R.id.homeParent);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(root);
                return false;
            }
        });

        return view;
    }

    public void login_btn(View view) {
        if(email[0].isEmpty() || password[0].isEmpty()){
            Toast.makeText(requireContext(),"Empty fields, fill them!",Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(email[0], password[0])
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(requireContext(),"Succefully Logged in",Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(getView()).navigate(R.id.action_login_to_introduction);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }



    public void moveToRegister(View view) {
        Navigation.findNavController(requireView()).navigate(R.id.action_login_to_register);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
package il.movies.application.authentication;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

import il.movies.application.R;
import il.movies.application.data.Data;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class register extends Fragment {

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
    private boolean isPrivate = false;

    private ActivityResultLauncher<String> imageLauncher;
    Uri imageTaken=null;
    ImageView imagePerson;
    ImageButton icon;
    ProgressBar progressBar;

    public register() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment register.
     */
    // TODO: Rename and change types and number of parameters
    public static register newInstance(String param1, String param2) {
        register fragment = new register();
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

        imageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri != null) {
                    imagePerson = requireActivity().findViewById(R.id.image_person);
                        Glide.with(register.this).asBitmap().load(uri).transform(new CircleCrop()).into(imagePerson);
                        imageTaken = uri;
                }
                else {
                    Toast.makeText(requireActivity(),"You haven't chose a photo",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressBar = view.findViewById(R.id.ProgressBar);
        Button registerBtn = view.findViewById(R.id.register_btn);
        //clicked to register
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFunc(view);
                progressBar.setVisibility(View.VISIBLE);
            }
        });



        //Picking an image from the gallery
        icon = (ImageButton)view.findViewById(R.id.image_picker);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageLauncher.launch("image/*");
            }
        });


        //listening to what the user has typed
        TextInputLayout emailEditText = view.findViewById(R.id.email_register);
        TextInputLayout passwordEditText = view.findViewById(R.id.password_register);
        TextInputLayout phoneEditText = view.findViewById(R.id.phone_register);
        TextInputLayout nameEditText = view.findViewById(R.id.name_register);
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

        TextWatcher phoneTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone[0] = phoneEditText.getEditText().getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        };

        TextWatcher nameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name[0] = nameEditText.getEditText().getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        emailEditText.getEditText().addTextChangedListener(emailTextWatcher);
        passwordEditText.getEditText().addTextChangedListener(passwordTextWatcher);
        phoneEditText.getEditText().addTextChangedListener(phoneTextWatcher);
        nameEditText.getEditText().addTextChangedListener(nameTextWatcher);


        //hiding the keyboard when touching anywhere on the screen if its opened
        LinearLayout root = (LinearLayout)view.findViewById(R.id.homeParent);
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(root);
                return false;
            }
        });

        RadioButton privacy = view.findViewById(R.id.profile_private);
        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPrivate){
                    isPrivate = true;
                    privacy.setChecked(true);
                }
                else {
                    isPrivate = false;
                    privacy.setChecked(false);
                }
            }
        });

        return view;
    }

    public void registerFunc(View view) {

        if(email[0].isEmpty() || password[0].isEmpty() || phone[0].isEmpty() || name[0].isEmpty()){
            Toast.makeText(requireContext(),"Empty fields, fill them!",Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email[0], password[0])
                    .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                //reference to the storage database, to store the photo of the user
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(user.getUid());
                                Toast.makeText(requireContext(),"Succefully Created the user",Toast.LENGTH_SHORT).show();

                                //storing to the realtime database
                                DatabaseReference myRef = database.getReference("users");


                                //uploading the photo to the storage database
                                //if the user didn't chose any photo then take the default photo
                                if(imageTaken==null ){
                                    imageTaken =Uri.parse("android.resource://il.movies.application/"+R.drawable.blank_user_photo);
                                }

                                Data newObject = new Data(user.getUid().toString(), email[0], password[0], phone[0], name[0],imageTaken.toString(),isPrivate);
                                UploadTask uploadTask = storageRef.putFile(imageTaken);
                                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                                            // Move to the next fragment after the upload is successful
                                            progressBar.setVisibility(View.INVISIBLE);
                                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                                            navController.navigate(R.id.action_register_to_introduction);
                                        } else {
                                            // If upload fails, display an error message
                                            Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                //setting the document inside my real database firestore
                                myRef.child(user.getUid()).setValue(newObject);
//                                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
//                                navController.navigate(R.id.action_register_to_introduction);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(requireContext(), "Couldn't make the user.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
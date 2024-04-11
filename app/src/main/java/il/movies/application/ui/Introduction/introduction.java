package il.movies.application.ui.Introduction;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import il.movies.application.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link introduction#newInstance} factory method to
 * create an instance of this fragment.
 */
public class introduction extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button btn;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    ImageView imagePerson;
    String currentId;

    public introduction() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment introduction.
     */
    // TODO: Rename and change types and number of parameters
    public static introduction newInstance(String param1, String param2) {
        introduction fragment = new introduction();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_introduction,container,false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser current = mAuth.getCurrentUser();
        if(current!=null){
            currentId = current.getUid();
            setPictureUser(currentId);
        }
        storage = FirebaseStorage.getInstance();
        btn = view.findViewById(R.id.next_intro_btn);
        imagePerson = view.findViewById(R.id.image_person);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_introduction_to_home_screen_fragment);
            }
        });
        //Logging out from the app
        Button logoutBtn = (Button)view.findViewById(R.id.intro_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutBtn(view);
            }
        });

        return view;
    }

    public void logoutBtn(View view) {
        mAuth.signOut();
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.action_introduction_to_login);
    }

    public void setPictureUser(String currentId){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(currentId);
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(requireActivity()).load(uri).into(imagePerson);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(requireActivity(),"Failed to load the user's picture",Toast.LENGTH_SHORT).show();
                Uri defaultUri = Uri.parse("android.resource://il.movies.application/"+R.drawable.blank_user_photo);
                Glide.with(requireActivity()).load(defaultUri).into(imagePerson);
            }
        });
    }
}
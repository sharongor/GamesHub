package il.movies.application.ui.profiles;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import il.movies.application.R;
import il.movies.application.adapters.GamesAdapters;
import il.movies.application.adapters.ProfilesAdapter;
import il.movies.application.adapters.UserLikedGamesAdapter;
import il.movies.application.data.Data;
import il.movies.application.ui.favorites.favorites;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link search_profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class search_profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    String currentUserId;
    String userTyped="";
    AppCompatEditText text;
    ImageButton iconSearch;
    String clickedId;
    List<Data> list= new ArrayList<Data>();

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private LinearLayoutManager likedLayoutManager;
    private ProfilesAdapter adapter;
    private UserLikedGamesAdapter likedAdapter;
    private RecyclerView userLikedRecycler;
    private Boolean privacy = false;

    public search_profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment search_profile.
     */
    // TODO: Rename and change types and number of parameters
    public static search_profile newInstance(String param1, String param2) {
        search_profile fragment = new search_profile();
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

        View view = inflater.inflate(R.layout.fragment_search_profile,container,false);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser current = mAuth.getCurrentUser();
        if(current!=null){
            currentUserId = current.getUid();
        }

        recyclerView =  view.findViewById(R.id.profiles_rv);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        text = view.findViewById(R.id.text_search);
        iconSearch = view.findViewById(R.id.search_btn);

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userTyped = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //when clicking on the search button get the user profiles to show if anything found
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userTyped.isEmpty()){
                    Toast.makeText(requireContext(),"Enter the name",Toast.LENGTH_SHORT).show();
                }
                else {
                    //getting the corresponding profiles with the text that the user has searched for
                    userProfiles(userTyped, new OnGetProfilesListener() {
                        @Override
                        public void getProfiles(List<Data> list2) {
                            list = list2;
                            if(list.isEmpty()){
                                Toast.makeText(requireContext(),"No users found!",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                hideKeyboard(iconSearch);
                            }
                            adapter = new ProfilesAdapter(list, requireContext(), new ProfilesAdapter.userClicked() {
                                @Override
                                public void user(Data data) {
                                    if(privacy){
                                        Toast.makeText(requireContext(),"User has declined to show his favourites",Toast.LENGTH_SHORT).show();
                                    }
                                    //user accepted to show his favourites
                                    else {
                                        clickedId=data.id;
                                        //get the favourites for the user that was clicked
                                        getUserFavorites(clickedId, new OnGetFavoritesListener() {
                                            @Override
                                            public void onGetFavorites(List<String> favorites) {
                                                // Inflate the custom layout
                                                View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_liked_games, null);

                                                if(favorites.isEmpty()){
                                                    TextView title = dialogView.findViewById(R.id.dialog_favourites_title);
                                                    title.setText("Empty list");
                                                }
                                                else if(favorites.get(0).isEmpty()){
                                                    TextView title = dialogView.findViewById(R.id.dialog_favourites_title);
                                                    title.setText("Empty list");
                                                }
                                                else {
                                                    //instantiate the recycler for the favourites for the specific user
                                                    userLikedRecycler = dialogView.findViewById(R.id.user_liked_rv);
                                                    likedLayoutManager = new LinearLayoutManager(requireContext());
                                                    userLikedRecycler.setLayoutManager(likedLayoutManager);
                                                    userLikedRecycler.setItemAnimator(new DefaultItemAnimator());
                                                    likedAdapter = new UserLikedGamesAdapter(favorites);
                                                    userLikedRecycler.setAdapter(likedAdapter);
                                                }
                                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                                builder.setView(dialogView);
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }

                                            @Override
                                            public void onError(String errorMessage) {
                                                Toast.makeText(requireContext(),errorMessage,Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });





        //hiding the keyboard when touching any where on the screen
        recyclerView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(recyclerView);
                return false;
            }
        });

        return view;
    }



    public interface OnGetFavoritesListener {
        void onGetFavorites(List<String> favorites);
        void onError(String errorMessage);
    }
    public void getUserFavorites(String userId, final OnGetFavoritesListener listener) {
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");

        favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> favoritesList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String favorite = snapshot.getValue(String.class);
                    if (favorite != null) {
                        favoritesList.add(favorite);
                    }
                }
                // Pass the favoritesList to the listener
                listener.onGetFavorites(favoritesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                listener.onError(databaseError.getMessage());
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    interface OnGetProfilesListener{
        void getProfiles(List<Data> list2);
        void onError(String errorMessage);
    }

    public void userProfiles(String searchedText, final OnGetProfilesListener listener){
        List<Data> profiles = new ArrayList<Data>();
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("users");
        favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data: snapshot.getChildren()){
                    String name = data.child("name").getValue(String.class);
                    if(name.toLowerCase().trim().contains(searchedText.toLowerCase().trim())){
                        String id = data.child("id").getValue(String.class);
                        String email = data.child("email").getValue(String.class);
                        String pass = data.child("pass").getValue(String.class);
                        String phone = data.child("phone").getValue(String.class);
                        String uri = data.child("profilePicture").getValue(String.class);
                        privacy = data.child("isPrivate").getValue(Boolean.class);
                        Map<String, String> favoritesMap = new HashMap<>();
                        DataSnapshot favoritesSnapshot = data.child("favorites");
                        for (DataSnapshot favoriteSnapshot : favoritesSnapshot.getChildren()) {
                            String key = favoriteSnapshot.getKey();
                            String value = favoriteSnapshot.getValue(String.class);
                            if (value != null) {
                                favoritesMap.put(key, value);
                            }
                        }

                        Data profile = new Data(id, email, pass, phone, name,uri,privacy);
                        profile.favorites = new ArrayList<>(favoritesMap.values());
                        profiles.add(profile);
                    }
                }
                listener.getProfiles(profiles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.getMessage());
            }
        });
    }
}
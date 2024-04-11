package il.movies.application.ui.HomeScreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import il.movies.application.R;
import il.movies.application.ui.AllGames.games_fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home_screen_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home_screen_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    String currentId;
    private List<String> userFavorites;

    public home_screen_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home_screen_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static home_screen_fragment newInstance(String param1, String param2) {
        home_screen_fragment fragment = new home_screen_fragment();
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
        View view = inflater.inflate(R.layout.fragment_home_screen_fragment,container,false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            currentId = currentUser.getUid();
            getUserFavorites(currentId,new OnGetFavoritesListener(){
                @Override
                public void onGetFavorites(List<String> favorites) {
                    userFavorites = favorites;
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(),errorMessage,Toast.LENGTH_SHORT).show();
                }
            });
        }

        //clicking to see the favourites
        view.findViewById(R.id.favourites_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("favourites", (ArrayList<String>) userFavorites);
                Navigation.findNavController(view).navigate(R.id.action_home_screen_fragment_to_favorites,bundle);
            }
        });



        view.findViewById(R.id.home_companies_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_home_screen_fragment_to_publishers_fragment);
            }
        });

        view.findViewById(R.id.home_games_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_home_screen_fragment_to_games_fragment);
            }
        });


        view.findViewById(R.id.more_info_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creating a variable for our bottom sheet dialog.
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());

                // passing a layout file for our bottom sheet dialog.
                View layout = LayoutInflater.from(getContext()).inflate(R.layout.bottom_info_dialog ,null );
                // passing our layout file to our bottom sheet dialog.
                bottomSheetDialog.setContentView(layout);

                // below line is to set our bottom sheet dialog as cancelable.
                bottomSheetDialog.setCancelable(false);

                // below line is to set our bottom sheet cancelable.
                bottomSheetDialog.setCanceledOnTouchOutside(true);

                // below line is to display our bottom sheet dialog.
                bottomSheetDialog.show();
            }
        });



        view.findViewById(R.id.profiles_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_home_screen_fragment_to_search_profile);
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
        navController.navigate(R.id.action_home_screen_fragment_to_login);
    }

    //interface to get the list of favorites for each user. need to wait for the result and to pass it out to the fragment inside the oncreate above...
    public interface OnGetFavoritesListener {
        void onGetFavorites(List<String> favorites);
        void onError(String errorMessage);
    }

    //getting the favorites list of the current user logged in to the database
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
}
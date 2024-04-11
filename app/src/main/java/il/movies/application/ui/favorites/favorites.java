package il.movies.application.ui.favorites;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
import il.movies.application.adapters.LikedGamesAdapter;
import il.movies.application.models.AllGamesExist;
import il.movies.application.ui.AllCompanies.companies_games_fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link favorites#newInstance} factory method to
 * create an instance of this fragment.
 */
public class favorites extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    String currentUserId;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<String> userFavorites;

    private LikedGamesAdapter adapter;

    String currentId;

    public favorites() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment favorites.
     */
    // TODO: Rename and change types and number of parameters
    public static favorites newInstance(String param1, String param2) {
        favorites fragment = new favorites();
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

        View view = inflater.inflate(R.layout.fragment_favorites,container,false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            currentId = currentUser.getUid();
        }
        recyclerView =  view.findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //getting the bundle from the games fragment or from the games_company_fragment
        Bundle bundle = getArguments();
        if(bundle!=null){
            ArrayList<String> stringList = bundle.getStringArrayList("favourites");
            if(stringList!=null){
                userFavorites = stringList;
            }
        }
        if(currentUser!=null){
            //getting the list of favorites for the current user logged in to the app
            currentUserId = currentUser.getUid();
            getUserFavorites(currentUserId, new OnGetFavoritesListener() {
                @Override
                public void onGetFavorites(List<String> favorites) {

                    userFavorites = favorites;
                    if(userFavorites.isEmpty()){
                        Toast.makeText(requireContext(),"No favourites found!",Toast.LENGTH_SHORT).show();
                    }
                    else if(userFavorites.get(0).equals("") ||userFavorites.get(0).isEmpty() ){
                        Toast.makeText(requireContext(),"No favourites found!",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        adapter = new LikedGamesAdapter(userFavorites, new LikedGamesAdapter.favoriteClicked() {
                            @Override
                            public void onItemClicked(String element) {
                                //deleting the specific favourite from the list of favourites
                                deleteFavourite(element,currentUserId);
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(),errorMessage,Toast.LENGTH_SHORT).show();
                }
            });
        }

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

    public void deleteFavourite(String favourite,String userId){
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");
        // Get the current list of favorites from the database
        favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the list exists and is not null
                if (dataSnapshot.exists()) {
                    userFavorites.clear();
                    // Iterate through the list to find and remove the specific string
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String value = snapshot.getValue(String.class);
                        if(value!=null && !value.equals(favourite)){
                            userFavorites.add(value);
                        }
                        if (value != null && value.equals(favourite)) {
                            // Remove the specific string from the list
                            snapshot.getRef().removeValue();
                            //Navigation.findNavController(requireView()).navigate(R.id.action_favorites_to_games_fragment);
                        }
                    }
                    //update the adapter with the new userFavourites list so the UI will be updated accordingly
                    adapter.setUserFavorites(userFavorites);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
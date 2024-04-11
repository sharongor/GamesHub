package il.movies.application.ui.AllCompanies;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import il.movies.application.R;
import il.movies.application.adapters.GamesAdapters;
import il.movies.application.models.AllGamesExist;
import il.movies.application.services.ServiceAllGames;
import il.movies.application.services.ServiceGamesForCompany;
import il.movies.application.ui.AllGames.games_fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link companies_games_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class companies_games_fragment extends Fragment implements ServiceGamesForCompany.OnAllGamesLoadedListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private GamesAdapters adapter;
    private ProgressBar progressBar;

    private String id="";
    private AppCompatEditText searchedText;

    private String searchedVar="";

    private ImageButton imageBtn;

    String selectedItemSpinner = "";

    private ArrayList<AllGamesExist> myArr = new ArrayList<>();

    private FirebaseAuth mAuth;
    String currentUserId;

    private List<String> userFavorites;

    //Needed for when pressing the favourites button and returning to the fragment to actually load the games from the service once again


    public companies_games_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment companies_games_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static companies_games_fragment newInstance(String param1, String param2) {
        companies_games_fragment fragment = new companies_games_fragment();
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
    public void onResume() {
        super.onResume();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_companies_games_fragment,container,false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            //getting the list of favorites for the current user logged in to the app
            currentUserId = currentUser.getUid();
            getUserFavorites(currentUserId, new OnGetFavoritesListener() {
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


        searchedText = view.findViewById(R.id.text_search);

        Spinner spinner = view.findViewById(R.id.spinner);
        String[] values = {getString(R.string.name_Capital) ,  getString(R.string.release_date), getString(R.string.rating),
                getString(R.string.available_platforms), getString(R.string.genres), getString(R.string.stores)};
        // Create an ArrayAdapter using the array of values and default layout
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, values);

        // Specify the layout to use when the list of choices appears
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach the adapter to the spinner
        spinner.setAdapter(adapterSpinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemSpinner = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedItemSpinner = "Name";
            }
        });

        imageBtn = view.findViewById(R.id.search_btn);

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.equals(searchedVar, "")){
                    Toast.makeText(requireContext(),"You didn't enter anything",Toast.LENGTH_SHORT).show();
                }
                else {
                    // Hide the keyboard
                    hideKeyboard(imageBtn);
                    myArr = listSearch(myArr,searchedVar,selectedItemSpinner);
                    adapter.setList(myArr);
                    recyclerView.setAdapter(adapter);
                }
            }
        });

        searchedText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchedVar = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //getting the arguments from the All companies screen( the name of the company )

        if(getArguments()!=null){
            id = getArguments().getString("name");
        }


        //The id that's coming from the previous page(the companies page)
        if(id!=""){
            recyclerView =  view.findViewById(R.id.rv);
            layoutManager = new LinearLayoutManager(requireContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            //hiding the keyboard if it opened when clicking anywhere on the recycler
            recyclerView.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(recyclerView);
                    return false;
                }
            });

            progressBar =view.findViewById(R.id.all_games_progress_bar);
            //ArrayList<AllGamesExist> arr= ServiceAllGames.getArrGames();
            //notifying to the service that this fragment is going to listen when the data fetch is done.
            progressBar.setVisibility(View.VISIBLE);
            ServiceGamesForCompany.getAllGames(id,this);
        }

        searchedText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchedText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Action to be performed when "Done" is pressed
                    // You can put your logic here
                    if(searchedVar==""){
                        Toast.makeText(requireContext(),getString(R.string.you_didn_t_enter_anything),Toast.LENGTH_SHORT).show();
                    }
                    else {
                        myArr = listSearch(myArr,searchedVar,selectedItemSpinner);
                        adapter.setList(myArr);
                        recyclerView.setAdapter(adapter);
                    }
                    hideKeyboard(v);
                    return true; // Return true to consume the event
                }
                return false; // Return false to let the system handle the event
            }
        });
        return view;
    }

    // Method to hide the soft keyboard
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        searchedVar="";
        searchedText.setText("");
    }


    private ArrayList<AllGamesExist> listSearch(ArrayList<AllGamesExist> myArr, String searchedVar,String spinnerItemSelected) {
        searchedVar = searchedVar.toLowerCase();
        ArrayList<AllGamesExist> newList = new ArrayList<>();
        if(spinnerItemSelected.equals(getString(R.string.name_Capital))) {
            for (AllGamesExist obj : myArr) {
                if (obj.getName().toLowerCase().contains(searchedVar)) {
                    newList.add(obj);
                }
            }
        }
        //{"Name", "Release Date", "Rating", "Platforms", "Genres", "Stores"};
        else if (spinnerItemSelected.equals(getString(R.string.release_date))){
            for (AllGamesExist obj : myArr) {
                if (obj.getDateReleased().toLowerCase().contains(searchedVar)) {
                    newList.add(obj);
                }
            }
        }
        else if (spinnerItemSelected.equals(getString(R.string.rating))) {
            for (AllGamesExist obj : myArr) {
                String rating = String.valueOf(obj.getRating()); // Convert rating to string
                String[] parts = rating.split("\\."); // Split the rating into parts using dot as separator
                if (parts.length > 0 && parts[0].equals(searchedVar)) {
                    newList.add(obj);
                }
            }
        }
        else if(spinnerItemSelected.equals(getString(R.string.available_platforms))){
            for (AllGamesExist obj : myArr) {
                for(String s : obj.getPlatforms()){
                    if(s.toLowerCase().contains(searchedVar)){
                        newList.add(obj);
                    }
                }
            }
        }
        else if(spinnerItemSelected.equals(getString(R.string.genres))){
            for (AllGamesExist obj : myArr) {
                for(String s : obj.getGenres()){
                    if(s.toLowerCase().contains(searchedVar)){
                        newList.add(obj);
                    }
                }
            }
        }

        else if(spinnerItemSelected.equals(getString(R.string.stores))){
            for (AllGamesExist obj : myArr) {
                for(String s : obj.getStores()){
                    if(s.toLowerCase().contains(searchedVar)){
                        newList.add(obj);
                    }
                }
            }

        }
        for(int i=0;i<myArr.size();i++){
            AllGamesExist a1 =myArr.get(i);
            if(!newList.contains(a1)){
                newList.add(a1);
            }
        }
        return newList;
    }

    @Override
    public void onAllGamesLoaded(ArrayList<AllGamesExist> allGames) {
        //checking if the fragment is connected to any activity, if not then don't execute it.
        //When going back before the screen could actually load the api, it may try to update UI related things
        //while the fragment has already been destroyed. it makes sure it won't crash.
        if(!isAdded()){
            return;
        }
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                myArr.clear();
                myArr = allGames;
                progressBar.setVisibility(View.INVISIBLE);

                adapter = new GamesAdapters(myArr, requireContext(),userFavorites,new GamesAdapters.favoriteClicked(){
                    @Override
                    public void onItemClicked(AllGamesExist element) {
                        String name = element.getName();
                        //adding the game that the user has clicked to the list of favorites
                        addFavoriteGame(currentUserId,name);
                    }
                } );
                recyclerView.setAdapter(adapter);
            }
        });
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

    public void addFavoriteGame(String userId, String gameName){
        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites");

        // Check if favorites list exists
        favoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get the existing favorites list
                    List<String> favorites = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String favorite = dataSnapshot.getValue(String.class);
                        if (favorite != null && !favorite.isEmpty()) {
                            favorites.add(favorite);
                        }
                    }

                    // If favorites list is empty or its first element is "", set the list to contain only the new gameName
                    if (favorites.isEmpty() || favorites.get(0).equals("")) {
                        favorites.clear();
                        favorites.add(gameName);
                    } else {
                        // Otherwise, add the new gameName to the existing list
                        favorites.add(gameName);
                    }

                    // Update the favorites field in the database
                    favoritesRef.setValue(favorites);
                } else {
                    // If favorites list doesn't exist, create a new list containing only the new gameName
                    List<String> favorites = new ArrayList<>();
                    favorites.add(gameName);
                    favoritesRef.setValue(favorites);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
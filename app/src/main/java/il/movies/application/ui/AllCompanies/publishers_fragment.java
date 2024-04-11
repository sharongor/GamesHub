package il.movies.application.ui.AllCompanies;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

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

import java.util.ArrayList;
import java.util.Objects;

import il.movies.application.R;
import il.movies.application.adapters.CompaniesAdapter;
import il.movies.application.models.State;
import il.movies.application.services.DataServiceCompanies;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link publishers_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class publishers_fragment extends Fragment implements DataServiceCompanies.OnAllGamesLoadedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CompaniesAdapter adapter = null;

    private ProgressBar progressBar;
    private ArrayList<State> myArr = new ArrayList<>();

    private AppCompatEditText searchedText;

    private String searchedVar="";

    private ImageButton imageBtn;

    String selectedItemSpinner = "";

    private ArrayList<State> myNewArr = new ArrayList<>();

    public publishers_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment publishers_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static publishers_fragment newInstance(String param1, String param2) {
        publishers_fragment fragment = new publishers_fragment();
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
        View view = inflater.inflate(R.layout.fragment_publishers_fragment,container,false);

        searchedText = view.findViewById(R.id.text_search);

        Spinner spinner = view.findViewById(R.id.spinner);
        String[] values = {"Name", "Game Count"};
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
                if(searchedVar.isEmpty()){
                    Toast.makeText(requireContext(),"You didn't enter anything",Toast.LENGTH_SHORT).show();
                }
                else {
                    // Hide the keyboard
                    hideKeyboard(imageBtn);
                    myNewArr = listSearch(myArr,searchedVar,selectedItemSpinner);
                    adapter =new CompaniesAdapter(myNewArr, requireContext(), new CompaniesAdapter.ItemListener() {
                        @Override
                        public void onItemClicked(int index) {
                            Toast.makeText(requireContext(),myNewArr.get(index).getName(),Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle("Choose between description or company's games")
                                    .setMessage("You have the option to choose between more details about the company or to see the company's games");
                            // Set positive button
                            builder.setPositiveButton("Games", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Do something when OK button is clicked
                                    String companyName = ""+myNewArr.get(index).getId();
                                    Bundle bundleName = new Bundle();
                                    bundleName.putString("name",companyName);
                                    Navigation.findNavController(view).navigate(R.id.action_publishers_fragment_to_companies_games_fragment,bundleName);
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            });
                            builder.setNegativeButton("Extra Details", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String companyId = ""+myNewArr.get(index).getId();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("id",companyId);
                                    Navigation.findNavController(view).navigate(R.id.action_publishers_fragment_to_companies_Description,bundle);
                                    dialog.dismiss();
                                }
                            });
                            // Create the dialog
                            AlertDialog alertDialog = builder.create();

                            // Show the dialog
                            alertDialog.show();
                        }
                    });
                    recyclerView.setAdapter(adapter);


                    adapter.setList(myNewArr);
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

        recyclerView =  view.findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        progressBar= view.findViewById(R.id.all_companies_progress_bar);
        DataServiceCompanies.getAllCompanies(this);





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

    private ArrayList<State> listSearch(ArrayList<State> myArr, String searchedVar, String spinnerItemSelected) {
        searchedVar = searchedVar.toLowerCase();
        ArrayList<State> newList = new ArrayList<>();
        if(spinnerItemSelected.equals("Name")) {
            for (State obj : myArr) {
                if (obj.getName().toLowerCase().contains(searchedVar)) {
                    newList.add(obj);
                }
            }
        }
        else if (spinnerItemSelected.equals("Game Count")) {
            int searchedCount = Integer.parseInt(searchedVar);
            for (State obj : myArr) {
                if (obj.getGames_count() >= searchedCount) {
                    newList.add(obj);
                }
            }
        }

        for(int i=0;i<myArr.size();i++){
            State a1 =myArr.get(i);
            if(!newList.contains(a1)){
                newList.add(a1);
            }
        }
        return newList;
    }


    @Override
    public void onAllGamesLoaded(ArrayList<State> allCompanies) {
        //checking if the fragment is connected to any activity, if not then don't execute it.
        //When going back before the screen could actually load the api, it may try to update UI related things
        //while the fragment has already been destroyed. it makes sure it won't crash.
        if(!isAdded()){
            return;
        }
        // Update your RecyclerView adapter with the fetched data on the main/UI thread
        requireActivity().runOnUiThread(new Runnable() {
            final View view = getView();
            @Override
            public void run() {
                if(!isAdded()){
                    return;
                }
                myArr.clear();
                myArr.addAll(allCompanies);
                progressBar.setVisibility(View.INVISIBLE);
                adapter =new CompaniesAdapter(myArr, requireContext(), new CompaniesAdapter.ItemListener() {
                    @Override
                    public void onItemClicked(int index) {
                        Toast.makeText(requireContext(),myArr.get(index).getName(),Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Choose between description or company's games")
                                .setMessage("You have the option to choose between more details about the company or to see the company's games");
                        // Set positive button
                        builder.setPositiveButton("Games", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do something when OK button is clicked
                                String companyName = ""+myArr.get(index).getId();
                                Bundle bundleName = new Bundle();
                                bundleName.putString("name",companyName);
                                Navigation.findNavController(view).navigate(R.id.action_publishers_fragment_to_companies_games_fragment,bundleName);
                                dialog.dismiss(); // Dismiss the dialog
                            }
                        });
                        builder.setNegativeButton("Extra Details", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String companyId = ""+myArr.get(index).getId();
                                Bundle bundle = new Bundle();
                                bundle.putString("id",companyId);
                                Navigation.findNavController(view).navigate(R.id.action_publishers_fragment_to_companies_Description,bundle);
                                dialog.dismiss();
                            }
                        });
                        // Create the dialog
                        AlertDialog alertDialog = builder.create();

                        // Show the dialog
                        alertDialog.show();
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
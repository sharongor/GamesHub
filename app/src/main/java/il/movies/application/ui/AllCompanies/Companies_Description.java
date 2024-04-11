package il.movies.application.ui.AllCompanies;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import il.movies.application.R;
import il.movies.application.models.SpecificCompanyModel;
import il.movies.application.services.ServiceSpecificCompany;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Companies_Description#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Companies_Description extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String id = "";
    TextView nameCompany;
    TextView numberOfGames;
    TextView description;

    public Companies_Description() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Companies_Description.
     */
    // TODO: Rename and change types and number of parameters
    public static Companies_Description newInstance(String param1, String param2) {
        Companies_Description fragment = new Companies_Description();
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
        View view = inflater.inflate(R.layout.fragment_companies__description,container,false);
        nameCompany = view.findViewById(R.id.name_company);
        numberOfGames = view.findViewById(R.id.games_count);
        description = view.findViewById(R.id.company_description);

        if(getArguments()!=null){
            id = getArguments().getString(getString(R.string.id));
        }

        if(id!=""){
            SpecificCompanyModel specific = ServiceSpecificCompany.specificCompany(id);
            Glide.with(requireContext()).load(specific.getImage_background()).into((ImageView) view.findViewById(R.id.imageCompany));
            nameCompany.setText( specific.getName());
            numberOfGames.setText(getString(R.string.total_games_developed) + specific.getGame_count());

            // Remove HTML tags from the string
            String output = specific.getDescription().replaceAll("\\<.*?\\>", "");

            // Replace \n with an empty string
            output = output.replaceAll("\\\\n", "");
            output = output.replace("&#39;","'");
            description.setText(output);
        }
        return view;
    }
}
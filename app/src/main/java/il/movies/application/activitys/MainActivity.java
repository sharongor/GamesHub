package il.movies.application.activitys;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import il.movies.application.R;
import il.movies.application.adapters.CompaniesAdapter;
import il.movies.application.models.State;
import il.movies.application.services.DataServiceCompanies;

public class MainActivity extends AppCompatActivity {

//    private RecyclerView recyclerView;
//    private LinearLayoutManager layoutManager;
//    private CompaniesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//        recyclerView = findViewById(R.id.rv);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        ArrayList<State> arr= DataServiceCompanies.getArrState();
//        adapter =new CompaniesAdapter(arr, this, new CompaniesAdapter.ItemListener() {
//            @Override
//            public void onItemClicked(int index) {
//                Toast.makeText(MainActivity.this,arr.get(index).getList().get(0).getNameGame().toString(),Toast.LENGTH_SHORT).show();
//                //TODO navigate to other fragment with the index passed here
//            }
//        });
//        recyclerView.setAdapter(adapter);
    }
}
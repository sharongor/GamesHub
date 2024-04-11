package il.movies.application.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import il.movies.application.R;
import il.movies.application.models.State;

public class CompaniesAdapter extends RecyclerView.Adapter<CompaniesAdapter.MyViewHolder> {

    public interface ItemListener{
        void onItemClicked(int index);
    }

    public void setList(ArrayList<State> list){
        this.dataset = list;
        notifyDataSetChanged();
    }


    ArrayList<State> dataset;
    Context context;
    private static ItemListener callback;


    public CompaniesAdapter(ArrayList<State> list, Context context,ItemListener callback){
        this.dataset = list;
        this.context = context;
        this.callback = callback;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView name;
        TextView game_count;
        public MyViewHolder(@NonNull View itemView)  {
            super(itemView);
            itemView.setOnClickListener(this);
            image = itemView.findViewById(R.id.imageCompany);
            name = itemView.findViewById(R.id.name_company);
            game_count = itemView.findViewById(R.id.games_count);
        }

        @Override
        public void onClick(View v) {
            callback.onItemClicked(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_company_layout,parent,false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String game_counter =  dataset.get(position).getGames_count()+"";
        holder.name.setText( dataset.get(position).getName());
        holder.game_count.setText(context.getString(R.string.number_of_games) + game_counter);
        Glide.with(context).load(dataset.get(position).getImage()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
